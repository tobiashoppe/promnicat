package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.preprocessing;

import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.Synset;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.util.WordNetUtil;

/**
 * Replaces words of label2 with words of label1 if one is the synonym of the other (based on WordNet).
 * Use this if you want synonyms to get an exact match of 1.0 
 * @author stefan.schaefer
 */
public class ReplaceSynonyms extends PairwisePreProcessingStep {

	/** Replaces words of label1 with words of label2 if one is the synonym of the other (based on WordNet) */
	@Override
	public String[] process(String label1, String label2) {
		String[] words1 = label1.split("\\s");
		String[] words2 = label2.split("\\s");
		for (String each1 : words1) {
			for (int i = 0; i < words2.length; i++) {
				String each2 = words2[i];
				words2[i] = processWords(each1, each2);
			}
		}
		return new String[] {rebuildString(words1), rebuildString(words2)};
	}


	private String processWords(String label1, String label2) {
		IndexWord[] indexWords1 = null, indexWords2 = null;
		indexWords1 = WordNetUtil.getIndexWords(label1);
		indexWords2 = WordNetUtil.getIndexWords(label2);
		for (IndexWord indexWord1 : indexWords1) {
			for (IndexWord indexWord2 : indexWords2) {
				if (areSynonyms(indexWord1, indexWord2)) {
					return label1; //synonym replaced
				}
			}
		}

		return label2;
	}
	
		
	private boolean areSynonyms(IndexWord indexWord1, IndexWord indexWord2) {	
		Synset[] set1 = null, set2 = null;
		set1 = WordNetUtil.getSenses(indexWord1);
		set2 = WordNetUtil.getSenses(indexWord2);
			
		for (int i=0; i<set1.length; i++) {
			if (set1[i].containsWord(indexWord2.getLemma())) {
				return true;
			}
		}
		for (int j=0; j<set2.length; j++) {
			if (set2[j].containsWord(indexWord1.getLemma())) {
				return true;
			}
		}
		
		return false;
	}

	private String rebuildString(String[] words) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < words.length; i++) {
			if (words[i] != null) {
				sb.append(words[i]).append(" ");
			}
		}
		if (sb.length() > 0) {
			sb.setLength(sb.length()-1);
		}
		return sb.toString();
	}
}
