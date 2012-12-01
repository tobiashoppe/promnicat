package de.uni_potsdam.hpi.bpt.promnicat.persistenceAPI.db4o.test.all;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceAPI.db4o.test.DbFilterConfigUsageTest;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceAPI.db4o.test.PersistenceApiDb4oEmptyContentTest;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceAPI.db4o.test.PersistenceApiDb4oTest;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceAPI.db4o.test.PersistenceApidb4oStableContentTest;

@RunWith(Suite.class)
@SuiteClasses({DbFilterConfigUsageTest.class,
	PersistenceApiDb4oEmptyContentTest.class,
	PersistenceApidb4oStableContentTest.class,
	PersistenceApiDb4oTest.class})
public class AllTests {

}
