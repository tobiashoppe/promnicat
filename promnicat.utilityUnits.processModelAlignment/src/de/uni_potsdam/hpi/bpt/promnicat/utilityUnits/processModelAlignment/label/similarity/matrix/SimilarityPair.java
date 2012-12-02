package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.similarity.matrix;

import org.jbpt.alignment.IEntity;

public class SimilarityPair<Entity extends IEntity> {

	public final Entity first;
	public final Entity second;
	public float similarity;

	public SimilarityPair(Entity first, Entity second, float similarity) {
		this.first = first;
		this.second = second;
		this.similarity = similarity;
	}
	
	@Override
	public int hashCode() {
		return ("" + first + second+similarity).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SimilarityPair))
			return false;
		@SuppressWarnings("unchecked")
		SimilarityPair<IEntity> pair = (SimilarityPair<IEntity>) obj;
		return this.first.getLabel().equals(pair.first.getLabel())
				&& this.second.getLabel().equals(pair.second.getLabel())
				&& this.similarity == pair.similarity;
	}



	@Override
	public String toString() {
		return first + " ~ "+second+" --> "+similarity;
	}
	
}
