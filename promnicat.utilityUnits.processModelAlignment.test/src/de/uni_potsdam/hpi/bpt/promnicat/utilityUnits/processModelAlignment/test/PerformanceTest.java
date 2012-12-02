package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jbpt.alignment.LabelEntity;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.assignment.DirectAssignment;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.LabelAlignment;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.similarity.StringEditDistance;

public class PerformanceTest {
	private static int TESTDATA_COUNT = 50;
	private static int TESTWORD_LENGTH = 10;
	private static ArrayList<LabelEntity> set1 = new ArrayList<LabelEntity>();
	private static ArrayList<LabelEntity> set2 = new ArrayList<LabelEntity>();
	private static Random rand = new Random();
	private static Level level = Level.ALL;
	private static final Logger logger = Logger.getLogger(PerformanceTest.class.getName());

	@BeforeClass
	public static void setUp() {
		disableLogger();
		if (set1.size() != TESTDATA_COUNT) {
			generateTestData();
		}
	}
	
	@AfterClass
	public static void tearDown() {
		enableLogger();
	}
	
	private static void disableLogger() {
		level = logger.getLevel();
		logger.setLevel(Level.OFF);
	}
	
	private static void enableLogger() {
		logger.setLevel(level);
	}
	
	private static void generateTestData() {
		set1.clear();
		set2.clear();
		for (int i=0 ; i < TESTDATA_COUNT ; i++) {
			LabelEntity word1 = getNewWord();
			LabelEntity word2 = getNewWord();
			set1.add(word1);
			set2.add(word2);
		}
	}

	private static LabelEntity getNewWord() {
		String word = "";
		int wordLength = rand.nextInt(TESTWORD_LENGTH);
		for (int j=0 ; j < wordLength || j < 3 ; j++) {
			int nextInt = rand.nextInt(26 + 26 + 10);
			if (nextInt < 26) { // (0..25)
				word += (char) ('a' + nextInt);
			} else if (nextInt < 52) {
				nextInt -= 26; // (0..25)
				word += (char) ('A' + nextInt);
			} else {
				nextInt -= 52; // (0..9)
				word += (char) ('0' + nextInt);
			}
		}
		return new LabelEntity(word);
	}
	
	
	@Test
	public void testPerformance() {
		LabelAlignment labelAlignment = new LabelAlignment();
		labelAlignment.setSimilarity(new StringEditDistance());
		labelAlignment.setAssignment(new DirectAssignment());
		
		Date start = new Date();
		labelAlignment.align(set1, set2);
		Date end = new Date();
		System.out.println(end.getTime() - start.getTime());
	}

}
