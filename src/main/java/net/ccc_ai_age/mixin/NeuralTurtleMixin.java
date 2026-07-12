package net.ccc_ai_age.mixin;

import dan200.computercraft.shared.turtle.blocks.TurtleBlockEntity;
import net.ccc_ai_age.integration.NeuralTurtleAccess;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TurtleBlockEntity.class)
public abstract class NeuralTurtleMixin extends BlockEntity implements NeuralTurtleAccess {

	@Unique
	private boolean neuralAI = false;

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

	@Inject(method = "loadServer", at = @At("TAIL"))
	private void onLoadServer(NbtCompound nbt, CallbackInfo ci) {
		this.neuralAI = nbt.getBoolean("NeuralAI");
	}

	@Inject(method = "writeNbt", at = @At("TAIL")) // writeNbt
	private void onWriteNbt(NbtCompound nbt, CallbackInfo ci) {
		if (this.neuralAI) {
			nbt.putBoolean("NeuralAI", true);
		}
	}
}
