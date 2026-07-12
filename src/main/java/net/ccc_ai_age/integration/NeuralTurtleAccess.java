package net.ccc_ai_age.integration;

/**
 * Accessor interface implemented by TurtleBlockEntity mixin to manage the NeuralAI state.
 */
public interface NeuralTurtleAccess {
	boolean isNeuralAI();
	void setNeuralAI(boolean value);
}
