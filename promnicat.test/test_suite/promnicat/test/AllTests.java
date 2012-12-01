package promnicat.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	de.uni_potsdam.hpi.bpt.promnicat.persistenceAPI.test.all.AllTests.class,
	de_uni_potsdam.hpi.bpt.promnicat.importer.test.all.AllTests.class,
	de.uni_potsdam.hpi.bpt.promnicat.persistenceAPI.db4o.test.all.AllTests.class,
	//FIXME uncomment if tests get working
//	de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdb.test.all.AllTests.class,
	de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.collector.test.all.AllTests.class,
	de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.extractor.test.all.AllTests.class,
	de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.filter.test.all.AllTests.class,
	de.uni_potsdam.hpi.bpt.promnicat.modelConverter.test.all.AllTests.class
})
public class AllTests {

}
