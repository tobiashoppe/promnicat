package de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.label.preprocessing.test;

import java.util.HashSet;

import org.jbpt.alignment.LabelEntity;
import org.junit.Assert;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.label.preprocessing.PairwisePreProcessingStep;

public abstract class PairwisePreProcessingStepTest<Step extends PairwisePreProcessingStep> {
	
	private final Step step;

	public PairwisePreProcessingStepTest(Step step) {
		this.step = step;
	}

	/** @see PairwisePreProcessingStep#process(String) */
	@Test
	public void testProcess() {
		String[] in = getInputStrings();
		String[] expected = getExpectedStrings();
		String[] out = step.process(in[0], in[1]);
		Assert.assertArrayEquals(expected, out);
	}

	/** @see PairwisePreProcessingStep#processAll(HashSet) */
	@Test
	public void testProcessAll() {
		HashSet<LabelEntity>[] in = getInputSets();
		HashSet<LabelEntity>[] expected = getExpectedSets();
		step.processAll(in[0], in[1]);
		
		Assert.assertArrayEquals(expected, in);
	}

	/** After processing, the input collection should equal the expected collection
	 *  @see #getExpectedSets() */
	protected abstract HashSet<LabelEntity>[] getInputSets();

	/** After processing, the input collection should equal the expected collection
	 *  @see #getInputSets() */
	protected abstract HashSet<LabelEntity>[] getExpectedSets();

	/** After processing, the input String should equal the expected String
	 *  @see #getExpectedStrings() */
	protected abstract String[] getInputStrings();

	/** After processing, the input String should equal the expected String
	 *  @see #getInputStrings() */
	protected abstract String[] getExpectedStrings();
}
