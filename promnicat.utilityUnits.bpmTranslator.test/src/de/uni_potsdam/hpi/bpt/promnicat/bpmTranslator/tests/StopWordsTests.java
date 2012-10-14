package de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.wordSenseDisambiguation.StopWords;

public class StopWordsTests {

	private static ArrayList<String> stopWords;
	private static StopWords sw;
	
	@Test
	public void test() {
		sw = new StopWords();
		stopWords = new ArrayList<String>();
		stopWords.add("this");
		stopWords.add("is");
		stopWords.add("a");
		stopWords.add("test");
		
		assertEquals(stopWords, sw.getStopWords("test"));
	}

}
