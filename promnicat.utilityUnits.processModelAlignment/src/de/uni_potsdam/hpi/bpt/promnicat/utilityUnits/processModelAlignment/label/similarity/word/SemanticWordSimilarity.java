package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.similarity.word;

import net.didion.jwnl.data.IndexWord;

import org.jbpt.alignment.LabelEntity;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.similarity.AbstractSimilarity;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.similarity.LabelSimilarity;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.similarity.StringEditDistance;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.util.WordNetUtil;

/**
 * A WordNet-based semantic similarity, which compares each pair of words
 * by traversing the WordNet hierarchy of words until a common anchestor
 * is found. The closer to the root entity, the less similar. 
 * @author stefan.schaefer
 */
public abstract class SemanticWordSimilarity extends AbstractSimilarity {
	
	/**
	 * A fallback syntactic similarity in case a word is misspelled,
	 * so that a semantic similarity must fail.
	 */
	private LabelSimilarity fallback = new StringEditDistance();;

	@Override
	public float compute(LabelEntity first, LabelEntity second) {
		if (first.getLabel().equals(second.getLabel())) return 1;
		float sim = getSemanticSimilarity(first, second);
		if (sim == -1) {
			//hypernym similarity failed, probably due to a typo in one of the words
			if (fallback != null) {
				sim = fallback.compute(first, second);
			}
		}
		return sim;
	}
	
	private float getSemanticSimilarity(LabelEntity label1, LabelEntity label2) {
		return getSemanticSimilarity(label1.getLabel(), label2.getLabel());
	}
	
	private float getSemanticSimilarity(String label1, String label2) {
		if (label1.equalsIgnoreCase(label2)) return 1;
		
		IndexWord[] indexWords1 = WordNetUtil.getIndexWords(label1);
		IndexWord[] indexWords2 = WordNetUtil.getIndexWords(label2);
		float maxSim = -1;
		for (IndexWord indexWord1 : indexWords1) {
			for (IndexWord indexWord2 : indexWords2) {
				if (indexWord1.getPOS() == indexWord2.getPOS()) {
					float sim = getSemanticSimilarity(indexWord1, indexWord2);
					if (sim > maxSim) {
						maxSim = sim;
					}
				}
			}
		}
		return maxSim;
	}

	/** compute the semantic similarity between the two words */ 
	abstract protected float getSemanticSimilarity(IndexWord indexWord1, IndexWord indexWord2);

	/** if WordNet doesn't know the one or the other word,
	 *  the similarity would always be 0. To prevent this,
	 *  we use a fallback similarity measure.<br>
	 *  If this is set to null, similarity will be 0. */
	public LabelSimilarity getFallback() {
		return fallback;
	}

	/** if WordNet doesn't know the one or the other word,
	 *  the similarity would always be 0. To prevent this,
	 *  we use a fallback similarity measure.<br>
	 *  If this is set to null, similarity will be 0. */
	public void setFallback(LabelSimilarity fallback) {
		this.fallback = fallback;
	}
}