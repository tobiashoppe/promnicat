package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.test.all;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.assignment.test.DirectAssignmentTest;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.assignment.test.HungarianAlgorithmTest;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.assignment.test.StableMarriageTest;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.preprocessing.test.FindSynonymsTest;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.preprocessing.test.PairwisePreProcessingStepTest;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.preprocessing.test.PorterStemmingTest;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.preprocessing.test.SinglePreProcessingStepTest;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.preprocessing.test.StopWordsTest;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.preprocessing.test.ToLowerCaseTest;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.similarity.test.JaccardTest;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.similarity.test.JaroWinklerTest;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.similarity.test.NGramTest;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.similarity.test.SemanticSimilarityTest;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.similarity.test.SimilarityTest;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.similarity.test.StringEditDistanceTest;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.test.PerformanceTest;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.test.ProcessModelAlignmentTest;

@RunWith(Suite.class)
@SuiteClasses({
	DirectAssignmentTest.class,
	HungarianAlgorithmTest.class,
	StableMarriageTest.class,
	FindSynonymsTest.class,
	PairwisePreProcessingStepTest.class,
	PorterStemmingTest.class,
	SinglePreProcessingStepTest.class,
	StopWordsTest.class,
	ToLowerCaseTest.class,
	JaccardTest.class,
	JaroWinklerTest.class,
	NGramTest.class,
	SemanticSimilarityTest.class,
	SimilarityTest.class,
	StringEditDistanceTest.class,
	PerformanceTest.class,
	ProcessModelAlignmentTest.class
})
public class AllTests {

}
