/**
 * 
 */
package de.uni_potsdam.hpi.bpt.promnicat.analysisModules.clustering.labeling;

import java.io.FileInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Pointer;
import net.didion.jwnl.data.PointerType;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.dictionary.Dictionary;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.objectbank.TokenizerFactory;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.trees.Tree;

/**
 * Class for querying a local WordNet database and enhancing the label candidate corpus.
 * @author Cindy Fähnrich
 *
 */
public class WordnetEnhancer extends ExternalResourceEnhancer {
	String ENGLISH = "";
	public LexicalizedParser parser;
	public TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
	
	/**
	 * Name of Wordnet config file. Change line 41 to your WordNet database to get it working.
	 */
	private static final String WORDNET_CONFIG_FILE = "wordnet_properties.xml";

	/**
	 * Holds a reference to the actual WordNet database.
	 */
	private static Dictionary dict;
	private static final Logger LOGGER = Logger.getLogger(WordnetEnhancer.class);
	
	public static Dictionary getDict() {
		if (dict == null) {
			try {
				LOGGER.info("Loading WordNet....");
				JWNL.initialize(new FileInputStream(WORDNET_CONFIG_FILE));
				dict = Dictionary.getInstance();
				LOGGER.info("Loading WordNet finished.");
			} catch (Exception e) {
				LOGGER.error("Loading WordNet failed", e);
			}
		}
		return dict;
	}
	
	/**
	 * Sets the path to the corpus that is needed for the lexical parser
	 * @param path
	 */
	public void setCorpusPath(String path){
		ENGLISH = path;
		parser  = new LexicalizedParser(ENGLISH);
	}
	
	/**
	 * Constructor that takes the path to the corpus for the lexical parser
	 * that determines the word type of a word (noun/verb/...).
	 * @param corpusPath path to the corpus
	 */
	public WordnetEnhancer(String corpusPath){
		ENGLISH = corpusPath;
		parser  = new LexicalizedParser(ENGLISH);
	}
	
	/**
	 * Enhances the label candidate set by using the WordNet database
	 * @param labelCandidates a list of words and their scores
	 */
	@Override
	public void enhanceLabelCandidates(HashMap<String, Double> labelCandidates){
		//index list; contains for each hypernym a list of its (direct) children (hyponyms)
		HashMap<Pointer, List<String>> hypernyms = new HashMap<Pointer, List<String>>();
		
		HashMap<String, IndexWord>wordSynsets = findHypernyms(hypernyms, labelCandidates);
		
		findHyponymsForHypernyms(wordSynsets, hypernyms);
		
		HashMap<String, Double> candidateHypernyms = rankCandidateHypernyms(hypernyms, labelCandidates);
		
		labelCandidates.putAll(candidateHypernyms);
	}
	
