package net.galgan.skyupplus.config;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.Config;
import io.github.notenoughupdates.moulconfig.annotations.*;
import io.github.notenoughupdates.moulconfig.common.text.StructuredText;
import io.github.notenoughupdates.moulconfig.gui.MoulConfigEditor;
import io.github.notenoughupdates.moulconfig.managed.ManagedConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.galgan.skyupplus.util.HudEditor;
import net.galgan.skyupplus.util.HudElement;

import java.io.File;

public class SUPConfig extends Config {

    private static ManagedConfig<SUPConfig> managed;
    private static boolean openNextTick = false;

    public static void init() {
        File file = FabricLoader.getInstance().getConfigDir().resolve("skyupplus/config.json").toFile();
        managed = ManagedConfig.create(file, SUPConfig.class);
        managed.saveToFile();

        ClientLifecycleEvents.CLIENT_STARTED.register(mc -> {
            if (get().main.hudScale <= 0) {
                get().main.hudScale = Math.min(mc.getWindow().getGuiScale(), 9);
                save();
            }
            if (get().dungeons.dungeonMap.dungeonMapScale <= 0) {
                get().dungeons.dungeonMap.dungeonMapScale = Math.min(mc.getWindow().getGuiScale(), 9);
                save();
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (openNextTick) {
                openNextTick = false;
                managed.openConfigGui();
            }
        });

        ClientLifecycleEvents.CLIENT_STOPPING.register(mc -> save());
    }

    public static SUPConfig get() {
        return managed.getInstance();
    }

    public static void save() {
        managed.saveToFile();
    }

    public static void openGui() {
        openNextTick = true;
    }

    public static MoulConfigEditor<SUPConfig> createEditor() {
        return managed.getEditor();
    }

    @Override
    public void saveNow() {
        save();
    }

    @Override
    public StructuredText getTitle() {
        return StructuredText.of("SkyUP+");
    }

    // --- SHARED ---

    public enum FishingDisplayBehavior {
        FISHING("Trzymając Wędkę"), ALWAYS("Zawsze"), NEVER("Nigdy");
        private final String name;
        FishingDisplayBehavior(String name) { this.name = name; }
        @Override public String toString() { return name; }
    }

    public enum XPCounterDisplayBehavior {
        XP("Zdobywając XP"), ALWAYS("Zawsze"), NEVER("Nigdy");
        private final String name;
        XPCounterDisplayBehavior(String name) { this.name = name; }
        @Override public String toString() { return name; }
    }

    public enum MainSound {
        BELL("Dzwon"), GOAT_HORN("Kozi Róg"), WITHER("Wither"), CHALLENGE("Wyzwanie");
        private final String name;
        MainSound(String name) { this.name = name; }
        @Override public String toString() { return name; }
    }

    public enum CountdownSound {
        EXP("EXP"), CLICK("Klik"), EIGHT_BIT("8-bit"), NOTEBLOCK("Blok Dźwiękowy");
        private final String name;
        CountdownSound(String name) { this.name = name; }
        @Override public String toString() { return name; }
    }

    public enum WarningSound {
        PLING("Pling"), TUBE("Tuba"), BASS("Gitara Basowa"), ANVIL("Kowadło");
        private final String name;
        WarningSound(String name) { this.name = name; }
        @Override public String toString() { return name; }
    }

    public enum DungeonMapType {
        SQUARE("Kwadratowa"), FULL("Cała");
        private final String name;
        DungeonMapType(String name) { this.name = name; }
        @Override public String toString() { return name; }
    }

    // --- CATEGORIES ---

    @Expose
    @Category(name = "Ogólne", desc = "Ogólne")
    public Main main = new Main();

    @Expose
    @Category(name = "Scoreboard", desc = "Scoreboard")
    public Scoreboard scoreboard = new Scoreboard();

    @Expose
    @Category(name = "Czat i Dźwięki", desc = "Czat i Dźwięki")
    public Chat chat = new Chat();

    @Expose
    @Category(name = "Automatyzacja", desc = "Automatyzacja")
    public Automation automation = new Automation();

    @Expose
    @Category(name = "Ostrzeżenia", desc = "Ostrzeżenia")
    public Warnings warnings = new Warnings();

    @Expose
    @Category(name = "Przypominajki", desc = "Przypominajki")
    public Reminders reminders = new Reminders();

    @Expose
    @Category(name = "Skrzynki", desc = "Skrzynki")
    public Crates crates = new Crates();

    @Expose
    @Category(name = "Umiejętności", desc = "Umiejętności")
    public Abilities abilities = new Abilities();

    @Expose
    @Category(name = "Licznik XP", desc = "Licznik XP")
    public XPCounter xpcounter = new XPCounter();

    @Expose
    @Category(name = "Rybak", desc = "Rybak")
    public Fishing fishing = new Fishing();

    @Expose
    @Category(name = "Dungeony", desc = "Dungeony")
    public Dungeons dungeons = new Dungeons();

    // --- MAIN ---

    public static class Main {
        @Expose
        @ConfigOption(name = "Skala HUD", desc = "Zmienia skalę wyświetlania HUD-u moda")
        @ConfigEditorSlider(minValue = 1, maxValue = 9, minStep = 1)
        public int hudScale = 0;

        @Expose
        public HudPosition requirementsPosition = new HudPosition(false, 0f);

        @ConfigOption(name = "Pozycja Zadań/Szamaństwa", desc = "Przenieś wyświetlanie zadań/szamaństwa tam, gdzie ci odpowiada")
        @ConfigEditorButton(buttonText = "Zmień")
        public transient Runnable moveRequirements = () -> HudEditor.open(HudElement.REQUIREMENTS);

        @Expose
        @ConfigOption(name = "Pokazuj Pełne Magazyny", desc = "Podświetla w kontrolerze magazynów pełne i prawie pełne magazyny")
        @ConfigEditorBoolean
        public boolean showFullStorages = true;

        @Expose
        @ConfigOption(name = "Pasek Życia", desc = "Ustawienia customowego paska życia")
        @Accordion
        public HealthBar healthBar = new HealthBar();

        @Expose
        @ConfigOption(name = "Automatyczna Paczka Zasobów", desc = "Automatycznie pobiera i zapisuję paczkę zasobów serwera. Paczka jest automatycznie używana przy każdym włączeniu gry. Ekran pobierania pokaże się tylko wtedy, kiedy dostępna jest nowa wersja paczki.")
        @ConfigEditorBoolean
        public boolean automaticResourcePack = true;

    }

    public static class HealthBar {
        @Expose
        @ConfigOption(name = "Customowy Pasek Życia", desc = "Zamienia serduszka w pasek życia, który dostosowuje się do maxymalnej ilości posiadanego życia")
        @ConfigEditorBoolean
        public boolean customHealthBar = true;

        @Expose
        @ConfigOption(name = "Liczba Życia", desc = "Wyświetla koło paska liczbę życia")
        @ConfigEditorBoolean
        public boolean healthNumber = true;
    }

    // --- SCOREBOARD ---
    public static class Scoreboard {
        @Expose
        @ConfigOption(name = "Customowy Scoreboard", desc = "Zamienia domyślny scoreboard na nowy, zajmujący mniej miejsca")
        @ConfigEditorBoolean
        public boolean customScoreboard = true;

        @Expose
        public HudPosition scoreboardPosition = new HudPosition(true, 0.5f);

        @ConfigOption(name = "Pozycja Scoreboarda", desc = "Przenieś customowy scoreboard tam, gdzie ci odpowiada")
        @ConfigEditorButton(buttonText = "Zmień")
        public transient Runnable moveScoreboard = () -> HudEditor.open(HudElement.SCOREBOARD);
    }


    // --- CHAT ---

    public static class Chat {
        @Expose
        @ConfigOption(name = "Usuń Ogłoszenia", desc = "Usuwa ogłoszenia serwerowe oraz blokuje ich dźwięk")
        @ConfigEditorBoolean
        public boolean disableAnnouncements = false;

        @Expose
        @ConfigOption(name = "Usuń Dźwięk Spawnera", desc = "Usuwa dźwięk wydawany podczas spawnowania się mobów ze spawnera")
        @ConfigEditorBoolean
        public boolean disableSpawnerSound = false;

        @Expose
        @ConfigOption(name = "Usuń Informację o Kucaniu", desc = "Usuwa informację o potrzebie kucnięcia aby zniszczyć niektóre bloki")
        @ConfigEditorBoolean
        public boolean disableCrouchingInfo = false;

        @Expose
        @ConfigOption(name = "Usuń Cudze Elementium", desc = "Usuwa informację o dropie z Elementium otrzymanym przez innych graczy")
        @ConfigEditorBoolean
        public boolean disableForeignElementium = false;

        @Expose
        @ConfigOption(name = "Usuń Informację o AFK", desc = "Usuwa informację o AFK pojawiającą się na chacie jak się nie ruszasz")
        @ConfigEditorBoolean
        public boolean disableAFKInfo = false;

        @Expose
        @ConfigOption(name = "Skracaj Aukcje", desc = "Usuwa niepotrzebne linijki z ogłoszenia o nowej aukcji")
        @ConfigEditorBoolean
        public boolean shortenAuctions = false;
    }

    // --- AUTOMATION ---

    public static class Automation {
        @Expose
        @ConfigOption(name = "Clicker LPM", desc = "Klika lewy przycisk myszki za ciebie. Możesz ustawić klawisz do aktywowania tego ustawienia w menu sterowania")
        @ConfigEditorBoolean
        public boolean leftClickerToggle = false;

        @Expose
        @ConfigOption(name = "Częstotliwość Clickera LPM", desc = "Ustawia częstotliwość clickera LPM w tickach (1 tick = 50ms)")
        @ConfigEditorSlider(minValue = 1, maxValue = 20, minStep = 1)
        public int leftClickInterval = 13;

        @Expose
        @ConfigOption(name = "Clicker PPM", desc = "Klika prawy przycisk myszki za ciebie. Możesz ustawić klawisz do aktywowania tego ustawienia w menu sterowania")
        @ConfigEditorBoolean
        public boolean rightClickerToggle = false;

        @Expose
        @ConfigOption(name = "Częstotliwość Clickera PPM", desc = "Ustawia częstotliwość clickera PPM w tickach (1 tick = 50ms)")
        @ConfigEditorSlider(minValue = 1, maxValue = 20, minStep = 1)
        public int rightClickInterval = 1;

        @Expose
        @ConfigOption(name = "Trzymaj LPM", desc = "Trzyma lewy przycisk myszki za ciebie. Możesz ustawić klawisz do aktywowania tego ustawienia w menu sterowania")
        @ConfigEditorBoolean
        public boolean leftHoldToggle = false;

        @Expose
        @ConfigOption(name = "Trzymaj PPM", desc = "Trzyma prawy przycisk myszki za ciebie. Możesz ustawić klawisz do aktywowania tego ustawienia w menu sterowania")
        @ConfigEditorBoolean
        public boolean rightHoldToggle = false;

        @Expose
        @ConfigOption(name = "Blokada Myszki", desc = "Blokuje obracanie się myszką. Możesz ustawić klawisz do aktywowania tego ustawienia w menu sterowania")
        @ConfigEditorBoolean
        public boolean mouseLockToggle = false;

        @Expose
        @ConfigOption(name = "Uzupełnianie Łapki", desc = "Automatycznie uzupełnia twoją łapkę blokiem, który kładziesz. MMożesz ustawić klawisz do aktywowania tego ustawienia w menu sterowania")
        @ConfigEditorBoolean
        public boolean handRestockToggle = false;
    }

    // --- WARNINGS ---

    public static class Warnings {
        @Expose
        @ConfigOption(name = "Dżwięk Ostrzeżenia", desc = "Odpowiada za to, który dźwięk będzie grany podczas ostrzeżenia")
        @ConfigEditorDropdown
        public WarningSound warningSound = WarningSound.PLING;

        @Expose
        @ConfigOption(name = "Ostrzeżenie o Pługu", desc = "Dodaje ostrzeżenie jeżeli próbujesz zebrać uprawy bez aktywnego pługu")
        @ConfigEditorBoolean
        public boolean togglePlugWarning = false;

        @Expose
        @ConfigOption(name = "Ostrzeżenie o Narzędziach", desc = "Dodaje ostrzeżenie jeżeli ilość użyć trzymanego narzędzia spadnie poniżej progu")
        @ConfigEditorBoolean
        public boolean toggleToolDurabilityWarning = false;

        @Expose
        @ConfigOption(name = "Próg Narzędzi", desc = "Ustawia procentowy próg użyć narzędzi poniżej którego będzie grane ostrzeżenie")
        @ConfigEditorSlider(minValue = 1, maxValue = 100, minStep = 1)
        public int toolDurabilityThreshold = 5;

        @Expose
        @ConfigOption(name = "Ostrzeżenie o Zbroi", desc = "Dodaje ostrzeżenie jeżeli ilość użyć założonej zbroi spadnie poniżej progu")
        @ConfigEditorBoolean
        public boolean toggleArmorDurabilityWarning = false;

        @Expose
        @ConfigOption(name = "Próg Zbroi", desc = "stawia procentowy próg użyć zbroi poniżej którego będzie grane ostrzeżenie")
        @ConfigEditorSlider(minValue = 1, maxValue = 100, minStep = 1)
        public int armorDurabilityThreshold = 5;
    }

    // --- REMINDERS ---

    public static class Reminders {
        @Expose
        public HudPosition remindersPosition = new HudPosition(false, 1f);

        @ConfigOption(name = "Pozycja Przypominajek", desc = "Przenieś przypominajki tam, gdzie ci odpowiadają")
        @ConfigEditorButton(buttonText = "Zmień")
        public transient Runnable moveReminders = () -> HudEditor.open(HudElement.REMINDERS);

        @Expose
        @ConfigOption(name = "Zadanie Codzienne", desc = "Informuje ciebie o dostępności nowego zadania codziennego")
        @ConfigEditorBoolean
        public boolean toggleDaily = false;

        @Expose
        @ConfigOption(name = "Zawody Wędkarskie", desc = "Informuje ciebie o tym kiedy są następne zawody wędkarskie")
        @ConfigEditorBoolean
        public boolean toggleZawody = false;

        @Expose
        @ConfigOption(name = "Eventy", desc = "Informuje ciebie o tym kiedy są następne eventy")
        @ConfigEditorBoolean
        public boolean toggleEventy = false;
    }

    // --- CRATES ---

    public static class Crates {
        @Expose
        public HudPosition cratesPosition = new HudPosition(false, 0.5f);

        @ConfigOption(name = "Pozycja Skrzynek", desc = "Przenieś drop ze skrzynek tam, gdzie ci odpowiada")
        @ConfigEditorButton(buttonText = "Zmień")
        public transient Runnable moveCrates = () -> HudEditor.open(HudElement.CRATES);

        @Expose
        @ConfigOption(name = "Elementium", desc = "Elementium crate drops to track")
        @Accordion
        public Elementium elementium = new Elementium();

        @Expose
        @ConfigOption(name = "Platinum", desc = "Platinum crate drops to track")
        @Accordion
        public Platinum platinum = new Platinum();

        @Expose
        @ConfigOption(name = "Other", desc = "Other drops to track")
        @Accordion
        public Other other = new Other();
    }

    public static class Elementium {
        @Expose @ConfigOption(name = "Karambit", desc = "Włącza statystykę dropu karambita") @ConfigEditorBoolean
        public boolean karambitToggle = true;
        @Expose @ConfigOption(name = "Perun", desc = "Włącza statystykę dropu peruna") @ConfigEditorBoolean
        public boolean perunToggle = true;
        @Expose @ConfigOption(name = "Cymofan", desc = "Włącza statystykę dropu cymofana") @ConfigEditorBoolean
        public boolean cymofanToggle = true;
        @Expose @ConfigOption(name = "Młot Thora", desc = "Włącza statystykę dropu młota thora") @ConfigEditorBoolean
        public boolean mlotThoraToggle = true;
        @Expose @ConfigOption(name = "Urizel", desc = "Włącza statystykę dropu urizela") @ConfigEditorBoolean
        public boolean urizelToggle = true;
        @Expose @ConfigOption(name = "Azada", desc = "Włącza statystykę dropu azady") @ConfigEditorBoolean
        public boolean azadaToggle = true;
        @Expose @ConfigOption(name = "Spinel", desc = "Włącza statystykę dropu spinela") @ConfigEditorBoolean
        public boolean spinelToggle = true;
        @Expose @ConfigOption(name = "Karpiołap", desc = "Włącza statystykę dropu karpiołapa") @ConfigEditorBoolean
        public boolean karpiolapToggle = true;
        @Expose @ConfigOption(name = "Etherica", desc = "Włącza statystykę dropu ethericy") @ConfigEditorBoolean
        public boolean ethericaToggle = true;
        @Expose @ConfigOption(name = "Łuk Legolasa", desc = "Włącza statystykę dropu łuku legolasa") @ConfigEditorBoolean
        public boolean lukLegolasaToggle = true;
        @Expose @ConfigOption(name = "Arbalet", desc = "Włącza statystykę dropu arbaleta") @ConfigEditorBoolean
        public boolean arbaletToggle = true;
        @Expose @ConfigOption(name = "Powrót Odysa", desc = "Włącza statystykę dropu powrotu odysa") @ConfigEditorBoolean
        public boolean powrotOdysaToggle = true;
        @Expose @ConfigOption(name = "Cassis", desc = "Włącza statystykę dropu cassisa") @ConfigEditorBoolean
        public boolean cassisToggle = true;
        @Expose @ConfigOption(name = "Cuirass", desc = "Włącza statystykę dropu cuirassa") @ConfigEditorBoolean
        public boolean cuirassToggle = true;
        @Expose @ConfigOption(name = "Cuissot", desc = "Włącza statystykę dropu cuissota") @ConfigEditorBoolean
        public boolean cuissotToggle = true;
        @Expose @ConfigOption(name = "Cosset", desc = "Włącza statystykę dropu cosseta") @ConfigEditorBoolean
        public boolean cossetToggle = true;
        @Expose @ConfigOption(name = "Maska głębinowca", desc = "Włącza statystykę dropu maski głębinowca") @ConfigEditorBoolean
        public boolean maskaGlebinowcaToggle = true;
        @Expose @ConfigOption(name = "Płaszcz leśnika", desc = "Włącza statystykę dropu płaszu leśnika") @ConfigEditorBoolean
        public boolean plaszczLesnikaToggle = true;
        @Expose @ConfigOption(name = "Nogawice strażaka", desc = "Włącza statystykę dropu nogawic strażaka") @ConfigEditorBoolean
        public boolean nogawiceStrazaka = true;
        @Expose @ConfigOption(name = "Kapcie Lotnika", desc = "Włącza statystykę dropu kapci lotnika") @ConfigEditorBoolean
        public boolean kapcieLotnikaToggle = true;
        @Expose @ConfigOption(name = "Rivendell", desc = "Włącza statystykę dropu rivendella") @ConfigEditorBoolean
        public boolean rivendellToggle = true;
        @Expose @ConfigOption(name = "Impet", desc = "Włącza statystykę dropu impeta") @ConfigEditorBoolean
        public boolean impetToggle = true;
        @Expose @ConfigOption(name = "Phlox", desc = "Włącza statystykę dropu phloxa") @ConfigEditorBoolean
        public boolean phloxToggle = true;
        @Expose @ConfigOption(name = "Majster", desc = "Włącza statystykę dropu majstra") @ConfigEditorBoolean
        public boolean majsterToggle = true;
        @Expose @ConfigOption(name = "Magiczne Wiaderko", desc = "Włącza statystykę dropu magicznego wiaderka") @ConfigEditorBoolean
        public boolean magiczneWiaderkoToggle = true;
        @Expose @ConfigOption(name = "Stat Tracker", desc = "Włącza statystykę dropu stat trackera") @ConfigEditorBoolean
        public boolean statTrackerToggle = true;
        @Expose @ConfigOption(name = "Jajko Niespodzianka", desc = "Włącza statystykę dropu jajka niespodzianki") @ConfigEditorBoolean
        public boolean jajkoNiespodziankaToggle = true;
        @Expose @ConfigOption(name = "Kontroler Magazynów", desc = "Włącza statystykę dropu kontrolera magazynów") @ConfigEditorBoolean
        public boolean kontrolerMagazynowToggle = true;
        @Expose @ConfigOption(name = "Kapelusz pracusia", desc = "Włącza statystykę dropu kapelusza pracusia") @ConfigEditorBoolean
        public boolean kapeluszPracusiaToggle = true;
        @Expose @ConfigOption(name = "Tajemnice SkyUPa", desc = "Włącza statystykę dropu tajemnic skyupa") @ConfigEditorBoolean
        public boolean tajemniceSkyUPaToggle = true;
        @Expose @ConfigOption(name = "Klejnot Kupiecki", desc = "Włącza statystykę dropu klejnota kupieckiego") @ConfigEditorBoolean
        public boolean klejnotKupieckiToggle = true;
    }

    public static class Platinum {
        @Expose @ConfigOption(name = "Klucz Elementium", desc = "Włącza statystykę dropu klicza elementium") @ConfigEditorBoolean
        public boolean elementiumToggle = true;
        @Expose @ConfigOption(name = "Mending", desc = "Włącza statystykę dropu mendingu") @ConfigEditorBoolean
        public boolean mendingToggle = true;
        @Expose @ConfigOption(name = "Blok Szmaragdu", desc = "Włącza statystykę dropu bloku szmaragdu") @ConfigEditorBoolean
        public boolean emeraldBlockToggle = true;
        @Expose @ConfigOption(name = "Blok Złota", desc = "Włącza statystykę dropu bloku złota") @ConfigEditorBoolean
        public boolean goldBlockToggle = true;
        @Expose @ConfigOption(name = "Blok Żelaza", desc = "Włącza statystykę dropu bloku żelaza") @ConfigEditorBoolean
        public boolean ironBlockToggle = true;
        @Expose @ConfigOption(name = "Blok Redstone", desc = "Włącza statystykę dropu bloku redstona") @ConfigEditorBoolean
        public boolean redstoneBlockToggle = true;
        @Expose @ConfigOption(name = "Ziemia", desc = "Włącza statystykę dropu ziemii") @ConfigEditorBoolean
        public boolean dirtToggle = true;
        @Expose @ConfigOption(name = "Blok Trawy", desc = "Włącza statystykę dropu bloku trawy") @ConfigEditorBoolean
        public boolean grassBlockToggle = true;
        @Expose @ConfigOption(name = "Piasek", desc = "Włącza statystykę dropu piasku") @ConfigEditorBoolean
        public boolean sandToggle = true;
    }

    public static class Other {
        @Expose @ConfigOption(name = "Czapki", desc = "Włącza statystykę dropu czapek") @ConfigEditorBoolean
        public boolean czapkiToggle = true;
        @Expose @ConfigOption(name = "Oceaniczna Ruda", desc = "Włącza statystykę dropu oceanicznej rudy") @ConfigEditorBoolean
        public boolean oceanicznaRudaToggle = true;
        @Expose @ConfigOption(name = "Wybitna Przynęta", desc = "Włącza statystykę dropu wybitnej przynęty") @ConfigEditorBoolean
        public boolean wybitnaPrzynetaToggle = true;
        @Expose @ConfigOption(name = "Dobra Przynęta", desc = "Włącza statystykę dropu dobrej przynęty") @ConfigEditorBoolean
        public boolean dobraPrzynetaToggle = true;
    }

    // --- JOBS ---

    public static class Abilities {
        @Expose
        @ConfigOption(name = "Customowe Umiejętności", desc = "Zastępuje paski umiejętności na górze ekranu customowym odliczaniem zajmującym mniej miejsca")
        @ConfigEditorBoolean
        public boolean customAbilities = true;

        @Expose
        public HudPosition abilitiesPosition = new HudPosition(true, 1f);

        @ConfigOption(name = "Pozycja Umiejętności", desc = "Przenieś umiejętności tam, gdzie ci odpowiadają")
        @ConfigEditorButton(buttonText = "Zmień")
        public transient Runnable moveAbilities = () -> HudEditor.open(HudElement.ABILITIES);

        @Expose
        @ConfigOption(name = "Graj Dźwięki Umiejętności", desc = "Dodaje dźwięk odliczania ostatnich 3 sekund umiejętności oraz zakończenia cooldownu")
        @ConfigEditorBoolean
        public boolean playAbilitiesSound = true;

        @Expose
        @ConfigOption(name = "Dźwięk Umiejętności", desc = "Odpowiada za to, który dźwięk będzie grany po zakończeniu cooldownu umiejętności")
        @ConfigEditorDropdown
        public MainSound abilitiesSound = MainSound.BELL;

        @Expose
        @ConfigOption(name = "Dźwięk Odliczania", desc = "Odpowiada za to, który dźwięk będzie grany podczas odliczania")
        @ConfigEditorDropdown
        public CountdownSound countdownSound = CountdownSound.EXP;

    }

    // --- XP COUNTER ---

    public static class XPCounter {
        @Expose
        @ConfigOption(name = "Licznik XP", desc = "Wybierz kiedy ma się wyświetlać licznik xp pracy")
        @ConfigEditorDropdown
        public XPCounterDisplayBehavior xpCounterDisplayBehavior = XPCounterDisplayBehavior.XP;

        @Expose
        public HudPosition skillsPosition = new HudPosition(true, 0f);

        @ConfigOption(name = "Pozycja Licznika XP", desc = "Przenieś licznik xp tam, gdzie ci odpowiada")
        @ConfigEditorButton(buttonText = "Zmień")
        public transient Runnable moveSkills = () -> HudEditor.open(HudElement.SKILLS);
    }

    // --- FISHING ---

    public static class Fishing {
        @Expose
        @ConfigOption(name = "Statystyki Rybaka", desc = "Wybierz kiedy mają się wyświetlać statystyki rybaka")
        @ConfigEditorDropdown
        public FishingDisplayBehavior fishingDisplayBehavior = FishingDisplayBehavior.FISHING;

        @Expose
        public HudPosition fishingPosition = new HudPosition(false, 0.5f);

        @ConfigOption(name = "Pozycja Statystyk", desc = "Przenieś statystyki rybaka tam, gdzie ci odpowiadają")
        @ConfigEditorButton(buttonText = "Zmień")
        public transient Runnable moveFishing = () -> HudEditor.open(HudElement.FISHING);

        @Expose
        @ConfigOption(name = "Wybór Statystyk", desc = "Wybierz, które statystyki rybaka mają się wyświetlać")
        @Accordion
        public Fish fish = new Fish();
    }

    public static class Fish {
        @Expose @ConfigOption(name = "Niewielka", desc = "Włącza wyświetlanie wyłowionych niewielkich ryb") @ConfigEditorBoolean
        public boolean toggleNiewielka = true;
        @Expose @ConfigOption(name = "Przeciętna", desc = "Włącza wyświetlanie wyłowionych przeciętnych ryb") @ConfigEditorBoolean
        public boolean togglePrzecietna = true;
        @Expose @ConfigOption(name = "Wymiarowa", desc = "Włącza wyświetlanie wyłowionych wymiarowych ryb") @ConfigEditorBoolean
        public boolean toggleWymiarowa = true;
        @Expose @ConfigOption(name = "Ogromna", desc = "Włącza wyświetlanie wyłowionych ogromnych ryb") @ConfigEditorBoolean
        public boolean toggleOgromna = true;
        @Expose @ConfigOption(name = "Mamucia", desc = "Włącza wyświetlanie wyłowionych mamucich ryb") @ConfigEditorBoolean
        public boolean toggleMamucia = true;
        @Expose @ConfigOption(name = "Suma", desc = "Włącza wyświetlanie sumy wszystkich wyłowionych ryb") @ConfigEditorBoolean
        public boolean toggleSuma = true;
        @Expose @ConfigOption(name = "Sashimi", desc = "Włącza wyświetlanie wyłowionego sashimi") @ConfigEditorBoolean
        public boolean toggleSashimi = true;
        @Expose @ConfigOption(name = "Zarobek", desc = "Włącza wyświetlanie zarobku ze sprzedaży ryb") @ConfigEditorBoolean
        public boolean toggleZarobek = true;
        @Expose @ConfigOption(name = "Waga", desc = "Włącza wyświetlanie sumy wagi wyłowionych ryb") @ConfigEditorBoolean
        public boolean toggleWaga = true;
        @Expose @ConfigOption(name = "Największa", desc = "Włącza wyświetlanie wagi największej wyłowionej ryby") @ConfigEditorBoolean
        public boolean toggleNajwieksza = true;
        @Expose @ConfigOption(name = "Najmniejsza", desc = "Włącza wyświetlanie wagi najmniejszej wyłowionej ryby") @ConfigEditorBoolean
        public boolean toggleNajmniejsza = true;
    }

    // --- DUNGEONS ---

    public static class Dungeons {
        @Expose
        @ConfigOption(name = "Mapa Dungeonu", desc = "Ustawienia customowej mapy dungeonu")
        @Accordion
        public DungeonMap dungeonMap = new DungeonMap();

        @Expose
        @ConfigOption(name = "Graj Dźwięk Dungeonu", desc = "Dodaje dźwięk zakończenia cooldownu dungeonu")
        @ConfigEditorBoolean
        public boolean playDungeonSound = true;

        @Expose
        @ConfigOption(name = "Dźwięk Dungeonu", desc = "Odpowiada za to, który dźwięk będzie grany po zakończeniu cooldownu dungeonu")
        @ConfigEditorDropdown
        public MainSound dungeonSound = MainSound.GOAT_HORN;
    }

    public static class DungeonMap {
        @Expose
        @ConfigOption(name = "Mapa Dungeonu", desc = "Wyświetla na ekranie customową mapę dungeonu")
        @ConfigEditorBoolean
        public boolean dungeonMapToggle = true;

        @Expose
        public HudPosition dungeonMapPosition = new HudPosition(false, 0f);

        @ConfigOption(name = "Pozycja Mapy", desc = "Przenieś mapę dungeonu tam, gdzie ci odpowiada")
        @ConfigEditorButton(buttonText = "Zmień")
        public transient Runnable moveDungeonMap = () -> HudEditor.open(HudElement.DUNGEON_MAP);

        @Expose
        @ConfigOption(name = "Typ Mapy", desc = "Zmienia typ wyświetlanej mapy dungeonu")
        @ConfigEditorDropdown
        public DungeonMapType dungeonMapType = DungeonMapType.SQUARE;

        @Expose
        @ConfigOption(name = "Rozmiar Mapy", desc = "Zmienia skalę wyświetlania mapy dungeonu")
        @ConfigEditorSlider(minValue = 1, maxValue = 9, minStep = 1)
        public int dungeonMapScale = 0;

        @Expose
        @ConfigOption(name = "Licznik Pojemników", desc = "Wyświetla pod mapą liczbę pojemników w pokoju, w którym się znajdujesz")
        @ConfigEditorBoolean
        public boolean containerCounters = true;

        @Expose
        @ConfigOption(name = "Ikonka Ceramiki", desc = "Wyświetla na mapie ikonkę ceramiki w pokojach, gdzie dostępne są pojemniki do zebrania")
        @ConfigEditorBoolean
        public boolean containerIcons = true;

        @Expose
        @ConfigOption(name = "Ślady Stóp", desc = "Wyświetla na mapie małe ślady stóp podążające za ikonką gracza")
        @ConfigEditorBoolean
        public boolean dungeonMapFootsteps = true;
    }
}