package net.ccc_ai_age.mixin;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.core.computer.ComputerSide;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.turtle.blocks.TurtleBlockEntity;
import dan200.computercraft.shared.turtle.core.TurtleBrain;
import net.ccc_ai_age.api.AITier;
import net.ccc_ai_age.blockentity.KineticAICoreBlockEntity;
import net.ccc_ai_age.integration.AITurtleUpgrade;
import net.ccc_ai_age.integration.NeuralComputerAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TurtleBrain.class, remap = false)
public class TurtleBrainMixin {

	@Shadow
	private TurtleBlockEntity owner;

	@Inject(method = "updatePeripherals", at = @At("TAIL"))
	private void onUpdatePeripherals(ServerComputer computer, CallbackInfo ci) {
		// --- Path 1: NeuralAI NBT flag (set by the crafting recipe) ---
		if (owner instanceof NeuralComputerAccess && ((NeuralComputerAccess) owner).isNeuralAI()) {
			IPeripheral neuralPeripheral = null;
			String tierName = ((NeuralComputerAccess) owner).getNeuralTier();
			AITier tierVal;
			try {
				tierVal = AITier.valueOf(tierName.toUpperCase());
			} catch (Exception e) {
				tierVal = AITier.QUANTUM;
			}
			neuralPeripheral = new KineticAICoreBlockEntity.KineticAICorePeripheral(tierVal);
			// Inject virtual peripheral on BOTTOM side to not override LEFT/RIGHT physical slots
			computer.setPeripheral(ComputerSide.BOTTOM, neuralPeripheral);
			return;
		}
		
		try {
			for (TurtleSide side : TurtleSide.values()) {
				Object upgrade = owner.getAccess().getUpgrade(side);
				if (upgrade instanceof AITurtleUpgrade && ((AITurtleUpgrade) upgrade).hasModem()) {
					AITier tier = ((AITurtleUpgrade) upgrade).getTier();
					
					net.minecraft.registry.RegistryKey<net.minecraft.registry.Registry<dan200.computercraft.api.turtle.ITurtleUpgrade>> key = 
						net.minecraft.registry.RegistryKey.ofRegistry(new net.minecraft.util.Identifier("computercraft", "turtle_upgrades"));
					net.minecraft.registry.Registry<dan200.computercraft.api.turtle.ITurtleUpgrade> registry = 
						owner.getWorld().getRegistryManager().get(key);
					
					String modemId = tier == AITier.QUANTUM ? "wireless_modem_advanced" : "wireless_modem_normal";
					dan200.computercraft.api.turtle.ITurtleUpgrade modemUpgrade = registry.get(new net.minecraft.util.Identifier("computercraft", modemId));
					
					if (modemUpgrade != null) {
						IPeripheral modemPeripheral = modemUpgrade.createPeripheral(owner.getAccess(), dan200.computercraft.api.turtle.TurtleSide.LEFT); // Side doesn't matter for modem
						computer.setPeripheral(ComputerSide.BOTTOM, modemPeripheral);
					}
					return;
				}
			}
		} catch (Exception ignored) {
			// If the CC:T API changes, fail silently rather than crashing the turtle
		}
		
		computer.setPeripheral(ComputerSide.BOTTOM, null);
	}
}
