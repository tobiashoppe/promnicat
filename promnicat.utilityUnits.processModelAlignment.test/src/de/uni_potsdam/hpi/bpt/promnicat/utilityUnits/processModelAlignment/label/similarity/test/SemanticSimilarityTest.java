package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.similarity.test;

import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.jbpt.alignment.LabelEntity;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.similarity.SemanticSimilarity;

public class SemanticSimilarityTest extends SimilarityTest<SemanticSimilarity> {

	private static final SemanticSimilarity semSim = new SemanticSimilarity();
	private static Level level;
	private static final Logger logger = Logger.getLogger(SemanticSimilarityTest.class.getName());

	public SemanticSimilarityTest() {
		super(semSim);
	}
	/**
	 * "Entity" is a special case, since it is the root for all hypernym trees 
	 */
	@Test
	public void testEntity() {
		LabelEntity label1 = new LabelEntity("entity");
		LabelEntity label2 = new LabelEntity("entity");
		float sim = semSim.compute(label1, label2);
		Assert.assertEquals(1.0, sim, 0.0);
	}
		
	/**
	 * we override the basic test, because semantic similarity works a bit different ^^
	 */
	@Override
	public void testComputeDistinction() {
		LabelEntity label1 = new LabelEntity("fish");
		LabelEntity label2 = new LabelEntity("idea");
		float sim = semSim.compute(label1, label2);
		Assert.assertEquals(0.0, sim, 0.0);
	}

	/**
	 * This test calculates the similarity of several word pairs
	 * which are listed in "Definition of Similarity" by Dekang Lin.
	 * The paper defines a correlation to the standard of "Miller & Charles"
	 * (a human-judgment experimental similarity)
	 */
	@Test
	public void testCorrelation() {
		disableLogger();
		String[] first = new String[] {
				"car","gem","journey","boy","coast","asylum","magician","midday","furnace","food","bird","bird","tool","brother","crane","lad","journey","monk","food","coast","forest","monk","coast","lad","chord","glass","noon","rooster"
		};
		String[] second = new String[] {
				"automobile","jewel","voyage","lad","shore","madhouse","wizard","noon","stove","fruit","cock","crane","implement","monk","implement","brother","car","oracle","rooster","hill","graveyard","slave","forest","wizard","smile","magician","string","voyage"
		};
		System.out.println("Lengths: "+first.length+";"+second.length);
		for (int i = 0; i < first.length; i++) {
			float sim = semSim.compute(new LabelEntity(first[i]), new LabelEntity(second[i]));
			System.out.println(sim);
		}
		enableLogger();
	}
	
	private static void disableLogger() {
		level = logger.getLevel();
		logger.setLevel(Level.OFF);
	}
	
	private static void enableLogger() {
		logger.setLevel(level);
	}
	
}
