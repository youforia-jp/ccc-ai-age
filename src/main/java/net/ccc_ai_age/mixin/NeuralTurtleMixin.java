package net.ccc_ai_age.mixin;

import dan200.computercraft.shared.computer.blocks.AbstractComputerBlockEntity;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.core.computer.ComputerSide;
import net.ccc_ai_age.api.AITier;
import net.ccc_ai_age.blockentity.KineticAICoreBlockEntity;
import net.ccc_ai_age.integration.NeuralComputerAccess;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractComputerBlockEntity.class)
public abstract class NeuralTurtleMixin extends BlockEntity implements NeuralComputerAccess {

	@Unique
	private boolean neuralAI = false;

	@Unique
	private String neuralTier = "quantum";

	protected NeuralTurtleMixin(net.minecraft.block.entity.BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public boolean isNeuralAI() {
		return this.neuralAI;
	}

	@Override
	public void setNeuralAI(boolean value) {
		this.neuralAI = value;
	}

	@Override
	public String getNeuralTier() {
		return this.neuralTier;
	}

	@Override
	public void setNeuralTier(String tier) {
		this.neuralTier = tier;
	}

	@Inject(method = "loadServer", at = @At("TAIL"))
	private void onLoadServer(NbtCompound nbt, CallbackInfo ci) {
		this.neuralAI = nbt.getBoolean("NeuralAI");
		if (nbt.contains("NeuralTier")) {
			this.neuralTier = nbt.getString("NeuralTier");
		}
	}

	@Inject(method = "writeNbt", at = @At("TAIL"))
	private void onWriteNbt(NbtCompound nbt, CallbackInfo ci) {
		if (this.neuralAI) {
			nbt.putBoolean("NeuralAI", true);
			nbt.putString("NeuralTier", this.neuralTier);
		}
	}

	@Inject(method = "createServerComputer", at = @At("RETURN"), remap = false)
	private void onCreateServerComputer(CallbackInfoReturnable<ServerComputer> cir) {
		ServerComputer computer = cir.getReturnValue();
		if (this.neuralAI && computer != null) {
			AITier tierVal;
			try {
				tierVal = AITier.valueOf(this.neuralTier.toUpperCase());
			} catch (Exception e) {
				tierVal = AITier.QUANTUM;
			}
			computer.setPeripheral(ComputerSide.BOTTOM, new KineticAICoreBlockEntity.KineticAICorePeripheral(tierVal));
		}
	}
}
