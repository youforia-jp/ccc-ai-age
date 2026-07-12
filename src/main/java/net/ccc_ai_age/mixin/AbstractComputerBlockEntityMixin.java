package net.ccc_ai_age.mixin;

import dan200.computercraft.shared.computer.blocks.AbstractComputerBlockEntity;
import net.ccc_ai_age.integration.NeuralComputerAccess;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractComputerBlockEntity.class)
public abstract class AbstractComputerBlockEntityMixin extends BlockEntity implements NeuralComputerAccess {

	@Unique
	private boolean neuralAI = false;

	@Unique
	private String neuralTier = "quantum";

	protected AbstractComputerBlockEntityMixin(net.minecraft.block.entity.BlockEntityType<?> type, net.minecraft.util.math.BlockPos pos, net.minecraft.block.BlockState state) {
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
}
