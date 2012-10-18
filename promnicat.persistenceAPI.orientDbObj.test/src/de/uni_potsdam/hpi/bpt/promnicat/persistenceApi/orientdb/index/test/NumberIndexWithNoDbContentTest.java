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
package de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdb.index.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.jbpt.pm.epc.Function;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IModel;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IRepresentation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdb.test.ModelFactory;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdb.test.RepresentationFactory;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.impl.PersistenceApiOrientDbObj;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.index.IndexElement;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.index.NumberIndex;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.pojos.LabelStorage;
import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;

/**
 * Test class for {@link NumberIndex}.
 * @author Andrina Mascher
 *
 */
public class NumberIndexWithNoDbContentTest {
	
	private static PersistenceApiOrientDbObj papi;
	private static NumberIndex<Float, IRepresentation> nIndex = null;
	static String mockModelId, mockRepresentationId, mockRepresentationId2;
	
	@BeforeClass
	public static void setUpClass() {
		try {
			papi = PersistenceApiOrientDbObj.getInstance(Constants.TEST_DB_CONFIG_PATH);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	
	@Before
	public void setUp(){
		papi.openDb();
		
		IModel mockModel = ModelFactory.createModelWithMultipleLinks();
		mockModelId = papi.savePojo(mockModel);
		IRepresentation mockRepresentation = RepresentationFactory.createLightweightRepresentation();
		mockRepresentationId = papi.savePojo(mockRepresentation);
		IRepresentation mockRepresentation2 = RepresentationFactory.createLightweightRepresentation();
		mockRepresentationId2 = papi.savePojo(mockRepresentation2);
		
		nIndex = new NumberIndex<Float, IRepresentation>("myTestNumberIndex",papi);
	}
	
	@After
	public void tearDown(){
		try{
			papi.dropDb();
		} catch (Exception e){
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testAdd(){
		try{
			nIndex.createIndex();
			assertEquals(0,nIndex.load().size());
			nIndex.add(1.2f, mockRepresentationId);
			List<IndexElement<Float, IRepresentation>> result = nIndex.load();
			assertEquals(1,result.size());
			IndexElement<Float, IRepresentation> e = result.get(0);
			assertEquals(new Float(1.2), e.getKey());
			assertEquals(mockRepresentationId, e.getDbId());
			assertEquals(RepresentationFactory.createLightweightRepresentation().getTitle(), e.getPojo().getTitle());
		} catch(Exception e) {
			fail();
		}

		//wrong input
		try{
			nIndex.add(null, mockRepresentationId);
			fail();
		} catch(IllegalArgumentException e) {
			assert(true);
		}
		try{
			nIndex.add(3.4f, "a wrong id");
			fail();
		} catch(IllegalArgumentException e) {
			assert(true);
		}
	}
	
	@Test
	public void testPojo(){
		NumberIndex<Double,LabelStorage> pojoIndex = new NumberIndex<Double, LabelStorage>("testPojoIndex", papi);
		try{
			pojoIndex.createIndex();
			LabelStorage pojoBefore = new LabelStorage("a label", Function.class.getSimpleName());
			papi.savePojo(pojoBefore);
			pojoIndex.add(5.3, pojoBefore.getDbId());
			List<IndexElement<Double,LabelStorage>> results = pojoIndex.load();
			assertEquals(1, results.size());
			LabelStorage pojo = results.get(0).getPojo();
			assertNotNull(pojo);
			assertEquals(pojoBefore.getLabel(), pojo.getLabel());
			assertEquals(pojoBefore.getClassName(), pojo.getClassName());
		} catch(Exception e) {
			fail();
		}
	}

	@Test
	public void testCreateIndex(){		
		//before creation
		try{
			nIndex.add(1.2f, mockRepresentationId);
			fail();
		} catch(IllegalStateException e) {
			assert(true);
		}

		try{
			nIndex.clearIndex(); 
			fail();
		} catch(IllegalStateException e) {
			assert(true);
		}
		
		nIndex.dropIndex();
		
		//after creation
		nIndex.createIndex();
		nIndex.clearIndex();
		nIndex.dropIndex();
	}
	
	@Test
	public void testDropIndex(){
		nIndex.createIndex();
		nIndex.add(1.2f, mockRepresentationId);
		nIndex.add(1.3f, mockRepresentationId2);	
		assertEquals(2,nIndex.load().size());
		nIndex.dropIndex();
		try{
			assertEquals(0,nIndex.load().size());
			fail();
		} catch(IllegalStateException e) {
			assert(true);
		}
		nIndex.dropIndex();
	}
	
	@Test
	public void testClearIndex(){
		nIndex.createIndex();
		nIndex.add(1.2f, mockRepresentationId);
		nIndex.add(1.3f, mockRepresentationId2);	
		assertEquals(2,nIndex.load().size());
		nIndex.clearIndex();
		assertEquals(0,nIndex.load().size());
		nIndex.clearIndex();
		assertEquals(0,nIndex.load().size());
	}
}
