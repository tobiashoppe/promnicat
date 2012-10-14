package de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.label.similarity;

import java.util.Collection;
import java.util.LinkedHashSet;

import org.jbpt.alignment.LabelEntity;

import de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.assignment.HungarianAlgorithm;
import de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.label.LabelAlignment;
import de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.label.preprocessing.ToLowerCase;
import de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.label.similarity.word.LinSimilarity;
import de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.label.similarity.word.SemanticWordSimilarity;

/**
 * A WordNet-based semantic similarity, which compares all words of the two labels
 * by traversing the WordNet hypernym tree of words. A hypernym is, so to speak, the
 * superclass of an object, e.g."animal" is a hypernym of "fish" is a hypernym of "salmon" 
 * @author stefan.schaefer
 * @see #wordAlignment
 * @see SemanticWordSimilarity
 */
public class SemanticSimilarity extends AbstractSimilarity {
	/**
	 * The local alignment performed on the set of words from label 1 and label 2.<br>
	 * @PreProcessing {@link ToLowerCase}
	 * @Similarity {@link SemanticWordSimilarity}
	 * @Assignment {@link HungarianAlgorithm} 
	 */
	private LabelAlignment wordAlignment;
	private SemanticWordSimilarity wordSimilarity;
	
	public SemanticSimilarity() {
		wordSimilarity = new LinSimilarity();
		wordAlignment = new LabelAlignment();
		wordAlignment.addPreProcessingStep(new ToLowerCase());
		wordAlignment.setSimilarity(getWordSimilarity());
		wordAlignment.setAssignment(new HungarianAlgorithm());
	}

	/**
	 * Split both labels into words and perform a {@link #wordAlignment} on it.
	 */
	@Override
	public float compute(LabelEntity first, LabelEntity second) {
		if (first.getLabel().equals(second.getLabel())) return 1;
		String[] firstWords = first.getLabel().split("\\s");
		String[] secondWords = second.getLabel().split("\\s");
		Collection<LabelEntity> labels1 = new LinkedHashSet<LabelEntity>();
		Collection<LabelEntity> labels2 = new LinkedHashSet<LabelEntity>();
		for (String each1 : firstWords) {
			labels1.add(new LabelEntity(each1));
		}
		for (String each2 : secondWords) {
			labels2.add(new LabelEntity(each2));
		}
		return wordAlignment.align(labels1, labels2).getSimilarity();
	}

	public SemanticWordSimilarity getWordSimilarity() {
		return wordSimilarity;
	}

	public void setWordSimilarity(SemanticWordSimilarity wordSimilarity) {
		this.wordSimilarity = wordSimilarity;
		this.wordAlignment.setSimilarity(wordSimilarity);
	}
}
