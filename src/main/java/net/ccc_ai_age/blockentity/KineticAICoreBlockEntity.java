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
 * <h2>CC: Tweaked Integration</h2>
 * <p>This class implements {@link IPeripheral} directly, which is the simplest
 * integration pattern for a block that is <em>always</em> a peripheral.
 * CC: Tweaked discovers it via the {@link net.ccc_ai_age.integration.CCTweakedPlugin}
 * registered under the {@code "computercraft"} entrypoint.
 *
 * <h2>Peripheral Lifecycle</h2>
 * <ul>
 *   <li>{@link #attach(IComputerAccess)} — called when a CC computer connects.</li>
 *   <li>{@link #detach(IComputerAccess)} — called when the computer disconnects.</li>
 *   <li>{@link #equals(IPeripheral)} — used by CC:T to deduplicate wrappers.</li>
 * </ul>
 *
 * <h2>Phase 2 — Lua API Surface</h2>
 * <ul>
 *   <li>{@link #streamTelemetry()} — stub returning a status string. Will be
 *       replaced in Phase 3 with async HTTP streaming to a local Ollama endpoint.</li>
 * </ul>
 */
public class KineticAICoreBlockEntity extends BlockEntity implements IPeripheral {

	public KineticAICoreBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.KINETIC_AI_CORE, pos, state);
	}

	// =========================================================================
	// IPeripheral — identity
	// =========================================================================

	/**
	 * The peripheral type string exposed to Lua code.
	 * Scripts use {@code peripheral.find("ai_core")} to locate this device.
	 */
	@Override
	public @NotNull String getType() {
		return "ai_core";
	}

	// =========================================================================
	// IPeripheral — lifecycle
	// =========================================================================

	/**
	 * Called by CC: Tweaked when a computer attaches to this peripheral.
	 * Override in Phase 3 to set up event queuing for streaming responses.
	 */
	@Override
	public void attach(@NotNull IComputerAccess computer) {
		// Phase 3: register this computer so we can push async events to it
	}

	/**
	 * Called by CC: Tweaked when a computer detaches from this peripheral.
	 * Override in Phase 3 to clean up any in-flight requests.
	 */
	@Override
	public void detach(@NotNull IComputerAccess computer) {
		// Phase 3: cancel in-flight HTTP requests for this computer
	}

	/**
	 * Equality check used by CC: Tweaked to avoid double-wrapping the same
	 * block entity. Two peripherals are the same if they are the same object.
	 */
	@Override
	public boolean equals(@Nullable IPeripheral other) {
		return this == other;
	}

	// =========================================================================
	// IPeripheral — Lua API
	// =========================================================================

	/**
	 * Phase 2 stub — returns a simple status string confirming the peripheral
	 * is online. This will be replaced in Phase 3 with a real async call to a
	 * local Ollama HTTP endpoint that streams token-by-token responses back to
	 * the calling Lua coroutine via {@code os.pullEvent}.
	 *
	 * <p><strong>Lua usage:</strong>
	 * <pre>{@code
	 * local core = peripheral.find("ai_core")
	 * print(core.streamTelemetry())
	 * -- Output: AI Core Online: Standing by for Ollama link.
	 * }</pre>
	 *
	 * @return a human-readable status string
	 */
	@LuaFunction
	public final String streamTelemetry() {
		return "AI Core Online: Standing by for Ollama link.";
	}
}
