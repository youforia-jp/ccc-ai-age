package net.ccc_ai_age.mixin;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.core.computer.ComputerSide;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.turtle.blocks.TurtleBlockEntity;
import dan200.computercraft.shared.turtle.core.TurtleBrain;
import net.ccc_ai_age.api.AITier;
import net.ccc_ai_age.blockentity.KineticAICoreBlockEntity;
import net.ccc_ai_age.integration.NeuralTurtleAccess;
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

	@Unique
	private IPeripheral neuralPeripheral;

	@Inject(method = "updatePeripherals", at = @At("TAIL"))
	private void onUpdatePeripherals(ServerComputer computer, CallbackInfo ci) {
		if (owner instanceof NeuralTurtleAccess && ((NeuralTurtleAccess) owner).isNeuralAI()) {
			if (neuralPeripheral == null) {
				neuralPeripheral = new KineticAICoreBlockEntity.KineticAICorePeripheral(AITier.QUANTUM);
			}
			// Inject virtual peripheral on BOTTOM side to not override LEFT/RIGHT physical slots
			computer.setPeripheral(ComputerSide.BOTTOM, neuralPeripheral);
		}
	}
}
