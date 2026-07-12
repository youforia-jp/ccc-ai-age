package net.ccc_ai_age.integration;

import net.ccc_ai_age.CCCAIAge;
import net.ccc_ai_age.ModBlockEntities;
import net.ccc_ai_age.blockentity.KineticAICoreBlockEntity;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.PeripheralLookup;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

/**
 * CC: Tweaked peripheral plugin for CC:C AI Age.
 *
 * <h2>How it works</h2>
 * <p>CC: Tweaked exposes a {@code "computercraft"} Fabric entrypoint. Any class
 * listed under that key in {@code fabric.mod.json} is instantiated by CC:T
 * after it has completed its own initialization. This guarantees that
 * {@link ComputerCraftAPI} and {@link PeripheralLookup} are ready to use.
 *
 * <h2>Registration strategy</h2>
 * <p>We use the {@link PeripheralLookup} (CC:T's block-entity API lookup) to
 * register a provider for {@link KineticAICoreBlockEntity}. CC:T calls this
 * provider whenever a computer tries to interact with an adjacent block entity
 * of that type. Because {@link KineticAICoreBlockEntity} already implements
 * {@link IPeripheral}, the provider simply returns the block entity itself —
 * no separate wrapper class is needed.
 *
 * <h2>Entrypoint declaration (fabric.mod.json)</h2>
 * <pre>{@code
 * "computercraft": [
 *     "net.ccc_ai_age.integration.CCTweakedPlugin"
 * ]
 * }</pre>
 */
public class CCTweakedPlugin implements Runnable {

	@Override
	public void run() {
		CCCAIAge.LOGGER.info("[CC:C AI Age] Registering CC: Tweaked peripheral provider...");

		// Register the KineticAICoreBlockEntity's peripheral wrapper as an IPeripheral provider.
		PeripheralLookup.get().registerForBlockEntity(
				(blockEntity, direction) -> blockEntity.getPeripheral(),
				ModBlockEntities.KINETIC_AI_CORE
		);

		CCCAIAge.LOGGER.info("[CC:C AI Age] Peripheral provider registered: ai_core");
	}
}
