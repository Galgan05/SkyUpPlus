package net.galgan.skyupplus;

import net.fabricmc.api.ClientModInitializer;
import net.galgan.skyupplus.clickers.*;
import net.galgan.skyupplus.dungeons.*;
import net.galgan.skyupplus.features.*;
import net.galgan.skyupplus.config.SUPConfig;
import net.galgan.skyupplus.config.SUPData;
import net.galgan.skyupplus.features.HUD;
import net.galgan.skyupplus.util.Commands;
import net.galgan.skyupplus.util.Keybinds;
import net.galgan.skyupplus.util.ServerRestrictor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkyUpPlusClient implements ClientModInitializer {

	public static final String MOD_ID = "skyupplus";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		// --- INITIALIZATION ---
		SUPConfig.init();
		SUPData.init();
		ServerRestrictor.register();
		Commands.register();
		Keybinds.register();

		// --- CLICKERS ---
		LeftClicker.register();
		LeftHold.register();
		RightClicker.register();
		RightHold.register();
		SellClicker.register();
		HandRestock.register();

		// --- FEATURES ---
		Scoreboard.register();
		Fishing.register();
		Requirements.register();
		Crates.register();
		Reminders.register();
		Warnings.register();
		Abilities.register();
		Chat.register();
		XPCounter.register();
		HealthBar.register();
		HUD.register();

		// --- DUNGEON ---
		SignScanner.register();
		SpawnerScanner.register();
		MinibossScanner.register();
		RoomContainerCounter.register();
		DungeonMapHud.register();

		// --- LOGGER ---
		LOGGER.info("Successfully loaded SUP!");
	}
}