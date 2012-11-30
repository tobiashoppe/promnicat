package de_uni_potsdam.hpi.bpt.promnicat.importer.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.uni_potsdam.hpi.bpt.promnicat.importer.aok.test.AokModelImporterTest;
import de.uni_potsdam.hpi.bpt.promnicat.importer.bpmai.test.BpmaiImporterTest;
import de.uni_potsdam.hpi.bpt.promnicat.importer.ibm.test.IBMImporterTest;
import de.uni_potsdam.hpi.bpt.promnicat.importer.npb.test.NPBImporterTest;
import de.uni_potsdam.hpi.bpt.promnicat.importer.sap_rm.test.SapReferenceModelImporterTest;
import de.uni_potsdam.hpi.bpt.promnicat.importer.test.ImporterTest;
import de.uni_potsdam.hpi.bpt.promnicat.importer.test.ModelImporterTest;
import de.uni_potsdam.hpi.bpt.promnicat.parser.test.BpmnParserTest;
import de.uni_potsdam.hpi.bpt.promnicat.parser.test.EpcParserTest;

@RunWith(Suite.class)
@SuiteClasses({
	AokModelImporterTest.class,
	BpmaiImporterTest.class,
	IBMImporterTest.class,
	NPBImporterTest.class,
	SapReferenceModelImporterTest.class,
	ImporterTest.class,
	ModelImporterTest.class,
	BpmnParserTest.class,
	EpcParserTest.class
})
public class AllTests {

}
