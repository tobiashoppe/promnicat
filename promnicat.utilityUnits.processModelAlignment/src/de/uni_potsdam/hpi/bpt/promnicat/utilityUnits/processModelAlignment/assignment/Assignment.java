package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.assignment;




import org.jbpt.alignment.IEntity;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.similarity.LabelSimilarity;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.similarity.matrix.ISimilarityMatrix;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.similarity.matrix.SimilarityMatrix;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.similarity.matrix.SimilarityPair;

/**
 * A ProcessModelAlignment finds a matching between the tasks
 * of two given processes, thus "aligning" these processes.
 * The alignment is based on a similarity matrix that represents
 * the similarity of tasks of Process A to tasks of Process B.
 * This matrix must be provided by a {@link LabelSimilarity}
 * @author stefan.schaefer
 */
public interface Assignment {

	/** @see Assignment 
	 *  @param similarities The {@link SimilarityMatrix} to use for the assignment 
	 *  @return a {@link SimilarityMatrix} containing <b>only the assigned {@link SimilarityPair}s</b>*/
	public ISimilarityMatrix<IEntity> assign(ISimilarityMatrix<IEntity> similarities);
}
