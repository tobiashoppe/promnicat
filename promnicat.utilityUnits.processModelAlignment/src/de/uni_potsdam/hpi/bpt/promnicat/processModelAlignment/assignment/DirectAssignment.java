package de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.assignment;

import java.util.HashSet;

import org.apache.log4j.Logger;
import org.jbpt.alignment.IEntity;

import de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.label.similarity.matrix.ISimilarityMatrix;
import de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.label.similarity.matrix.SimilarityMatrix;
import de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.label.similarity.matrix.SimilarityPair;


/**
 * The {@link DirectAssignment} algorithm straight-forward matches each of the Strings from first collection
 * to the most-preferred available String from second collection, based on the similarity matrix.
 * @author stefan.schaefer
 */
public class DirectAssignment extends AbstractAssignment {
	private static final Logger LOGGER = Logger.getLogger(DirectAssignment.class);

	/** @see DirectAssignment 
	 *  @param similarities The {@link SimilarityMatrix} to use for the assignment 
	 *  @return a {@link SimilarityMatrix} containing <b>only the assigned {@link SimilarityPair}s</b>*/
	@Override
	public ISimilarityMatrix<IEntity> assignWithEquallySizedSets(ISimilarityMatrix<IEntity> similarities) {
		ISimilarityMatrix<IEntity> result = new SimilarityMatrix<IEntity>();
		HashSet<IEntity> availableWomen = new HashSet<IEntity>(similarities.getSecondSet());
		for (IEntity man : similarities.getFirstSet()) {
			SimilarityPair<IEntity> favorite = null;
			float favoriteSim = 0;
			for (IEntity woman : availableWomen) {
				SimilarityPair<IEntity> pair = similarities.getSimilarities(man, woman).iterator().next();
				float similarity = pair.similarity;
				if (favorite == null || similarity > favoriteSim) {
					favorite = pair;
					favoriteSim = similarity;
				}
			}
			availableWomen.remove(favorite.second);
			LOGGER.debug(favorite);
			result.addSimilarity(favorite);
		}
		return result;
	}
}
