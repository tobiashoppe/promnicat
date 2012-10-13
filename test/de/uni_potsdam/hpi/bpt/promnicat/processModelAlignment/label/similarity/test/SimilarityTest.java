package de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.label.similarity.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.jbpt.alignment.LabelEntity;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.label.similarity.LabelSimilarity;

public abstract class SimilarityTest<Similarity extends LabelSimilarity> {
	private static final LabelEntity REFERENCE = new LabelEntity("Buy Fish");
	private static final LabelEntity DISTINCT = new LabelEntity("idea");
	private static final LabelEntity MORE_SIMILAR = new LabelEntity("Buy Salmon");
	private static final LabelEntity LESS_SIMILAR = new LabelEntity("Sell Salmon");
	protected final Similarity sim;

	public SimilarityTest(Similarity sim) {
		this.sim = sim;
	}

	@Test
	public void testComputeIdentity() {
		float result = sim.compute(REFERENCE, REFERENCE);
		float epsilon = 0.0f;
		assertEquals(1.0f, result, epsilon);
	}
	
	@Test
	public void testComputeDistinction() {
		float result = sim.compute(REFERENCE, DISTINCT);
		float epsilon = 0.01f; //we allow some small divergence
		assertEquals(0.0f, result, epsilon);
	}
	
	@Test
	public void testRelativeSimilarity() {
		float lower = sim.compute(REFERENCE, LESS_SIMILAR);
		float higher = sim.compute(REFERENCE, MORE_SIMILAR);
		assertTrue(lower < higher);
	}

	@Test
	public void testSymmetry() {
		float oneWay = sim.compute(REFERENCE, MORE_SIMILAR);
		float viceversa = sim.compute(MORE_SIMILAR, REFERENCE);
		float epsilon = 0.0f;
		assertEquals(oneWay, viceversa, epsilon);
	}

	@Test
	public void testPresentationExamples() {
//		float first = sim.compute(new LabelEntity("Receive Order"), new LabelEntity("Recieve Order"));
		float second = sim.compute(new LabelEntity("Process The Order"), new LabelEntity("Process Order"));
//		float third = sim.compute(new LabelEntity("Ship Order"), new LabelEntity("Shipping"));
//		Logger.getLogger(getClass()).info("First:\t"+first);
		Logger.getLogger(getClass()).info("Second:\t"+second);
//		Logger.getLogger(getClass()).info("Third:\t"+third);
	}
	
}