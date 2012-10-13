package de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.assignment;

import java.util.HashSet;

import org.apache.log4j.Logger;
import org.jbpt.alignment.IEntity;

import de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.label.similarity.matrix.ISimilarityMatrix;
import de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.label.similarity.matrix.SimilarityMatrix;
import de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.label.similarity.matrix.SimilarityPair;


/**
 * The {@link StableMarriage} algorithm matches each of the Strings from first collection
 * with each of the Strings from second collection, based on the similarity matrix.<br>
 * The original Stable Marriage problem is simplified, due to the symmetric of the similarity algorithm
 * (i.e. similarity(A,B) == similarity(B,A)). Therefore it reduces the problem to an analog of the
 * Hungarian algorithm.   
 * @author stefan.schaefer
 */
public class StableMarriage extends AbstractAssignment {
	private static final Logger LOGGER = Logger.getLogger(StableMarriage.class);

	/** @see StableMarriage 
	 *  @param similarities The {@link SimilarityMatrix} to use for the assignment 
	 *  @return a {@link SimilarityMatrix} containing <b>only the assigned {@link SimilarityPair}s</b>*/
	@Override
	public ISimilarityMatrix<IEntity> assignWithEquallySizedSets(ISimilarityMatrix<IEntity> similarities) {
		ISimilarityMatrix<IEntity> result = new SimilarityMatrix<IEntity>();
		HashSet<IEntity> availableMen = new HashSet<IEntity>(similarities.getFirstSet());
		HashSet<IEntity> availableWomen = new HashSet<IEntity>(similarities.getSecondSet());
		while (!availableMen.isEmpty()) {
			for (IEntity man : similarities.getFirstSet()) {
				if (!availableMen.contains(man)) continue;
				
				SimilarityPair<IEntity> favorite = getFavorite(man, availableMen, availableWomen, similarities, result);
				availableMen.remove(favorite.first);
				availableWomen.remove(favorite.second);
				LOGGER.debug(favorite);
				result.addSimilarity(favorite);
			}
		}
		return result;
	}

	private SimilarityPair<IEntity> getFavorite(IEntity man,
			HashSet<IEntity> availableMen, HashSet<IEntity> availableWomen,
			ISimilarityMatrix<IEntity> similarities,
			ISimilarityMatrix<IEntity> result) {
		SimilarityPair<IEntity> favorite = null;
		SimilarityPair<IEntity> toDivorce = null;
		for (IEntity woman : similarities.getSecondSet()) {
			SimilarityPair<IEntity> pair = similarities.getSimilarities(man, woman).iterator().next();
			if ((favorite == null || pair.similarity > favorite.similarity)) {
				if (availableWomen.contains(woman)) {
					toDivorce = null;
					favorite = pair;
				} else {
					SimilarityPair<IEntity> current = result.getSimilarities(null, woman).iterator().next();
					if (current.similarity < pair.similarity) {
						toDivorce = current;
						favorite = pair;
					}
				}
			}
		}
		if (toDivorce != null) {
			result.getSimilarities().remove(toDivorce);
			availableMen.add(toDivorce.first);
			availableWomen.add(toDivorce.second);			
		}
		return favorite;
	}
}
