package net.ccc_ai_age.mixin;

import dan200.computercraft.shared.computer.blocks.ComputerBlockEntity;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.core.computer.ComputerSide;
import net.ccc_ai_age.api.AITier;
import net.ccc_ai_age.blockentity.KineticAICoreBlockEntity;
import net.ccc_ai_age.integration.NeuralComputerAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ComputerBlockEntity.class)
public class NeuralComputerMixin {

	@Inject(method = "createComputer", at = @At("RETURN"), remap = false)
	private void onCreateComputer(int id, CallbackInfoReturnable<ServerComputer> cir) {
		ServerComputer computer = cir.getReturnValue();
		Object self = this;
		if (self instanceof NeuralComputerAccess && ((NeuralComputerAccess) self).isNeuralAI() && computer != null) {
			String tierName = ((NeuralComputerAccess) self).getNeuralTier();
			AITier tierVal;
			try {
				tierVal = AITier.valueOf(tierName.toUpperCase());
			} catch (Exception e) {
				tierVal = AITier.QUANTUM;
			}
			computer.setPeripheral(ComputerSide.BOTTOM, new KineticAICoreBlockEntity.KineticAICorePeripheral(tierVal));
		}
	}
}
