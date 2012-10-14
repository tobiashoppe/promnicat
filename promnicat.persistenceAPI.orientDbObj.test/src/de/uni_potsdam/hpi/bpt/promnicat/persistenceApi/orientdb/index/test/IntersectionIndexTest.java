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
import static org.junit.Assert.fail;

import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.Model;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdb.test.ModelFactory;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdb.test.RepresentationFactory;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.PersistenceApiOrientDbObj;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.index.IndexCollectionElement;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.index.IndexIntersection;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.index.NumberIndex;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.index.StringIndex;
import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;

/**
 * Test class for {@link NumberIndex}.
 * @author Andrina Mascher
 *
 */
public class IntersectionIndexTest {
	
	private static PersistenceApiOrientDbObj papi;
	private static NumberIndex<Float, Representation> nIndex1, nIndex2 = null;
	private static StringIndex<Representation> sRIndex = null;
	private static StringIndex<Model> sMIndex = null;
	private static IndexIntersection<Representation> intersection = null;
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
		
		Model mockModel = ModelFactory.createModelWithMultipleLinks();
		mockModelId = papi.savePojo(mockModel);
		Representation mockRepresentation = RepresentationFactory.createLightweightRepresentation();
		mockRepresentationId = papi.savePojo(mockRepresentation);
		Representation mockRepresentation2 = RepresentationFactory.createLightweightRepresentation();
		mockRepresentationId2 = papi.savePojo(mockRepresentation2);
		
		nIndex1 = new NumberIndex<Float, Representation>("myTestNumber1Index",papi);
		nIndex2 = new NumberIndex<Float, Representation>("myTestNumber2Index",papi);
		sRIndex = new StringIndex<Representation>("myTestStringIndex", papi);
		sMIndex = new StringIndex<Model>("myTestStringModelIndex", papi);
		nIndex1.createIndex();
		nIndex2.createIndex();
		sRIndex.createIndex();
		sMIndex.createIndex();
		intersection = new IndexIntersection<Representation>(papi);
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
			nIndex1.add(1.4f, mockRepresentationId);
			nIndex1.add(1.5f, mockRepresentationId2);
			nIndex2.add(1.6f, mockRepresentationId2);
			
			sRIndex.add("hello", mockRepresentationId2);
			sRIndex.add("world", mockRepresentationId2);
			sRIndex.add("test 1", mockRepresentationId);
			
			intersection.add(nIndex1);
			intersection.add(nIndex2);
			intersection.add(sRIndex);
			
			//select all from indices and inspect this 1 element
			Collection<IndexCollectionElement<Representation>> result = intersection.load();
			assertEquals(1, result.size());
			IndexCollectionElement<Representation> element = result.iterator().next();
			String dbIdFound = element.getDbId();
			assertEquals(mockRepresentationId2, dbIdFound);
			int numberOfIndices = element.getIndexElements().size();
			assertEquals(4, numberOfIndices);
			
			//one index does not yield any results
			nIndex1.setSelectGreaterOrEquals(2.0f);
			result = intersection.load();
			assertEquals(0, result.size());
			
			//find 2 each
			nIndex1.setSelectAll();
			nIndex2.add(1.7f, mockRepresentationId);
			result = intersection.load();
			assertEquals(2, result.size());
			
		} catch(Exception e) {
			fail();
		}
	}
	
	@Test
	public void testAddWrong(){
		try{
			nIndex1.add(1.4f, mockModelId);
			nIndex1.add(1.5f, mockRepresentationId);

			nIndex2.add(1.6f, mockModelId);
			nIndex2.add(1.6f, mockRepresentationId);

			sRIndex.add("hello", mockModelId);
			sRIndex.add("world", mockRepresentationId);

			sMIndex.add("dummy", mockModelId);
			sMIndex.add("dummy", mockRepresentationId);

			intersection.add(nIndex1);
			intersection.add(nIndex2);
			intersection.add(sRIndex);
			intersection.add(sMIndex);

			Collection<IndexCollectionElement<Representation>> result = intersection.load();
			assertEquals(2, result.size());
			for(IndexCollectionElement<Representation> element : result) {
				@SuppressWarnings("unused")
				Representation rep = element.getPojo();
			}
			fail();
		} catch(ClassCastException e) {
			assert(true);
		}
	}

}
