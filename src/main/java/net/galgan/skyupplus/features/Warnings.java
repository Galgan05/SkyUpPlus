package net.galgan.skyupplus.features;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.galgan.skyupplus.config.SUPConfig;
import net.galgan.skyupplus.mixin.TitleAccessor;
import net.galgan.skyupplus.util.ServerRestrictor;
import net.galgan.skyupplus.util.Sound;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.ChatFormatting;

import java.util.Set;

import static net.galgan.skyupplus.features.Abilities.states;


public class Warnings {

    private static boolean playPlugWarning = false;

    public static void register() {

        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (!SUPConfig.get().warnings.togglePlugWarning || !ServerRestrictor.isAllowed() || states.get(Abilities.AbilityType.PLUG).isActive) {
                return InteractionResult.PASS;
            }

            Block block = world.getBlockState(pos).getBlock();

            playPlugWarning = crops.contains(block);

            return InteractionResult.PASS;
        });

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (!ServerRestrictor.isAllowed()) return;

            boolean plugWarning = SUPConfig.get().warnings.togglePlugWarning;
            boolean toolWarning = SUPConfig.get().warnings.toggleToolDurabilityWarning;
            boolean armorWarning = SUPConfig.get().warnings.toggleArmorDurabilityWarning;

            if (!toolWarning && !armorWarning && !plugWarning) return;

            if (mc.player == null) return;

            TitleAccessor title = (TitleAccessor) mc.gui.hud;
            boolean titleActive = title.getTitle() != null || title.getSubtitle() != null;

            if (!titleActive && plugWarning && playPlugWarning) {
                mc.gui.hud.setTitle(Component.literal("UWAGA!").withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
                mc.gui.hud.setSubtitle(Component.literal("Pług nie jest aktywny!"));
                mc.gui.hud.setTimes(5, 10, 5);
                Sound.playSound(mc, warningSound(), SoundSource.MASTER, 1.0f);
                playPlugWarning = false;
                titleActive = true;
            }

            if (!titleActive && toolWarning) {
                int toolThreshold = SUPConfig.get().warnings.toolDurabilityThreshold;

                ItemStack mainHand = mc.player.getItemBySlot(EquipmentSlot.MAINHAND);
                ItemStack offHand = mc.player.getItemBySlot(EquipmentSlot.OFFHAND);

                boolean toolLow = compareDurability(mainHand, toolThreshold) || compareDurability(offHand, toolThreshold);

                if(toolLow) {
                    mc.gui.hud.setTitle(Component.literal("UWAGA!").withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
                    mc.gui.hud.setSubtitle(Component.literal("Niskie użycia narzędzia!"));
                    mc.gui.hud.setTimes(5, 10, 5);
                    Sound.playSound(mc, warningSound(), SoundSource.MASTER, 1.0f);
                    titleActive = true;
                }
            }

            if (!titleActive && armorWarning) {
                int armorThreshold = SUPConfig.get().warnings.armorDurabilityThreshold;

                ItemStack head = mc.player.getItemBySlot(EquipmentSlot.HEAD);
                ItemStack chest = mc.player.getItemBySlot(EquipmentSlot.CHEST);
                ItemStack legs = mc.player.getItemBySlot(EquipmentSlot.LEGS);
                ItemStack feet = mc.player.getItemBySlot(EquipmentSlot.FEET);

                boolean armorLow = compareDurability(head, armorThreshold) || compareDurability(chest, armorThreshold) || compareDurability(legs, armorThreshold) || compareDurability(feet, armorThreshold);

                if(armorLow) {
                    mc.gui.hud.setTitle(Component.literal("UWAGA!").withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
                    mc.gui.hud.setSubtitle(Component.literal("Niskie użycia zbroi!"));
                    mc.gui.hud.setTimes(5, 10, 5);
                    Sound.playSound(mc, warningSound(), SoundSource.MASTER, 1.0f);
                }
            }
        });
    }

    private static final Set<Block> crops = Set.of(
            net.minecraft.world.level.block.Blocks.WHEAT,
            net.minecraft.world.level.block.Blocks.BEETROOTS,
            net.minecraft.world.level.block.Blocks.CARROTS,
            net.minecraft.world.level.block.Blocks.POTATOES,
            net.minecraft.world.level.block.Blocks.NETHER_WART,
            net.minecraft.world.level.block.Blocks.COCOA
    );

    private static boolean compareDurability(ItemStack stack, int durabilityThreshold) {
        if (stack.isEmpty() || !stack.isDamageableItem()) return false;

        int percent = ((stack.getMaxDamage() - stack.getDamageValue()) * 100) / stack.getMaxDamage();

        return percent <= durabilityThreshold;
    }

    private static SoundEvent warningSound() {
        return switch (SUPConfig.get().warnings.warningSound) {
            case PLING -> SoundEvents.NOTE_BLOCK_PLING.value();
            case TUBE -> SoundEvents.NOTE_BLOCK_DIDGERIDOO.value();
            case BASS -> SoundEvents.NOTE_BLOCK_BASS.value();
            case ANVIL -> SoundEvents.ANVIL_LAND;
        };
    }
}
