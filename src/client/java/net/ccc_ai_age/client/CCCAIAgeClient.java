package net.ccc_ai_age.client;

import net.fabricmc.api.ClientModInitializer;

public class CCCAIAgeClient implements ClientModInitializer {
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
	}
}