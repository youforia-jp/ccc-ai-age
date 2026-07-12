package net.ccc_ai_age.blockentity;

import net.ccc_ai_age.ModBlockEntities;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Block entity for the Kinetic AI Core.
 *
 * <p>To prevent conflicts with {@link BlockEntity#getType()}, the peripheral implementation
 * is delegated to the inner class {@link KineticAICorePeripheral}.
 */
public class KineticAICoreBlockEntity extends BlockEntity {

	private final IPeripheral peripheral;

	public KineticAICoreBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.KINETIC_AI_CORE, pos, state);
		this.peripheral = new KineticAICorePeripheral(this);
	}

	/**
	 * Gets the CC: Tweaked peripheral instance associated with this block entity.
	 *
	 * @return the peripheral instance
	 */
	public IPeripheral getPeripheral() {
		return this.peripheral;
	}

	/**
	 * Implementation of {@link IPeripheral} for the Kinetic AI Core.
	 *
	 * <p>Exposes CC: Tweaked peripheral functions and connects them to the parent block entity.
	 */
	public static class KineticAICorePeripheral implements IPeripheral {

		private final KineticAICoreBlockEntity blockEntity;

		public KineticAICorePeripheral(KineticAICoreBlockEntity blockEntity) {
			this.blockEntity = blockEntity;
		}

		@Override
		public @NotNull String getType() {
			return "ai_core";
		}

		@Override
		public void attach(@NotNull IComputerAccess computer) {
			// Phase 3: register this computer so we can push async events to it
		}

		@Override
		public void detach(@NotNull IComputerAccess computer) {
			// Phase 3: cancel in-flight HTTP requests for this computer
		}

		@Override
		public boolean equals(@Nullable IPeripheral other) {
			if (this == other) return true;
			if (!(other instanceof KineticAICorePeripheral)) return false;
			return this.blockEntity == ((KineticAICorePeripheral) other).blockEntity;
		}

		/**
		 * Phase 2 stub — returns a simple status string confirming the peripheral
		 * is online. This will be replaced in Phase 3 with a real async call to a
		 * local Ollama HTTP endpoint that streams token-by-token responses back to
		 * the calling Lua coroutine via {@code os.pullEvent}.
		 *
		 * @return a human-readable status string
		 */
		@LuaFunction
		public final String streamTelemetry() {
			return "AI Core Online: Standing by for Ollama link.";
		}
	}
}
