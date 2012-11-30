package de.uni_potsdam.hpi.bpt.promnicat.persistenceAPI.db4o.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({DbFilterConfigUsageTest.class,
	PersistenceApiDb4oEmptyContentTest.class,
	PersistenceApidb4oStableContentTest.class,
	PersistenceApiDb4oTest.class})
public class AllTests {

}
