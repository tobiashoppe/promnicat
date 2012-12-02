package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.similarity.matrix;

import java.util.Set;

import org.jbpt.alignment.IEntity;

public interface ISimilarityMatrix<Entity extends IEntity> {

	public abstract void addSimilarity(Entity first, Entity second,
			float similarity);

	public abstract void addSimilarity(SimilarityPair<Entity> pair);

	/**
	 * A query method to retrieve all relevant similarities for a given entity or pair of entities
	 * @param first an entity to retrieve - or null to retrieve all
	 * @param second an entity to retrieve - or null to retrieve all
	 * @return all {@link SimilarityPair}s of this matrix, matching first and second entity 
	 */
	public abstract Set<SimilarityPair<Entity>> getSimilarities(Entity first, Entity second);

	public abstract Set<Entity> getFirstSet();

	public abstract void setFirstSet(Set<Entity> firstSet);

	public abstract Set<Entity> getSecondSet();

	public abstract void setSecondSet(Set<Entity> secondSet);

	public abstract float getThreshold();

	public abstract void setThreshold(float threshold);

	public abstract Set<SimilarityPair<Entity>> getSimilarities();

	public abstract void setSimilarities(
			Set<SimilarityPair<Entity>> similarities);

	public abstract void removeSimilarities(
			Set<SimilarityPair<Entity>> toRemove);

	/**
	 * @return the average similarity of the pairs in this matrix
	 */
	public abstract float getSimilarity();

}