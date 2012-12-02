package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.util;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.logging.Logger;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.PointerUtils;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.list.PointerTargetTree;
import net.didion.jwnl.dictionary.Dictionary;

import org.jbpt.alignment.LabelEntity;

/**
 * A utility class which encapsulates basic usages of the WordNet dictionary
 * and improves performance by caching results in hashmaps.
 * @author stefan.schaefer
 */
public class WordNetUtil {

	private static final Logger LOGGER = Logger.getLogger(WordNetUtil.class.getName());

	private static final String WORDNET_CONFIG_FILE = "../promnicat.utilityUnits.processModelAlignment/lib/wordnet/config/my_properties.xml";

	public static final String LIB_PATH = "../promnicat.utilityUnits.processModelAlignment/lib/wordnet";

	private static Dictionary dict;

	/* result stores to improve performance */
	private static HashMap<String, IndexWord[]> 		labelToIndexWords 	= new HashMap<String, IndexWord[]>();
	private static HashMap<IndexWord, Synset[]> 		indexWordToSynsets	= new HashMap<IndexWord, Synset[]>();
	private static HashMap<Synset, PointerTargetTree> 	synsetToHypernymTree	= new HashMap<Synset, PointerTargetTree>();

	/** Lazily initializes the dictionary 
	 * @return the WordNet dictionary*/
	public static Dictionary getDict() {
		if (dict == null) {
			try {
				LOGGER.info("Loading WordNet....");
				JWNL.initialize(new FileInputStream(WORDNET_CONFIG_FILE));
				dict = Dictionary.getInstance();
				LOGGER.info("Loading WordNet finished.");
			} catch (Exception e) {
				LOGGER.severe("Loading WordNet failed with: " + e.getMessage());
			}
		}
		return dict;
	}
	
	/** @see #getIndexWords(String) */
	public static IndexWord[] getIndexWords(LabelEntity label) {
		return getIndexWords(label.getLabel());
	}
	
	/**
	 * IndexWord is a unique "combination" of part-of-speech
	 * and word, e.g. "fish (noun)" and "fish (verb)"
	 * @param label a single word
	 * @return the IndexWords of this word
	 */
	public static IndexWord[] getIndexWords(String label) {
		if (labelToIndexWords.containsKey(label)) {
			return labelToIndexWords.get(label);
		}
		try {
			IndexWord[] indexWordArray = getDict().lookupAllIndexWords(label).getIndexWordArray();
			labelToIndexWords.put(label, indexWordArray);
			return indexWordArray;
		} catch(JWNLException e) {
			LOGGER.severe("Error while accessing WordNet library: " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * A sense of an index word is its meaning. Thus a homonym
	 * will return a synset for each of its meanings, e.g.
	 * "fish (animal)" and "fish (zodiac sign)"
	 * @param indexWord the index word
	 * @return the different synsets of this indexWord
	 */
	public static Synset[] getSenses(IndexWord indexWord) {
		if (indexWordToSynsets.containsKey(indexWord)) {
			return indexWordToSynsets.get(indexWord);
		}
		try {
			Synset[] senses = indexWord.getSenses();
			indexWordToSynsets.put(indexWord, senses);
			return senses;
		} catch (JWNLException e) {
			LOGGER.severe("Error while accessing WordNet library: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Computes the hypernym tree for a sense, e.g. 
	 * "salmon -> fish -> animal" 
	 * @param sense a synset of a word
	 * @return the hypernym tree for this synset
	 */
	public static PointerTargetTree getHypernymTree(Synset sense) {
		if (synsetToHypernymTree.containsKey(sense)) {
			return synsetToHypernymTree.get(sense);
		}
		try {
			PointerTargetTree hypernymTree = PointerUtils.getInstance().getHypernymTree(sense);
			return hypernymTree;
		} catch (JWNLException e) {
			LOGGER.severe("Error while accessing WordNet library: " + e.getMessage());
			return null;
		}
	}
}
