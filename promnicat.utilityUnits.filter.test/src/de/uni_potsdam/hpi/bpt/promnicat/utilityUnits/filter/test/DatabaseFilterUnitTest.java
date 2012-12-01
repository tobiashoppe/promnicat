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

import java.util.Collection;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.config.DbFilterConfig;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;
import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.UnitChain;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.filter.DatabaseFilterUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;

/**
 * @author Cindy Fähnrich, Tobias Hoppe
 *
 */
public class DatabaseFilterUnitTest {
	
	private static DatabaseFilterUnit databaseFilterUnit;

	private static PersistenceApiOrientDbObj persistenceApi;
	
	@BeforeClass
	public static void setUp(){
		try{
			persistenceApi = PersistenceApiOrientDbObj.getInstance(Constants.TEST_DB_CONFIG_PATH);
			
			Model mockModel = ModelFactory.createModelWith1Link();
			persistenceApi.savePojo(mockModel);
			databaseFilterUnit = new DatabaseFilterUnit(persistenceApi);
		} catch (Exception e){
			fail("An unexpected exception occurred:" + e.getMessage());
		}
	}
	
	@AfterClass
	public static void tearDown(){
		try{
			persistenceApi.dropDb();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetName(){
		assertTrue(databaseFilterUnit.getName().equals("DatabaseFilterUnit"));
	}
	
	@Test
	public void testExecute(){
		DbFilterConfig databaseConfig = new DbFilterConfig();
		
		//set up the database configuration
		databaseConfig.addOrigin(Constants.ORIGINS.BPMAI);
		databaseConfig.addNotation(Constants.NOTATIONS.EPC);
		databaseConfig.addFormat(Constants.FORMATS.BPMAI_JSON);
		databaseConfig.setLatestRevisionsOnly(true);
				
		databaseFilterUnit.setDatabaseConfig(databaseConfig);
		
		UnitChain chain = new UnitChain(null);
		chain.register(databaseFilterUnit);
		chain.register(new SimpleCollectorUnit());
		
		try {
			Collection<? extends IUnitData<Object>> result = chain.execute();
			assertEquals(1, result.size());
			IUnitData<Object> labelFilterResult = result.iterator().next();
			assertTrue(labelFilterResult.getValue() instanceof Representation);
			assertEquals(Constants.NOTATIONS.EPC.toString(), ((Representation)labelFilterResult.getValue()).getNotation());
		} catch (IllegalTypeException e) {
			fail("An unexpected exception occurred:" + e.getMessage());
		}
	}
}