package de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.assignment.test;

import java.util.Set;

import org.jbpt.alignment.IEntity;
import org.jbpt.alignment.LabelEntity;
import org.junit.Assert;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.assignment.Assignment;
import de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.label.similarity.matrix.ISimilarityMatrix;
import de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.label.similarity.matrix.SimilarityMatrix;
import de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.label.similarity.matrix.SimilarityPair;

public abstract class AssignmentTest<Algorithm extends Assignment> {
	
	private final Algorithm alg;

	public AssignmentTest(Algorithm alg) {
		this.alg = alg;
	}
	
	@Test
	public void testMoreLabels1ThanLabels2() {
		ISimilarityMatrix<IEntity> similarities = new SimilarityMatrix<IEntity>();
		ISimilarityMatrix<IEntity> expected = new SimilarityMatrix<IEntity>();
		fillWithValues(similarities, expected);
		IEntity toBeRemoved = similarities.getSecondSet().iterator().next();
		boolean removeFromSecond = false;
		removeFrom(similarities, toBeRemoved, removeFromSecond);
		removeFrom(expected, toBeRemoved, removeFromSecond);
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		ISimilarityMatrix<IEntity> assignments = alg.assign((ISimilarityMatrix) similarities);
		Assert.assertEquals(expected.getSimilarities(), assignments.getSimilarities());
	}
	
	@Test
	public void testLessLabels1ThanLabels2() {
		ISimilarityMatrix<IEntity> similarities = new SimilarityMatrix<IEntity>();
		ISimilarityMatrix<IEntity> expected = new SimilarityMatrix<IEntity>();
		fillWithValues(similarities, expected);
		IEntity toBeRemoved = similarities.getFirstSet().iterator().next();
		boolean removeFromFirst = true;
		removeFrom(similarities, toBeRemoved, removeFromFirst);
		removeFrom(expected, toBeRemoved, removeFromFirst);
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		ISimilarityMatrix<IEntity> assignments = alg.assign((ISimilarityMatrix) similarities);
		Assert.assertEquals(expected.getSimilarities(), assignments.getSimilarities());
	}

	private void removeFrom(ISimilarityMatrix<IEntity> similarities, IEntity toBeRemoved, boolean fromFirst) {
		if (fromFirst) {
			Set<SimilarityPair<IEntity>> simsToRemove = similarities.getSimilarities(toBeRemoved, null);
			similarities.getFirstSet().remove(toBeRemoved);
			similarities.removeSimilarities((Set<SimilarityPair<IEntity>>)simsToRemove);
		} else {
			Set<SimilarityPair<IEntity>> simsToRemove = similarities.getSimilarities(null, toBeRemoved);
			similarities.getSecondSet().remove(toBeRemoved);
			similarities.removeSimilarities(simsToRemove);
		}
	}
	
	@Test
	public void testPerfectMatch() {
		ISimilarityMatrix<IEntity> similarities = new SimilarityMatrix<IEntity>();
		ISimilarityMatrix<IEntity> expected = new SimilarityMatrix<IEntity>();
		fillWithValues(similarities, expected);

		@SuppressWarnings({ "rawtypes", "unchecked" })
		ISimilarityMatrix<IEntity> assignments = alg.assign((ISimilarityMatrix) similarities);
		Assert.assertEquals(expected.getSimilarities(), assignments.getSimilarities());
	}

	private void fillWithValues(ISimilarityMatrix<IEntity> similarities, ISimilarityMatrix<IEntity> expected) {
		IEntity[] labels = { 	new LabelEntity("This is a test"),
									new LabelEntity("This is another test"),
									new LabelEntity("Each should be matched exactly")
		};
		for (IEntity each : labels) {
			for (IEntity eachAgain : labels) {
				similarities.addSimilarity(each, eachAgain, each.equals(eachAgain) ? 1.0f : 0.0f);
			}
			expected.addSimilarity(new SimilarityPair<IEntity>(each, each, 1));
		}
	}
}
