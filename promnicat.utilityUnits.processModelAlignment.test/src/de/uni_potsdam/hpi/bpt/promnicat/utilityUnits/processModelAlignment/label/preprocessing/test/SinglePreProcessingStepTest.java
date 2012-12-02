package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.preprocessing.test;

import java.util.HashSet;

import org.jbpt.alignment.LabelEntity;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.preprocessing.SinglePreProcessingStep;

@Ignore
public abstract class SinglePreProcessingStepTest<Step extends SinglePreProcessingStep> {
	
	private final Step step;

	public SinglePreProcessingStepTest(Step step) {
		this.step = step;
	}

	/** @see SinglePreProcessingStep#process(String) */
	@Test
	public void testProcess() {
		String in = getInputString();
		String expected = getExpectedString();
		String out = step.process(in);
		Assert.assertEquals(expected, out);
	}

	/** @see SinglePreProcessingStep#processAll(HashSet) */
	@Test
	public void testProcessAll() {
		HashSet<LabelEntity> in = getInputCollection();
		HashSet<LabelEntity> expected = getExpectedCollection();
		step.processAll(in, in); //same collection processed twice shouldn't mess with results
		
		Assert.assertEquals(expected, in);
	}

	/** After processing, the input collection should equal the expected collection
	 *  @see #getExpectedCollection() */
	protected abstract HashSet<LabelEntity> getInputCollection();

	/** After processing, the input collection should equal the expected collection
	 *  @see #getInputCollection() */
	protected abstract HashSet<LabelEntity> getExpectedCollection();

	/** After processing, the input String should equal the expected String
	 *  @see #getExpectedString() */
	protected abstract String getInputString();

	/** After processing, the input String should equal the expected String
	 *  @see #getInputString() */
	protected abstract String getExpectedString();
}
