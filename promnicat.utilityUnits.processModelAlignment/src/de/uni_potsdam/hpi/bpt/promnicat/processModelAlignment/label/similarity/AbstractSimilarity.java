package de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.label.similarity;

import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Logger;

import org.jbpt.alignment.LabelEntity;

import de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.label.similarity.matrix.ISimilarityMatrix;
import de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.label.similarity.matrix.LinkedSimilarityMatrix;

/**
 * Implements {@link #getSimilarityMatrix(Collection, Collection)} so that
 * subclasses don't need to think about similarity matrices.
 * @author stefan.schaefer
 */
public abstract class AbstractSimilarity implements LabelSimilarity {
	@Override
	public ISimilarityMatrix<LabelEntity> getSimilarityMatrix(Collection<LabelEntity> first, Collection<LabelEntity> second) {
		HashMap<LabelEntity, HashMap<LabelEntity, Float>> result = new HashMap<LabelEntity, HashMap<LabelEntity, Float>>();
		Logger.getLogger(getClass().getName()).info("SimilarityClass: "+getClass().getSimpleName());
		for (LabelEntity eachOfFirst : first) {
			for (LabelEntity eachOfSecond : second) {
				HashMap<LabelEntity, Float> hashMap = result.get(eachOfFirst);
				if (hashMap == null) {
					result.put(eachOfFirst, hashMap = new HashMap<LabelEntity, Float>());
				}
				float sim = compute(eachOfFirst, eachOfSecond);
				hashMap.put(eachOfSecond, sim);
				Logger.getLogger(getClass().getName()).info(
					"Similarity: ("+eachOfFirst.getLabel()+", "+eachOfSecond.getLabel()+", "+sim+")"
				);
			}
		}
		return new LinkedSimilarityMatrix<LabelEntity>(result);
	}
}
