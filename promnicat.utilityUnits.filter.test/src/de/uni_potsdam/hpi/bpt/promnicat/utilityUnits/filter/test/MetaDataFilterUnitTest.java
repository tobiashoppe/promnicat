/**
 * PromniCAT - Collection and Analysis of Business Process Models
 * Copyright (C) 2012 Cindy Fähnrich, Tobias Hoppe, Andrina Mascher
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.filter.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.configuration.ConfigurationParser;
import de.uni_potsdam.hpi.bpt.promnicat.importer.npb.NPBImporter;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPersistenceApi;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.filter.MetaDataFilterUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitDataMetaData;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.UnitDataMetaData;

/**
 * Test class for {@link MetaDataFilterUnit}
 * @author Tobias Hoppe
 *
 */
public class MetaDataFilterUnitTest {
	
	private static IPersistenceApi persistenceApi;
	
	@BeforeClass
	public static void setUp(){
		try{
			persistenceApi = new ConfigurationParser(Constants.TEST_DB_CONFIG_PATH).getDbInstance();
			new NPBImporter(persistenceApi).importModelsFrom("../promnicat/resources/NPB/Begruenden.xml");
			persistenceApi.openDb();
		} catch (Exception e){
			fail("An unexpected exception occurred:" + e.getMessage());
		}
	}
	
	@AfterClass
	public static void tearDown(){
		try{
			persistenceApi.dropDb();
		} catch (Exception e){
			fail("An unexpected exception occurred:" + e.getMessage());
		}
	}
	@Test
	public void testGetName(){
		assertTrue(new MetaDataFilterUnit(null, Pattern.compile("\\*")).getName().equals("MetaDataFilterUnit"));
	}
	
	@Test
	public void testFindValue(){
		MetaDataFilterUnit unit = new MetaDataFilterUnit(null, Pattern.compile("[a-z]+"));
		try{
			Representation representation = (Representation) persistenceApi.loadPojos(Representation.class).get(0);

			IUnitData<Object> input = new UnitDataMetaData<Object>(representation);
			IUnitData<Object> result = unit.execute(input);
	
			assertTrue(((IUnitDataMetaData<Object>) result).getMetaData() instanceof Map);
			Map<String, Collection<String>> metaData = ((IUnitDataMetaData<Object>) result).getMetaData();
			assertTrue(metaData.keySet().size() == 1);
			assertTrue(metaData.containsKey("tags"));
		}catch (Exception e){
			fail("Unexpected exception occurred: " + e.getMessage());
		}	
	}
	
	@Test
	public void testFindKey(){
		MetaDataFilterUnit unit = new MetaDataFilterUnit(Pattern.compile("tags"), null);
		try{
			Representation representation = (Representation) persistenceApi.loadPojos(Representation.class).get(0);

			IUnitData<Object> input = new UnitDataMetaData<Object>(representation);
			IUnitData<Object> result = unit.execute(input);
	
			assertTrue(((IUnitDataMetaData<Object>) result).getMetaData() instanceof Map);
			Map<String, Collection<String>> metaData = ((IUnitDataMetaData<Object>) result).getMetaData();
			assertTrue(metaData.keySet().size() == 1);
			assertTrue(metaData.containsKey("tags"));
		}catch (Exception e){
			fail("Unexpected exception occurred: " + e.getMessage());
		}	
	}
	
	@Test
	public void testFindValueFromString(){
		MetaDataFilterUnit unit = new MetaDataFilterUnit(null, "antrag");
		try{
			Representation representation = (Representation) persistenceApi.loadPojos(Representation.class).get(0);

			IUnitData<Object> input = new UnitDataMetaData<Object>(representation);
			IUnitData<Object> result = unit.execute(input);
	
			assertTrue(((IUnitDataMetaData<Object>) result).getMetaData() instanceof Map);
			Map<String, Collection<String>> metaData = ((IUnitDataMetaData<Object>) result).getMetaData();
			assertTrue(metaData.keySet().size() == 1);
			assertTrue(metaData.containsKey("tags"));
		}catch (Exception e){
			fail("Unexpected exception occurred: " + e.getMessage());
		}	
	}
	
	@Test
	public void testFindKeyFromString(){
		MetaDataFilterUnit unit = new MetaDataFilterUnit("tags", null);
		try{
			Representation representation = (Representation) persistenceApi.loadPojos(Representation.class).get(0);

			IUnitData<Object> input = new UnitDataMetaData<Object>(representation);
			IUnitData<Object> result = unit.execute(input);
	
			assertTrue(((IUnitDataMetaData<Object>) result).getMetaData() instanceof Map);
			Map<String, Collection<String>> metaData = ((IUnitDataMetaData<Object>) result).getMetaData();
			assertTrue(metaData.keySet().size() == 1);
			assertTrue(metaData.containsKey("tags"));
		}catch (Exception e){
			fail("Unexpected exception occurred: " + e.getMessage());
		}	
	}
}
