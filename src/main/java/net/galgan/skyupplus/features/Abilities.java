package net.galgan.skyupplus.features;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.galgan.skyupplus.config.SUPConfig;
import net.galgan.skyupplus.mixin.BossBarAccessor;
import net.galgan.skyupplus.util.ServerRestrictor;
import net.galgan.skyupplus.util.Sound;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Abilities {

    public static List<Component> body = new ArrayList<>();
    public static Component bossBarMessage = Component.empty();
    public static long lastPlugActivation = -1L;

    record Line(String ability, int time) {}

    public enum AbilityType {
        WIERTLO("\uF011 Wiertło", () -> SUPConfig.get().abilities.playAbilitiesSound),
        PILA("\uF012 Piła", () -> SUPConfig.get().abilities.playAbilitiesSound),
        PLUG("\uF013 Pług", () -> SUPConfig.get().abilities.playAbilitiesSound),
        NAWALNICA("\uF014 Nawałnica", () -> SUPConfig.get().abilities.playAbilitiesSound),
        ROZBIORKA("\uF015 Rozbiórka", () -> SUPConfig.get().abilities.playAbilitiesSound),
        SIECI_RYBACKIE("\uF016 Sieci rybackie", () -> SUPConfig.get().abilities.playAbilitiesSound),
        ESKALACJA("\uF017 Eskalacja", () -> SUPConfig.get().abilities.playAbilitiesSound);

        final String BOSS_BAR_PREFIX;
        final BooleanSupplier ENABLED;

        AbilityType(String BOSS_BAR_PREFIX, BooleanSupplier ENABLED) {
            this.BOSS_BAR_PREFIX = BOSS_BAR_PREFIX;
            this.ENABLED = ENABLED;
        }

        public static AbilityType fromMessage(String message) {
            for (AbilityType type : values()) {
                if (message.contains(type.BOSS_BAR_PREFIX)) return type;
            }

            return null;
        }
    }

    public static final class AbilityState {
        int timeRemaining = Integer.MIN_VALUE;
        boolean isActive = false;
        int lastSoundTime = Integer.MIN_VALUE;
    }

    public static final EnumMap<AbilityType, AbilityState> states = new EnumMap<>(Abilities.AbilityType.class);

    static {
        for (Abilities.AbilityType type : Abilities.AbilityType.values()) {
            states.put(type, new Abilities.AbilityState());
        }
    }

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (!ServerRestrictor.isAllowed()) return;
            if (mc.player == null) return;

            // Get the current time in seconds
            long currentTime = System.currentTimeMillis() / 1000;

            // Reset all active flags (except for plug if it was activated less than 5 seconds ago)
            for (AbilityType type : AbilityType.values()) {
                AbilityState state = states.get(type);

                if (type == AbilityType.PLUG && currentTime - lastPlugActivation < 5) {
                    state.isActive = true;
                    continue;
                }

                state.isActive = false;
                state.timeRemaining = Integer.MIN_VALUE;
            }

            // Check if there is a boss bar
            BossHealthOverlay hud = mc.gui.hud.getBossOverlay();

            //Loop through every boss bar
            for (LerpingBossEvent bar : ((BossBarAccessor) hud).getBossBarsMap().values()) {
                String title = bar.getName().getString();

                //Check if its name is on the list
                for (AbilityType type : AbilityType.values()) {
                    if (!title.startsWith(type.BOSS_BAR_PREFIX)) continue;

                    //Set all the needed perimeters if the name is correct
                    AbilityState state = states.get(type);
                    state.isActive = true;
                    state.timeRemaining = getRemainingSeconds(title);

                    if (!type.ENABLED.getAsBoolean() || state.timeRemaining < 0 || state.timeRemaining > 3 || state.lastSoundTime == state.timeRemaining) continue;

                    //Play the countdown sound
                    float pitch = switch (state.timeRemaining) {
                        case 3 -> 0.9f;
                        case 1 -> 1.1f;
                        default -> 1.0f;
                    };

                    Sound.playSound(mc, state.timeRemaining == 0 ? finishedSound() : countdownSound(), SoundSource.MASTER, pitch);
                    state.lastSoundTime = state.timeRemaining;
                }
            }

            // Detect if an ability cooldown has ended
            if (bossBarMessage.getString().startsWith("Prace » Umiejętność ") && SUPConfig.get().abilities.playAbilitiesSound) {
                Sound.playSound(mc, finishedSound(), SoundSource.MASTER, 1.0f);
                bossBarMessage = Component.empty();
            }

            // Detect if dungeon cooldown has ended
            if (bossBarMessage.getString().startsWith("Dungeon » Możesz ponownie zawalczyć w Dungeonie!") && SUPConfig.get().dungeons.playDungeonSound) {
                Sound.playSound(mc, dungeonSound(), SoundSource.MASTER, 1.0f);
                bossBarMessage = Component.empty();
            }

            // Get the body of the cooldown HUD
            List<Component> bodyText = new ArrayList<>();

            if (!SUPConfig.get().abilities.customAbilities) {
                body = bodyText;
                return;
            }

            List<Line> bodyList = List.of(
                    new Line("Pług", states.get(AbilityType.PLUG).timeRemaining),
                    new Line("Piła", states.get(AbilityType.PILA).timeRemaining),
                    new Line("Sieci rybackie", states.get(AbilityType.SIECI_RYBACKIE).timeRemaining),
                    new Line("Rozbiórka", states.get(AbilityType.ROZBIORKA).timeRemaining),
                    new Line("Eskalacja", states.get(AbilityType.ESKALACJA).timeRemaining),
                    new Line("Nawałnica", states.get(AbilityType.NAWALNICA).timeRemaining),
                    new Line("Wiertło", states.get(AbilityType.WIERTLO).timeRemaining)
            );

            // Sort by lowest time remaining
            bodyList.stream()
                    .sorted(Comparator.comparingInt(Line::time))
                    .forEach(line -> {
                        if(line.time >= 0) {
                            bodyText.add(Component.empty()
                                    .append(Component.literal(line.ability + ": ").withStyle(ChatFormatting.GREEN))
                                    .append(Component.literal(String.format("%02d:%02d", line.time / 60, line.time % 60))));
                        }
                    });

            body = bodyText;
        });

        ClientReceiveMessageEvents.GAME.register((Component message, boolean overlay) -> {
            if(!ServerRestrictor.isAllowed()) return;

            String messageString = message.getString();

            if(!overlay && messageString.startsWith("Prace » Przerwano umiejętność")) {
                AbilityType type = AbilityType.fromMessage(messageString);
                if (type != null) {
                    states.get(type).isActive = false;
                }
            }
        });
    }

    private static int getRemainingSeconds(String message) {
        int minutes = 0, seconds = 0;

        Matcher mMin = Pattern.compile("(\\d+)\\s*min").matcher(message);
        if (mMin.find()) minutes = Integer.parseInt(mMin.group(1));

        Matcher mSec = Pattern.compile("(\\d+)\\s*sek").matcher(message);
        if (mSec.find()) seconds = Integer.parseInt(mSec.group(1));

        return minutes * 60 + seconds;
    }

    private static SoundEvent finishedSound() {
        return switch (SUPConfig.get().abilities.abilitiesSound) {
            case BELL -> SoundEvents.BELL_BLOCK;
            case GOAT_HORN -> SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(1).value();
            case WITHER -> SoundEvents.WITHER_SPAWN;
            case CHALLENGE -> SoundEvents.UI_TOAST_CHALLENGE_COMPLETE;
        };
    }

    private static SoundEvent countdownSound() {
        return switch (SUPConfig.get().abilities.countdownSound) {
            case EXP -> SoundEvents.EXPERIENCE_ORB_PICKUP;
            case CLICK -> SoundEvents.NOTE_BLOCK_HAT.value();
            case EIGHT_BIT -> SoundEvents.NOTE_BLOCK_BIT.value();
            case NOTEBLOCK -> SoundEvents.NOTE_BLOCK_BASS.value();
        };
    }

    private static SoundEvent dungeonSound() {
        return switch (SUPConfig.get().dungeons.dungeonSound) {
            case BELL -> SoundEvents.BELL_BLOCK;
            case GOAT_HORN -> SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(1).value();
            case WITHER -> SoundEvents.WITHER_SPAWN;
            case CHALLENGE -> SoundEvents.UI_TOAST_CHALLENGE_COMPLETE;
        };
    }
}