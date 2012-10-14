package de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.tests;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.wordNet.WordNet;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;


public class WordNetTest {

	private static WordNet wordNet;
	private static String word;
	private static String pos;
	private static Synset[] synsets;
	private String[] wordForms;
	private String[] usageExamples;
	private String definitions;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		wordNet = new WordNet();
		word = "bank";
		pos = "noun";
		synsets = wordNet.synsetsOf(word, pos);
	}
	
	@Test
	public void variousTests()  {
		for (int i=0; i<synsets.length; i++) {
			System.out.println(synsets[i].getDefinition());
			System.out.println(((NounSynset) synsets[i]).getUsages());
		}
	}

	@Test
	public void testSynsetsOf() {
		assertEquals(10, synsets.length);
	}
	
	@Test
	public void testGetWordForms() {
		wordForms = synsets[1].getWordForms();
		assertEquals("depository financial institution", wordForms[0]);
	}
	
	@Test
	public void testGetUsageExamples() {
		usageExamples = synsets[1].getUsageExamples();
		assertEquals("\"that bank holds the mortgage on my home\"", usageExamples[1]);
	}
	
	@Test
	public void testGetDefinition() {
		definitions = synsets[1].getDefinition();
		assertEquals("a financial institution that accepts deposits and " +
				"channels the money into lending activities", definitions);
	}

}
