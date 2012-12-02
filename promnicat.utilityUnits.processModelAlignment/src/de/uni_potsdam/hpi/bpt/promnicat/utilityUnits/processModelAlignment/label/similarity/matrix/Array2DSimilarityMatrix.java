package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.similarity.matrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import org.jbpt.alignment.IEntity;

public class Array2DSimilarityMatrix<Entity extends IEntity> implements ISimilarityMatrix<Entity> {
	private ArrayList<Entity> firstSet = new ArrayList<Entity>();
	private ArrayList<Entity> secondSet = new ArrayList<Entity>();
	private float[][] similarities = new float[0][0];
	/** A minimum degree of similarity. Values below will be set to 0. */
	private float threshold;

	/** creates an empty matrix with no similarity threshold */
	public Array2DSimilarityMatrix() {
		this(0);
	}
	
	/**
	 * creates an empty matrix
	 * @param threshold @see {@link #threshold}
	 */
	public Array2DSimilarityMatrix(float threshold) {
		this.setThreshold(threshold);
	}

	/** transforms the given (Entity,Entity,Similarity) relations into a new matrix without a threshold */
	public Array2DSimilarityMatrix(HashMap<Entity, HashMap<Entity, Float>> result) {
		this(result, 0);
	}
	
	/** transforms the given (Entity,Entity,Similarity) relations into a new matrix
	 * @param threshold @see {@link #threshold} */
	public Array2DSimilarityMatrix(HashMap<Entity, HashMap<Entity, Float>> result, float threshold) {
		this(threshold);
		firstSet.addAll(result.keySet());
		for (Entry<Entity, HashMap<Entity, Float>> eachEntry : result.entrySet()) {
			Set<Entity> keySet = eachEntry.getValue().keySet();
			for (Entity each : keySet) {
				if (!secondSet.contains(each)) {
					secondSet.add(each);
				}
			}
		}
		updateArrayRange();
		for (Entry<Entity, HashMap<Entity, Float>> eachEntry : result.entrySet()) {
			Entity first = eachEntry.getKey();
			for (Entry<Entity, Float> eachSubEntry : eachEntry.getValue().entrySet()) {
				Entity second = eachSubEntry.getKey();
				Float similarity = eachSubEntry.getValue();
				addSimilarity(first, second, similarity);
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.hpi.bpt.pma.label.similarity.ISimilarityMatrix#addSimilarity(Entity, Entity, float)
	 */
	@Override
	public void addSimilarity(Entity first, Entity second, float similarity) {
		if (similarity < threshold) similarity = 0;
		int index1 = firstSet.indexOf(first);
		if (index1 == -1) {
			index1 = firstSet.size();
			firstSet.add(first);
		}
		int index2 = secondSet.indexOf(second);
		if (index2 == -1) {
			index2 = secondSet.size();
			secondSet.add(second);
		}
		updateArrayRange();
		similarities[index1][index2] = similarity;
	}
	
	/* (non-Javadoc)
	 * @see de.hpi.bpt.pma.label.similarity.ISimilarityMatrix#addSimilarity(de.hpi.bpt.pma.label.similarity.SimilarityPair)
	 */
	@Override
	public void addSimilarity(SimilarityPair<Entity> pair) {
		addSimilarity(pair.first, pair.second, pair.similarity);
	}

	/* (non-Javadoc)
	 * @see de.hpi.bpt.pma.label.similarity.ISimilarityMatrix#getSimilarities(Entity, Entity)
	 */
	@Override
	public Set<SimilarityPair<Entity>> getSimilarities(Entity first, Entity second) {
		HashSet<SimilarityPair<Entity>> result = new HashSet<SimilarityPair<Entity>>();
		for (int i=0 ; i < firstSet.size() ; i++) {
			Entity eachFirst = firstSet.get(i);
			if (first == null || first.equals(eachFirst)) {
				for (int j=0 ; j < secondSet.size() ; j++) {
					Entity eachSecond = secondSet.get(j);
					if (second == null || second.equals(eachSecond)) {
						result.add(new SimilarityPair<Entity>(eachFirst, eachSecond, similarities[i][j]));
					}
				}
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see de.hpi.bpt.pma.label.similarity.ISimilarityMatrix#getFirstSet()
	 */
	@Override
	public Set<Entity> getFirstSet() {
		return new HashSet<Entity>(firstSet);
	}

	/* (non-Javadoc)
	 * @see de.hpi.bpt.pma.label.similarity.ISimilarityMatrix#setFirstSet(java.util.Set)
	 */
	@Override
	public void setFirstSet(Set<Entity> firstSet) {
		this.firstSet.clear();
		this.firstSet.addAll(firstSet);
		updateArrayRange();
	}

	/* (non-Javadoc)
	 * @see de.hpi.bpt.pma.label.similarity.ISimilarityMatrix#getSecondSet()
	 */
	@Override
	public Set<Entity> getSecondSet() {
		return new HashSet<Entity>(secondSet);
	}

	/* (non-Javadoc)
	 * @see de.hpi.bpt.pma.label.similarity.ISimilarityMatrix#setSecondSet(java.util.Set)
	 */
	@Override
	public void setSecondSet(Set<Entity> secondSet) {
		this.secondSet.clear();
		this.secondSet.addAll(secondSet);
		updateArrayRange();
	}

	/* (non-Javadoc)
	 * @see de.hpi.bpt.pma.label.similarity.ISimilarityMatrix#getThreshold()
	 */
	@Override
	public float getThreshold() {
		return threshold;
	}

	/* (non-Javadoc)
	 * @see de.hpi.bpt.pma.label.similarity.ISimilarityMatrix#setThreshold(float)
	 */
	@Override
	public void setThreshold(float threshold) {
		this.threshold = threshold;
		for (int i=0 ; i < firstSet.size() ; i++) {
			for (int j=0 ; j < secondSet.size() ; j++) {
				if (similarities[i][j] < threshold) {
					similarities[i][j] = 0;
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.hpi.bpt.pma.label.similarity.ISimilarityMatrix#getSimilarities()
	 */
	@Override
	public Set<SimilarityPair<Entity>> getSimilarities() {
		return getSimilarities(null, null);
	}

	/* (non-Javadoc)
	 * @see de.hpi.bpt.pma.label.similarity.ISimilarityMatrix#setSimilarities(java.util.HashSet)
	 */
	@Override
	public void setSimilarities(Set<SimilarityPair<Entity>> similarities) {
		firstSet.clear();
		secondSet.clear();
		for (SimilarityPair<Entity> pair : similarities) {
			firstSet.add(pair.first);
			secondSet.add(pair.second);
		}
		updateArrayRange();
		for (SimilarityPair<Entity> pair : similarities) {
			addSimilarity(pair);
		}
	}

	private void updateArrayRange() {
		int setSize1 = firstSet.size();
		int setSize2 = secondSet.size();
		int arrayLength1 = similarities.length;
		int arrayLength2 = (arrayLength1 > 0 ? similarities[0].length : 0); 
		if (setSize1 == arrayLength1 && setSize2 == arrayLength2) {
			return; //no update required
		}
		if (setSize2 > arrayLength2) { //information added, copy old data
			for (int i=0 ; i < arrayLength1 ; i++) {
				if (similarities[i] != null) {
					similarities[i] = Arrays.copyOf(similarities[i], setSize1);
				} else {
					Logger.getLogger(getClass().getName()).warning("similarities[" + i + "] was null");
				}
			}
		}
		if (setSize1 > arrayLength1) { //information added, copy old data
			similarities = Arrays.copyOf(similarities, setSize1);
			for (int i = arrayLength1; i < setSize1 ; i++) {
				similarities[i] = new float[setSize2];
			}
		} else { //new values will be added --> empty matrix 
			similarities = new float[setSize1][setSize2];
		}
	}

	/* (non-Javadoc)
	 * @see de.hpi.bpt.pma.label.similarity.ISimilarityMatrix#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String nl = "\n";
		for (int i=0 ; i < firstSet.size() ; i++) {
			Entity eachFirst = firstSet.get(i);
			for (int j=0 ; j < secondSet.size() ; j++) {
				Entity eachSecond = secondSet.get(j);
				sb.append(eachFirst + " ~ "+eachSecond+" --> "+similarities[i][j]).append(nl);
			}
		}
		return sb.toString(); 
	}

	@Override
	public void removeSimilarities(Set<SimilarityPair<Entity>> toRemove) {
		for (SimilarityPair<Entity> pair : toRemove) {
			int index1 = firstSet.indexOf(pair.first);
			int index2 = secondSet.indexOf(pair.second);
			similarities[index1][index2] = 0.0f;
		}
	}

	@Override
	public float getSimilarity() {
		float sum = 0;
		for (SimilarityPair<Entity> each : getSimilarities()) {
			sum += each.similarity;
		}
		return sum / Math.max(firstSet.size(), secondSet.size());
	}
}
