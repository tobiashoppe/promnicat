package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.similarity;

import java.util.Collection;

import org.jbpt.alignment.LabelEntity;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.similarity.matrix.ISimilarityMatrix;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.similarity.matrix.SimilarityMatrix;

/**
 * A LabelSimilarity is used in Process Model Alignment.
 * For each pair of activities from process A and process B
 * the similarity must be computed and will range from
 * 0 (nothing in common) to 1 (equal)  
 * @author stefan.schaefer
 */
public interface LabelSimilarity {
	/** computes the similarity of the two given Strings.
	 * @return a value between 0.0 (nothing in common) and 1.0 (equal)*/
	public float compute(LabelEntity first, LabelEntity second);
	
	/** {@link #compute(LabelEntity, LabelEntity)} the similarity of each pair of Strings from first and second set.
	 * @return a {@link SimilarityMatrix} containing all pairs of similarities */
	public ISimilarityMatrix<LabelEntity> getSimilarityMatrix(Collection<LabelEntity> first, Collection<LabelEntity> second);
}
