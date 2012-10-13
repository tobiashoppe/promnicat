package de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.assignment.test;

import org.jbpt.alignment.IEntity;
import org.jbpt.alignment.LabelEntity;
import org.junit.Assert;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.assignment.StableMarriage;
import de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.label.similarity.matrix.ISimilarityMatrix;
import de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.label.similarity.matrix.SimilarityMatrix;
import de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.label.similarity.matrix.SimilarityPair;

public class StableMarriageTest extends AssignmentTest<StableMarriage> {

	private final static StableMarriage sm = new StableMarriage();

	public StableMarriageTest() {
		super(sm);
	}

	@Test
	public void testStability() {
		ISimilarityMatrix<LabelEntity> similarities = new SimilarityMatrix<LabelEntity>();
		ISimilarityMatrix<LabelEntity> expected = new SimilarityMatrix<LabelEntity>();

		addValues(similarities, expected);

		@SuppressWarnings({ "rawtypes", "unchecked" })
		ISimilarityMatrix<IEntity> assignments = sm.assign((ISimilarityMatrix) similarities);
		Assert.assertEquals(expected.getSimilarities(), assignments.getSimilarities());
	}

	private void addValues(ISimilarityMatrix<LabelEntity> similarities,
			ISimilarityMatrix<LabelEntity> expected) {
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
		
		// 0 -> 0, 1 -> 2, 2 -> 1
		// 2 will not be happy, but 0 and 1 are --> stable
		expected.addSimilarity(new SimilarityPair<LabelEntity>(labels[2], labels[1], 1));
		expected.addSimilarity(new SimilarityPair<LabelEntity>(labels[1], labels[2], 1));
		expected.addSimilarity(new SimilarityPair<LabelEntity>(labels[0], labels[0], 0));
	}

}
