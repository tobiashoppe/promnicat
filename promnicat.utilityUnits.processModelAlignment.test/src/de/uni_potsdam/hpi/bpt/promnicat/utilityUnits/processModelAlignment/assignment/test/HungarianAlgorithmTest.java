package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.assignment.test;

import org.jbpt.alignment.IEntity;
import org.jbpt.alignment.LabelEntity;
import org.junit.Assert;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.assignment.HungarianAlgorithm;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.similarity.matrix.ISimilarityMatrix;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.similarity.matrix.SimilarityMatrix;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.similarity.matrix.SimilarityPair;

public class HungarianAlgorithmTest extends AssignmentTest<HungarianAlgorithm> {

	private final static HungarianAlgorithm ha = new HungarianAlgorithm();

	public HungarianAlgorithmTest() {
		super(ha);
	}

	@Test
	public void testMinimumCost() {
		ISimilarityMatrix<LabelEntity> similarities = new SimilarityMatrix<LabelEntity>();
		ISimilarityMatrix<LabelEntity> expected = new SimilarityMatrix<LabelEntity>();

		addValues(similarities, expected);

		@SuppressWarnings({ "rawtypes", "unchecked" })
		ISimilarityMatrix<IEntity> assignments = ha.assign((ISimilarityMatrix) similarities);
		Assert.assertEquals(expected.getSimilarities(), assignments.getSimilarities());
	}

	private void addValues(ISimilarityMatrix<LabelEntity> similarities, ISimilarityMatrix<LabelEntity> expected) {
		LabelEntity[] labels = { 	new LabelEntity("This is a test"),
									new LabelEntity("We will fake similarities"),
									new LabelEntity("In order to create a stability problem")
		};
		// 0 likes 2 the most
		similarities.addSimilarity(labels[0], labels[0], 0.0f);
		similarities.addSimilarity(labels[0], labels[1], 0.5f);
		similarities.addSimilarity(labels[0], labels[2], 0.8f);
		// 1 likes 2 the most
		similarities.addSimilarity(labels[1], labels[0], 0.5f);
		similarities.addSimilarity(labels[1], labels[1], 0.0f);
		similarities.addSimilarity(labels[1], labels[2], 1.0f);
		// 2 likes 1 the most
		similarities.addSimilarity(labels[2], labels[0], 0.8f);
		similarities.addSimilarity(labels[2], labels[1], 1.0f);
		similarities.addSimilarity(labels[2], labels[2], 0.0f);
		
		// 2 -> 0, 0 -> 1, 1 -> 2
		// 2 is best suited for 0 and 1, 1 for 2 --> 0 should take 1, 2 should take 0
		expected.addSimilarity(new SimilarityPair<LabelEntity>(labels[2], labels[1], 1.0f));
		expected.addSimilarity(new SimilarityPair<LabelEntity>(labels[1], labels[0], 0.5f));
		expected.addSimilarity(new SimilarityPair<LabelEntity>(labels[0], labels[2], 0.8f));
	}

}
