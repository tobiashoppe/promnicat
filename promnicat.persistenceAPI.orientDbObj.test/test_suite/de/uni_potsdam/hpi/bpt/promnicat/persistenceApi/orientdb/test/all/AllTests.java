package de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdb.test.all;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdb.index.test.IntersectionIndexTest;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdb.index.test.NumberIndexWithNoDbContentTest;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdb.index.test.NumberIndexWithStableDbContentTest;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdb.index.test.StringIndexWithNoDbContentTest;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdb.index.test.StringIndexWithStableDbContentTest;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdb.test.DbFilterConfigUsageTest;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdb.test.PersistenceApiOrientDbEmptyContentTest;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdb.test.PersistenceApiOrientDbStableContentTest;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdb.test.PersistenceApiOrientDbTest;

@RunWith(Suite.class)
@SuiteClasses({ DbFilterConfigUsageTest.class, PersistenceApiOrientDbEmptyContentTest.class,
		PersistenceApiOrientDbStableContentTest.class, PersistenceApiOrientDbTest.class, IntersectionIndexTest.class,
		NumberIndexWithNoDbContentTest.class, NumberIndexWithStableDbContentTest.class,
		StringIndexWithNoDbContentTest.class, StringIndexWithStableDbContentTest.class })
public class AllTests {

}
