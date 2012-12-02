package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.preprocessing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.logging.Logger;

/**
 * Removes all words matching an entry in the stopword list.<br>
 * By default, it uses an english stop word list, but others can be specified.
 * @see #load(StopWordList)
 * @see #load(String)
 * @author stefan.schaefer
 */
public class StopWords extends TokenBasedPreProcessingStep {
	/** stopword lists which are provided by default */
	public enum StopWordList {
		/** A list of english stop words by Ranks.nl */
		EN("stopwords/EN.txt"),
		/** A list of english stop words, a little more extensive than EN.txt */
		EN_extended("stopwords/EN_extended.txt");
		
		private final String path;
		StopWordList(String path) {
			this.path = path;
		}
	}
	
	/** The stopwords are parsed when processing labels.
	 * You can directly manipulate this set to add custom words */
	public final LinkedHashSet<String> stopwords = new LinkedHashSet<String>();

	@Override
	public String process(String in) {
		if (stopwords.isEmpty()) {
			load(StopWordList.EN);
		}
		return super.process(in);
	}

	/** {@link #load(String)} a list defined in the enumeration */
	public void load(StopWordList list) {
		load(list.path);
	}

	/** add a list of stop words to the {@link #stopwords}
	 * @param path the path to the */
	public void load(String path) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(path));
			String strLine;
			while ((strLine = in.readLine()) != null)   {
				stopwords.add(strLine);
			}
		} catch (FileNotFoundException e) {
			Logger.getLogger(getClass().getName()).severe("StopWord file not found: " + e.getMessage());
		} catch (IOException e) {
			Logger.getLogger(getClass().getName()).severe("StopWord file could not be read: " + e.getMessage());
		}
	}

	@Override
	protected String processWord(String word) {
		if (stopwords.contains(word)) {
			return null;
		}
		return word;
	}
}
