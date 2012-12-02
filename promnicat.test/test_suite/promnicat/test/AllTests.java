package promnicat.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	de_uni_potsdam.hpi.bpt.promnicat.importer.test.all.AllTests.class,
	de.uni_potsdam.hpi.bpt.promnicat.persistenceAPI.db4o.test.all.AllTests.class,
	//FIXME uncomment if tests get working
//	de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdb.test.all.AllTests.class,
	de.uni_potsdam.hpi.bpt.promnicat.persistenceAPI.test.all.AllTests.class,
//	de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.tests.all.AllTests.class,
	de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.builder.test.all.AllTests.class,
	de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.clustering.test.all.AllTests.class,	
	de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.collector.test.all.AllTests.class,
	de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.extractor.test.all.AllTests.class,
	de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.filter.test.all.AllTests.class,
	de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processMetrics.test.all.AllTests.class,
//	de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.test.all.AllTests.class,
	de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelConverter.test.all.AllTests.class,
//	de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.test.all.AllTests.class,
	de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processEvolution.test.all.AllTests.class,
	de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.test.all.AllTests.class,
	de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.transformer.test.all.AllTests.class
})
public class AllTests {

}
