package net.galgan.skyupplus.config;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.managed.ManagedDataFile;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class SUPData {

    private static ManagedDataFile<SUPData> managed;

    public static void init() {
        File file = FabricLoader.getInstance().getConfigDir().resolve("skyupplus/data.json").toFile();
        managed = ManagedDataFile.create(file, SUPData.class);
        managed.saveToFile();
    }

    public static SUPData get() {
        return managed.getInstance();
    }

    public static void save() {
        managed.saveToFile();
    }

    // --- FISHING ---

    @Expose public int niewielkaCount = 0;
    @Expose public int przecietnaCount = 0;
    @Expose public int wymiarowaCount = 0;
    @Expose public int ogromnaCount = 0;
    @Expose public int mamuciaCount = 0;
    @Expose public int totalCount = 0;
    @Expose public int sashimiCount = 0;
    @Expose public double totalEarned = 0;
    @Expose public double totalWeight = 0;
    @Expose public double biggestWeight = 0;
    @Expose public double smallestWeight = 0;

    // --- ELEMENTIUM ---

    @Expose public int karambitDropped = 0;
    @Expose public int perunDropped = 0;
    @Expose public int cymofanDropped = 0;
    @Expose public int mlotThoraDropped = 0;
    @Expose public int urizelDropped = 0;
    @Expose public int azadaDropped = 0;
    @Expose public int spinelDropped = 0;
    @Expose public int karpiolapDropped = 0;
    @Expose public int ethericaDropped = 0;
    @Expose public int lukLegolasaDropped = 0;
    @Expose public int arbaletDropped = 0;
    @Expose public int powrotOdysaDropped = 0;
    @Expose public int cassisDropped = 0;
    @Expose public int cuirassDropped = 0;
    @Expose public int cuissotDropped = 0;
    @Expose public int cossetDropped = 0;
    @Expose public int kapturSztormowyDropped = 0;
    @Expose public int kamizelkaBiwakowaDropped = 0;
    @Expose public int nogawiceOgnioodporneDropped = 0;
    @Expose public int traperyTerenoweDropped = 0;
    @Expose public int rivendellDropped = 0;
    @Expose public int impetDropped = 0;
    @Expose public int phloxDropped = 0;
    @Expose public int majsterDropped = 0;
    @Expose public int magiczneWiaderkoDropped = 0;
    @Expose public int statTrackerDropped = 0;
    @Expose public int jajkoNiespodziankaDropped = 0;
    @Expose public int kontrolerMagazynowDropped = 0;
    @Expose public int kapeluszPracusiaDropped = 0;
    @Expose public int tajemniceSkyUPaDropped = 0;
    @Expose public int klejnotKupieckiDropped = 0;

    // --- PLATINUM ---

    @Expose public int elementiumDropped = 0;
    @Expose public int mendingDropped = 0;
    @Expose public int emeraldBlockDropped = 0;
    @Expose public int goldBlockDropped = 0;
    @Expose public int ironBlockDropped = 0;
    @Expose public int redstoneBlockDropped = 0;
    @Expose public int dirtDropped = 0;
    @Expose public int grassBlockDropped = 0;
    @Expose public int sandDropped = 0;

    // --- OTHER ---

    @Expose public int kaskGornikaDropped = 0;
    @Expose public int czapkaDrwalaDropped = 0;
    @Expose public int kapeluszFarmeraDropped = 0;
    @Expose public int bandanaLowcyDropped = 0;
    @Expose public int kaskBudowniczegoDropped = 0;
    @Expose public int kapeluszRybakaDropped = 0;
    @Expose public int kapeluszCzarodziejaDropped = 0;
    @Expose public int oceanicznaRudaDropped = 0;
    @Expose public int wybitnaPrzynetaDropped = 0;
    @Expose public int dobraPrzynetaDropped = 0;

    // --- DAILY ---

    @Expose public int currentDay = ZonedDateTime.now(ZoneId.of("Europe/Warsaw")).getDayOfYear();
    @Expose public boolean hasSeenDaily = false;
}