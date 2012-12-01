package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.filter.test.all;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.filter.test.ConnectednessFilterUnitTest;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.filter.test.DatabaseFilterUnitTest;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.filter.test.LabelFilterUnitTest;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.filter.test.MetaDataFilterUnitTest;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.filter.test.ProcessModelFilterUnitTest;

@RunWith(Suite.class)
@SuiteClasses({
	ConnectednessFilterUnitTest.class,
	DatabaseFilterUnitTest.class,
	LabelFilterUnitTest.class,
	MetaDataFilterUnitTest.class,
	ProcessModelFilterUnitTest.class
})
public class AllTests {

}
