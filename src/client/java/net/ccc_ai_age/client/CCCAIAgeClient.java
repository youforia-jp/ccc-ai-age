package net.ccc_ai_age.client;

import net.fabricmc.api.ClientModInitializer;

public class CCCAIAgeClient implements ClientModInitializer {
	private static boolean shouldShowPopup = false;

	@Override
	public void onInitializeClient() {
		// Register turtle upgrade modellers on the client (v0.39)
		dan200.computercraft.api.client.FabricComputerCraftAPIClient.registerTurtleUpgradeModeller(
				net.ccc_ai_age.CCCAIAge.BASIC_UPGRADE_SERIALISER,
				dan200.computercraft.api.client.turtle.TurtleUpgradeModeller.sided(
						net.ccc_ai_age.CCCAIAge.id("item/basic_kinetic_ai_core_left"),
						net.ccc_ai_age.CCCAIAge.id("item/basic_kinetic_ai_core_right")
				)
		);
		dan200.computercraft.api.client.FabricComputerCraftAPIClient.registerTurtleUpgradeModeller(
				net.ccc_ai_age.CCCAIAge.ADVANCED_UPGRADE_SERIALISER,
				dan200.computercraft.api.client.turtle.TurtleUpgradeModeller.sided(
						net.ccc_ai_age.CCCAIAge.id("item/advanced_kinetic_ai_core_left"),
						net.ccc_ai_age.CCCAIAge.id("item/advanced_kinetic_ai_core_right")
				)
		);
		dan200.computercraft.api.client.FabricComputerCraftAPIClient.registerTurtleUpgradeModeller(
				net.ccc_ai_age.CCCAIAge.QUANTUM_UPGRADE_SERIALISER,
				dan200.computercraft.api.client.turtle.TurtleUpgradeModeller.sided(
						net.ccc_ai_age.CCCAIAge.id("item/quantum_kinetic_ai_core_left"),
						net.ccc_ai_age.CCCAIAge.id("item/quantum_kinetic_ai_core_right")
				)
		);

		// Register world join event (v0.46)
		net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			shouldShowPopup = true;
		});

		// Register tick event to safely trigger popup once loading screens clear (v0.46)
		net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (shouldShowPopup && client.world != null && client.currentScreen == null) {
				shouldShowPopup = false;
				client.setScreen(new ConfigNotificationScreen());
			}
		});
	}
}