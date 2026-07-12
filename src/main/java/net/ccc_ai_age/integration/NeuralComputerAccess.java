package net.ccc_ai_age.integration;

/**
 * Accessor interface implemented by AbstractComputerBlockEntity mixin to manage the NeuralAI and NeuralTier state.
 */
public interface NeuralComputerAccess {
	boolean isNeuralAI();
	void setNeuralAI(boolean value);
	String getNeuralTier();
	void setNeuralTier(String tier);
}
