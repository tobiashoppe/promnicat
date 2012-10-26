package de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdb.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdb.index.test.IntersectionIndexTest;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdb.index.test.NumberIndexWithNoDbContentTest;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdb.index.test.NumberIndexWithStableDbContentTest;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdb.index.test.StringIndexWithNoDbContentTest;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdb.index.test.StringIndexWithStableDbContentTest;

@RunWith(Suite.class)
@SuiteClasses({ DbFilterConfigUsageTest.class, PersistenceApiOrientDbEmptyContentTest.class,
		PersistenceApiOrientDbStableContentTest.class, PersistenceApiOrientDbTest.class,
		IntersectionIndexTest.class, NumberIndexWithNoDbContentTest.class,
		NumberIndexWithStableDbContentTest.class, StringIndexWithNoDbContentTest.class,
		StringIndexWithStableDbContentTest.class})
public class AllOrientDBTests {

}
