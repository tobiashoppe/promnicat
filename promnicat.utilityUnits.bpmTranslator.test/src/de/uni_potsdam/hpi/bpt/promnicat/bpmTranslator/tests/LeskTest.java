package de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.wordNet.WordNet;
import de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.wordSenseDisambiguation.Lesk;
import de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.wordSenseDisambiguation.StopWords;

public class LeskTest {
	
	private static Lesk lesk;
	private static String sentence;
	private static String word;
	private static String sourceLanguage;
	private static String targetLanguage;
	private static ArrayList<String> checkedWords;
	private static StopWords stopWordsGetter;
	private static ArrayList<String> sourceLanguageStopWords;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sourceLanguage = "english";
		targetLanguage = "german";
		lesk = new Lesk(sourceLanguage, targetLanguage);
		sentence = "The bank can guarantee deposits will eventually " +
				"cover future tuition costs because it invests in " +
				"adjustable-rate mortgage securities.";
		word = "bank";
		stopWordsGetter = new StopWords();
		sourceLanguageStopWords = stopWordsGetter.getStopWords(sourceLanguage);
	}
	
    @Before
    public void setUp() throws Exception {
        checkedWords = new ArrayList<String>();
    }
    
    @Test
    public void testDisambiguation() {
    	String expected = "a financial institution that accepts deposits " +
				"and channels the money into lending activities";
    	assertEquals(expected, lesk.disambiguate(word, "noun", sentence));
    }

	@Test
	public void testOverlap() {
		WordNet wordNet = new WordNet();
		assertEquals(3, lesk.computeOverlap(wordNet.synsetsOf(word, "noun")[1], sentence));
	}
	
	@Test
	public void testWordFormOverlap() {
		String[] wordForms = {"savings bank", "coin bank", "money box", "bank", "deposits"};
		assertEquals(2, lesk.computeWordFormOverlap(wordForms, sentence, checkedWords, sourceLanguageStopWords));
	}
	
	@Test
	public void testExamplesOverlap() {
		String[] examples = {"\"he cashed a check at the bank\"", "\"that bank holds the mortgage on my home\""};
		assertEquals(2, lesk.computeExamplesOverlap(examples, sentence, checkedWords, sourceLanguageStopWords));
	}

	@Test
	public void testDefinitionOverlap() {
		String definition = "a financial institution that accepts deposits " +
				"and channels the money into lending activities";
		assertEquals(1, lesk.computeDefinitionOverlap(definition, sentence, checkedWords, sourceLanguageStopWords));
	}
	
	@Test
	public void testSentenceOverlap() {
		String firstSentence = "They pulled the canoe up on the bank";
		String secondSentence = "They pulled the canoe up on the bank";
		assertEquals(9, lesk.computeSentenceOverlap(firstSentence, secondSentence, sourceLanguageStopWords));
	}

	@Test
	public void testBestTranslation() {
		ArrayList<String> translationCandidates1 = new ArrayList<String>();
		ArrayList<String> translationCandidates2 = new ArrayList<String>();
		translationCandidates1.add("Schach Verfügbarkeit");
		translationCandidates1.add("Schach halten Verfügbarkeit");
		translationCandidates1.add("überprüfen , die Verfügbarkeit");
		translationCandidates2.add("um eine schriftliche erarbeitete eine Bank zu zahlen , die Geld");
		translationCandidates2.add("um eine schriftliche erarbeitete eine Bank zu zahlen , die Geld");
		translationCandidates2.add("um eine schriftliche erarbeitete eine Bank zu zahlen , die Geld");
		
		assertEquals("", lesk.computeBestTranslation(translationCandidates1, translationCandidates2));
		
	}
	
	@Test
	public void testLongestCommonSubstring() {
		assertEquals(1, lesk.longestCommonWordSequence("the students soon are in time", "the students are in shape", checkedWords, sourceLanguageStopWords));
	}
	
}
