package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.assignment;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.jbpt.alignment.IEntity;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.similarity.matrix.ISimilarityMatrix;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.similarity.matrix.SimilarityMatrix;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.similarity.matrix.SimilarityPair;

/**
 * An {@link AbstractAssignment} ensures, that when calling {@link #assign(SimilarityMatrix)}
 * both entity sets have an equal size by adding dummy entities with similarity=0 as necessary 
 * @author stefan.schaefer
 */
public abstract class AbstractAssignment implements Assignment {

	private final class DummyEntity implements IEntity {
		private String label = null;

		@Override
		public String getLabel() {
			return label;
		}

		@Override
		public int hashCode() {
			return (DummyEntity.class.toString()+label).hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof DummyEntity))
				return false;
			return label.equals(((DummyEntity) obj).label);
		}

		@Override
		public String toString() {
			return label;
		}
		
		@Override
		public DummyEntity clone() {
			DummyEntity clone = null;
			try {
				clone = (DummyEntity) super.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			if(this.label != null) {
				clone.label = new String(this.getLabel());
			}
			return clone;
		}
	}

	/** This implementation only ensures that both entity sets have equal size by adding dummies
	 *  as necessary and removing them after assignment. 
	 *  @see AbstractAssignment#assignWithEquallySizedSets(SimilarityMatrix)  */
	public ISimilarityMatrix<IEntity> assign(ISimilarityMatrix<IEntity> similarities) {
		//TODO: threshold in case of an assignment (=all that are not zero)
		boolean firstHasMore = similarities.getFirstSet().size() > similarities.getSecondSet().size();
		HashSet<DummyEntity> dummies = addDummies(similarities, firstHasMore);
		ISimilarityMatrix<IEntity> result = assignWithEquallySizedSets(similarities);
		removeDummies(dummies, result, firstHasMore);
		Logger.getLogger(getClass().getName()).info("AssignmentClass: "+getClass());
		for (SimilarityPair<IEntity> pair : result.getSimilarities()) {
			Logger.getLogger(getClass().getName()).info("Assignment: "+pair.toString());
		}
		return result;
	}

	/** This method should perform the actual assignment, since {@link #assign(SimilarityMatrix)} only
	 *  ensures that both entity sets are of equal size 
	 *  @see Assignment#assign(SimilarityMatrix) */
	protected abstract ISimilarityMatrix<IEntity> assignWithEquallySizedSets(ISimilarityMatrix<IEntity> similarities);

	private HashSet<DummyEntity> addDummies(ISimilarityMatrix<IEntity> similarities, boolean firstHasMore) {
		HashSet<DummyEntity> dummies = new HashSet<DummyEntity>();
		Set<IEntity> more = (firstHasMore ? similarities.getFirstSet() : similarities.getSecondSet());
		Set<IEntity> less = (!firstHasMore? similarities.getFirstSet() : similarities.getSecondSet());
		int howMany = more.size() - less.size();
		for (int i = 0; i < howMany; i++) {
			for (IEntity each : more) {
				DummyEntity dummy = new DummyEntity();
				dummy.label = "dummy"+i; //we need different labels to ensure, that the hash is different
				dummies.add(dummy);
				if (firstHasMore) {
					similarities.addSimilarity(each, dummy, 0);
				} else {
					similarities.addSimilarity(dummy, each, 0);
				}
			}
		}
		return dummies;
	}

	private void removeDummies(HashSet<DummyEntity> dummies, ISimilarityMatrix<IEntity> result, boolean firstHasMore) {
		for (DummyEntity dummyEntity : dummies) {
			if (firstHasMore) {
				Set<SimilarityPair<IEntity>> toRemove = result.getSimilarities(null, dummyEntity);
				result.removeSimilarities(toRemove);
				result.getSecondSet().remove(dummyEntity);
			} else {
				Set<SimilarityPair<IEntity>> toRemove = result.getSimilarities(dummyEntity, null);
				result.removeSimilarities(toRemove);
				result.getFirstSet().remove(dummyEntity);
			}
		}
	}
}
