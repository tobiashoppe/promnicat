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
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;

import org.jbpt.pm.FlowNode;
import org.jbpt.pm.bpmn.Task;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.DbFilterConfig;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPersistenceApi;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.PersistenceApiOrientDbObj;
import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;
import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnitChain;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnitChainBuilder;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.UnitChain;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.UnitChainBuilder;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.collector.ICollectorUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.collector.SimpleCollectorUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.filter.ConnectednessFilterUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.filter.DatabaseFilterUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.transformer.BpmaiJsonToDiagramUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.transformer.DiagramToJbptUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;

/**
 * Test class for {@link UnitChainBuilder}.
 * @author Tobias Hoppe
 *
 */
public class UnitBuilderTest {

	@Test
	public void initializationTest(){
		try {
			IUnitChainBuilder builder = new UnitChainBuilder("", Constants.DATABASE_TYPES.ORIENT_DB, null);
			IUnitChain<IUnitData<Object>,IUnitData<Object>> chain = builder.getChain();
			assertTrue(chain.getFirstUnit() instanceof DatabaseFilterUnit);
			assertTrue(chain.getLastUnit() instanceof ICollectorUnit<?,?>);
			assertTrue(chain.getUnits().size() == 2);
			IPersistenceApi papi = PersistenceApiOrientDbObj.getInstance(Constants.TEST_DB_CONFIG_PATH);
			IUnitChainBuilder builderWithPersistenceApi = new UnitChainBuilder(papi, 0, null);
			IUnitChain<IUnitData<Object>,IUnitData<Object>> chainWithPersistenceApi = builderWithPersistenceApi.getChain();
			assertTrue(chainWithPersistenceApi.getFirstUnit() instanceof DatabaseFilterUnit);
			assertTrue(chainWithPersistenceApi.getLastUnit() instanceof ICollectorUnit<?,?>);
			assertTrue(chainWithPersistenceApi.getUnits().size() == 2);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected Exception occurred: " + e.getMessage());
		}
	}
	
	@Test
	public void addDbFilterConfigTest(){
		try {
			IUnitChainBuilder builder = new UnitChainBuilder("", Constants.DATABASE_TYPES.ORIENT_DB, null);
			IUnit<IUnitData<Object>, IUnitData<Object>> dbUnit = builder.getChain().getFirstUnit();
			assertTrue(dbUnit instanceof DatabaseFilterUnit);
			((DatabaseFilterUnit) dbUnit).setDatabaseConfig(null);
			assertNull(((DatabaseFilterUnit) builder.getChain().getFirstUnit()).getDatabaseConfig());
			builder.addDbFilterConfig(new DbFilterConfig());
			assertNotNull(((DatabaseFilterUnit) builder.getChain().getFirstUnit()).getDatabaseConfig());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected Exception occurred: " + e.getMessage());
		}
	}
	
	@Test
	public void addUnitChainTest(){
		try {
			IUnitChainBuilder builder = new UnitChainBuilder("", Constants.DATABASE_TYPES.ORIENT_DB, null);
			
			//build chain without builder
			IUnitChain<IUnitData<Object>,IUnitData<Object>> chain = new UnitChain(null);
			chain.register(new BpmaiJsonToDiagramUnit());
			chain.register(new DiagramToJbptUnit(false));
			chain.register(new ConnectednessFilterUnit());
			SimpleCollectorUnit collector = new SimpleCollectorUnit();
			chain.register(collector);
			
			builder.addUnitChain(chain);
			Object[] units = builder.getChain().getUnits().toArray();
			assertTrue(units.length == 5);
			assertTrue(units[0] instanceof DatabaseFilterUnit);
			assertTrue(units[1] instanceof BpmaiJsonToDiagramUnit);
			assertTrue(units[2] instanceof DiagramToJbptUnit);
			assertTrue(units[3] instanceof ConnectednessFilterUnit);
			assertTrue(units[4] instanceof SimpleCollectorUnit);
			assertSame(collector, units[4]);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected Exception occurred: " + e.getMessage());
		}
	}
	
	@Test
	public void buildChainTest(){
		try {
			IUnitChainBuilder builder = new UnitChainBuilder("", Constants.DATABASE_TYPES.ORIENT_DB, null);
			//build chain
			builder.createBpmaiJsonToJbptUnit();
			builder.createProcessModelFilterUnit(FlowNode.class);
			builder.createProcessModelFilterUnit(new Task());
			builder.createProcessModelFilterUnit(new ArrayList<Class<?>>(), new ArrayList<Class<?>>());
			builder.createConnectednessFilterUnit();
			builder.createProcessModelLabelExtractorUnit();
			builder.createLabelFilterUnit("");
			
			assertTrue(builder.getChain().getUnits().size() == 10);
			assertTrue(builder.getChain().getLastUnit() instanceof ICollectorUnit<?,?>);
			assertTrue(builder.getChain().getFirstUnit() instanceof DatabaseFilterUnit);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected error: " + e.getMessage());
		}
	}
	