	/**
	 * Takes the label candidates and queries WordNet for their direct hypernyms.
	 * @param hypernyms
	 * @param labelCandidates
	 * @return
	 */
	public HashMap<String, IndexWord> findHypernyms (HashMap<Pointer, List<String>> hypernyms, HashMap<String, Double> labelCandidates){
		HashMap<String, IndexWord> wordSynsets = new HashMap<String, IndexWord>();
		try{
		for (Entry<String, Double> candidate : labelCandidates.entrySet()){
			String word = candidate.getKey();
			Pointer hypernym = null;
			
			POS type = determineType(word);
			if (type == null){//break because we only search hypernyms for nouns and verbs
				break;
			}
			//get wordnet entries
			IndexWord wordnetEntry;
			wordnetEntry = getDict().lookupIndexWord(type, word);
			if (wordnetEntry != null){
				//Synset[] synsets = database.getSynsets(word, type);
				wordSynsets.put(word, wordnetEntry);
			
				assignHypernyms(wordnetEntry, hypernyms, hypernym, type, word);
			}
		}
		} catch (JWNLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return wordSynsets;
	}
	
	/**
	 * Assigns the found hypernyms to the overall set of hypernyms
	 * @param synsets the query results
	 * @param hypernyms the set of hypernyms
	 * @param hypernym the currently found hypernym - value is assigned in this method
	 * @param type the word type
	 * @param word the current label candidate with which has been queried
	 */
	public void assignHypernyms(IndexWord wordnetEntry, HashMap<Pointer, List<String>> hypernyms, Pointer hypernym, POS type, String word){
		try{
		for (int i = 0; i < wordnetEntry.getSenseCount(); i++) {//for all found entries in wordnet
			//always only 1 hypernym
				Pointer[] synset;
				synset = wordnetEntry.getSenses()[i].getPointers(PointerType.HYPERNYM);
				if (synset.length > 0){
					hypernym = synset[0];
				}
		    //add the hypernym and its child (hyponym) to index list
		    if (!hypernyms.containsKey(hypernym)){
		    	List<String> hyponyms = new ArrayList<String>();
		    	hyponyms.add(word);
		    	hypernyms.put(hypernym, hyponyms);
		    } else {
		    	hypernyms.get(hypernym).add(word);
		    }
		}
		} catch (JWNLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Determines the SynsetType of a word. Only has a value if word is a verb or noun,
	 * otherwise returns null.
	 * @param word for which the SynsetType to find
	 * @return the word's SynsetType
	 */
	public POS determineType(String word){
		//determine word type to query the correct wordnet tree afterwards
		String tag = tag(word);
		POS type = POS.NOUN;
		if (tag.contains("VB")){
			type = POS.VERB;
		} else {
			if (tag.contains("NN")){
				type = POS.NOUN;
			} else {
				type = null;
			}
		}
		return type;
	}
	
	/**
	 * Searches for each hypernym which of the label candidates is a hyponym of it.
	 * @param wordSynsets wordnet entries
	 * @param hypernyms all hypernyms found
	 */
	public void findHyponymsForHypernyms(HashMap<String, IndexWord> wordSynsets, HashMap<Pointer, List<String>>hypernyms){
		//iterate along the tree for each word's synsets
		try {		
			for (Entry<String, IndexWord> synsets : wordSynsets.entrySet()){
					String word = synsets.getKey();
					for (int i = 0; i < synsets.getValue().getSenseCount(); i++){
						Synset set = synsets.getValue().getSenses()[i];
						Pointer[] hypernym;
						hypernym = set.getPointers(PointerType.HYPERNYM);
						while (hypernym.length > 0){
							//check whether this hypernym is contained in hypernym-index
							if (hypernyms.containsKey(hypernym[0])){
								List<String> hyponyms = hypernyms.get(hypernym[0]);
								if (!hyponyms.contains(word)){//add word belonging to hypernym to index
									hyponyms.add(word);
								}
							}
							hypernym = hypernym[0].getTargetSynset().getPointers(PointerType.HYPERNYM);						
						}
						
					}
				}
		} catch (JWNLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	/**
	 * Ranks all found hypernyms according to what words they encapsulate. If a hypernym
	 * only encapsulates 1 of the original label candidates, it is not considered to extend
	 * the set of label candidates. If otherwise, a hypernym gets a score to be added to 
	 * the set of label candidates. The score is calculated by adding the scores of all 
	 * encapsulated label candidates, weighted by their reciprocal rank.
	 * @param hypernyms the set of found hypernyms
	 * @param labelCandidates the set of current label candidates
	 * @return the set of label candidates enhanced by hypernyms
	 */
	public HashMap<String, Double> rankCandidateHypernyms(HashMap<Pointer, List<String>> hypernyms, HashMap<String, Double> labelCandidates){
		HashMap<String, Double> candidateHypernyms = new HashMap<String, Double>();
		//iterate over hypernym index
		try {
		for (Entry<Pointer, List<String>> hypernym : hypernyms.entrySet()){
			List<String> hyponyms = hypernym.getValue();
			if (hyponyms.size() > 1){
				double score = 0;
				//add scores weighted by reciprocalRank
				for (String hyponym : hyponyms){
					score += labelCandidates.get(hyponym) * getReciprocalRank(labelCandidates, hyponym);	
				}
				//create new candidate (take first word from hypernym list here)
				String newCandidate;
				newCandidate = hypernym.getKey().getTargetSynset().getWords()[0].getLemma();
				candidateHypernyms.put(newCandidate, score);
			}
		}
		} catch (JWNLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return candidateHypernyms;
	}
	
	/**
	 * Determine the type of a given word according to the stanford lexical parser.
	 * @param word of which to determine the type
	 * @return the type of the given word
	 */
	public String tag(String word) {
		if(!word.isEmpty()) {
			List<CoreLabel> rawText = tokenizerFactory.getTokenizer(new StringReader(word.toLowerCase())).tokenize();
			Tree tree = ((Tree) parser.apply(rawText));
		    List<TaggedWord> taggedWords = tree.taggedYield();
			return taggedWords.get(0).tag();
		}
	    return null;
	}	
	
	/**
	 * Returns the reciprocal rank of a given label candidate according to its belonging set.
	 * @param labelCandidates overall set of label candidates
	 * @param label candidate for which to find the reciprocal rank
	 * @return the given label candidate's reciprocal rank
	 */
	public double getReciprocalRank(HashMap<String, Double> labelCandidates, String label){
		double rank = 1;
		double score = labelCandidates.get(label);
		for (Entry<String, Double> candidate : labelCandidates.entrySet()){
			if (candidate.getValue() > score){
				rank += 1;
			}
		}
		
		return rank/labelCandidates.size();
	}

}