	@Test
	public void buildErrorFullChainTest(){
		IUnitChainBuilder builder = null;		
		try {
			builder = new UnitChainBuilder("", Constants.DATABASE_TYPES.ORIENT_DB, null);
			//build chain
			builder.createBpmaiJsonToJbptUnit();
			builder.createLabelFilterUnit("");
			
			fail("An expected error has not been thrown!");
		} catch (IllegalTypeException e) {
			assertTrue(builder.getChain().getUnits().size() == 4);
			assertTrue(builder.getChain().getLastUnit() instanceof ICollectorUnit<?,?>);
		} catch (IOException e) {
			e.printStackTrace();
			fail("Unexpected error: " + e.getMessage());
		}
	}
	
	@Test
	public void buildErrorFullChainTest2(){
		IUnitChainBuilder builder = null;		
		try {
			builder = new UnitChainBuilder("", Constants.DATABASE_TYPES.ORIENT_DB, null);
			//build chain
			builder.createLabelFilterUnit("");
			
			fail("An expected error has not been thrown!");
		} catch (IllegalTypeException e) {
			assertTrue(builder.getChain().getUnits().size() == 2);
			assertTrue(builder.getChain().getLastUnit() instanceof ICollectorUnit<?,?>);
		} catch (IOException e) {
			e.printStackTrace();
			fail("Unexpected error: " + e.getMessage());
		}
	}
	
	@Test
	public void buildErrorFullChainTest3(){
		IUnitChainBuilder builder = null;		
		try {
			builder = new UnitChainBuilder("", Constants.DATABASE_TYPES.ORIENT_DB, null);
			//build chain
			builder.createProcessModelFilterUnit(new Task());
			
			fail("An expected error has not been thrown!");
		} catch (IllegalTypeException e) {
			assertTrue(builder.getChain().getUnits().size() == 2);
			assertTrue(builder.getChain().getLastUnit() instanceof ICollectorUnit<?,?>);
		} catch (IOException e) {
			e.printStackTrace();
			fail("Unexpected error: " + e.getMessage());
		}
	}
	
	@Test
	public void buildErrorFullChainTest4(){
		IUnitChainBuilder builder = null;		
		try {
			builder = new UnitChainBuilder("", Constants.DATABASE_TYPES.ORIENT_DB, null);
			//build chain
			builder.createConnectednessFilterUnit();
			
			fail("An expected error has not been thrown!");
		} catch (IllegalTypeException e) {
			assertTrue(builder.getChain().getUnits().size() == 2);
			assertTrue(builder.getChain().getLastUnit() instanceof ICollectorUnit<?,?>);
		} catch (IOException e) {
			e.printStackTrace();
			fail("Unexpected error: " + e.getMessage());
		}
	}
	
	@Test
	public void buildErrorFullChainTest5(){
		IUnitChainBuilder builder = null;		
		try {
			builder = new UnitChainBuilder("", Constants.DATABASE_TYPES.ORIENT_DB, null);
			//build chain
			builder.createBpmaiJsonToJbptUnit();
			builder.createBpmaiJsonToJbptUnit();
			
			fail("An expected error has not been thrown!");
		} catch (IllegalTypeException e) {
			assertTrue(builder.getChain().getUnits().size() == 4);
			assertTrue(builder.getChain().getLastUnit() instanceof ICollectorUnit<?,?>);
		} catch (IOException e) {
			e.printStackTrace();
			fail("Unexpected error: " + e.getMessage());
		}
	}

	@Test
	public void buildErrorFullChainTest6(){
		IUnitChainBuilder builder = null;		
		try {
			builder = new UnitChainBuilder("", Constants.DATABASE_TYPES.ORIENT_DB, null);
			//build chain
			builder.createBpmaiJsonToJbptUnit();
			builder.createProcessModelLabelExtractorUnit();
			builder.createConnectednessFilterUnit();
			
			fail("An expected error has not been thrown!");
		} catch (IllegalTypeException e) {
			assertTrue(builder.getChain().getUnits().size() == 5);
			assertTrue(builder.getChain().getLastUnit() instanceof ICollectorUnit<?,?>);
		} catch (IOException e) {
			e.printStackTrace();
			fail("Unexpected error: " + e.getMessage());
		}
	}
	
	@Test
	public void buildErrorFullChainTest7(){
		IUnitChainBuilder builder = null;		
		try {
			builder = new UnitChainBuilder("", Constants.DATABASE_TYPES.ORIENT_DB, null);
			//build chain
			builder.createProcessModelLabelExtractorUnit();
			
			fail("An expected error has not been thrown!");
		} catch (IllegalTypeException e) {
			assertTrue(builder.getChain().getUnits().size() == 2);
			assertTrue(builder.getChain().getLastUnit() instanceof ICollectorUnit<?,?>);
		} catch (IOException e) {
			e.printStackTrace();
			fail("Unexpected error: " + e.getMessage());
		}
	}
	
	@Test
	public void buildErrorFullChainTest8(){
		IUnitChainBuilder builder = null;		
		try {
			builder = new UnitChainBuilder("", Constants.DATABASE_TYPES.ORIENT_DB, null);
			//build chain
			builder.createElementExtractorUnit(FlowNode.class);
			
			fail("An expected error has not been thrown!");
		} catch (IllegalTypeException e) {
			assertTrue(builder.getChain().getUnits().size() == 2);
			assertTrue(builder.getChain().getLastUnit() instanceof ICollectorUnit<?,?>);
		} catch (IOException e) {
			e.printStackTrace();
			fail("Unexpected error: " + e.getMessage());
		}
	}
	
}
