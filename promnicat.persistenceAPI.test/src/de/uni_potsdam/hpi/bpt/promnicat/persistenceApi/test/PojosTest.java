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
package de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.Model;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.Revision;

/**
 * Test class for methods on {@link PersistanceApiOrientDB} that change database content 
 * such as save and delete.
 * @author Andrina Mascher
 *
 */
public class PojosTest {

	static Model mockModel = null;

	@Test
	public void testSetLatestRevision() {
		Model model = new Model();
		Revision rev1 = new Revision();
		Revision rev2 = new Revision();
		model.connectRevision(rev1);
		model.connectRevision(rev2);
		
		//model has no latest revision
		assertNull(model.getLatestRevision());
		assertFalse(rev1.isLatestRevision());
		assertFalse(rev2.isLatestRevision());
		assertFalse(model.hasDbId());
		
		//no choose 1 latest revision
		model.connectLatestRevision(rev2);
		
		assertTrue(model.getNrOfRevisions() == 2); // not 3 revisions
		assertTrue(model.getLatestRevision() == rev2);
		assertTrue(rev2.isLatestRevision());
		assertFalse(rev1.isLatestRevision());
		
		Revision rev3 = new Revision();
		model.connectLatestRevision(rev3);
		assertTrue(model.getNrOfRevisions() == 3); 
		assertTrue(model.getLatestRevision() == rev3);
		assertTrue(rev3.isLatestRevision());
		assertFalse(rev2.isLatestRevision());
	}
	
	@Test
	public void testConnectRevisions() {
		Model model = new Model();
		Revision rev1 = new Revision();
		model.connectRevision(rev1);
		assertTrue(model.getNrOfRevisions() == 1);
		
		//set empty revisions list
		ArrayList<Revision> list = new ArrayList<Revision>();
		model.setAndConnectRevisions(list);
		assertTrue(model.getNrOfRevisions() == 0);
		assertNull(model.getLatestRevision());
		
		//add 1 revision
		model.connectRevision(rev1);
		assertTrue(model.getNrOfRevisions() == 1);
		
		//set 2 other revisions
		Revision rev2 = new Revision();
		Revision rev3 = new Revision();
		list.add(rev2);
		list.add(rev3);
		model.setAndConnectRevisions(list);
		assertTrue(model.getNrOfRevisions() == 2); //not 3
		assertNull(model.getLatestRevision()); //still no latestRevision set		
	}
	
	@Test
	public void testConnectNull() {
		Model model = new Model();
		Revision rev = new Revision();
		Representation rep = new Representation();
		
		model.connectRevision(rev);
		assertTrue(model.getNrOfRevisions() == 1);
		rev.connectRepresentation(rep);
		assertTrue(rev.getNrOfRepresentations() == 1);

		//set empty revisions list
		model.setAndConnectRevisions(null);
		assertTrue(model.getNrOfRevisions() == 0);
		
		//set empty reps list
		rev.setAndConnectRepresentations(null);
		assertTrue(rev.getNrOfRepresentations() == 0);
	}

	@Test
	public void testUnconnectedObjects() {
		Representation rep = new Representation();
		assertNull(rep.getRevision());
		assertNull(rep.getModel());
		assertNull(rep.getTitle());
		assertNull(rep.getRevisionNumber());
		assertFalse(rep.belongsToLatestRevision());
		assertFalse(rep.hasDataContent());
		
		Revision rev = new Revision();
		assertNull(rev.getModel());
		assertNull(rev.getTitle());
		assert(rev.getNrOfRepresentations() == 0);
		
		//connect both
		rev.connectRepresentation(rep);
		assert(rev.getNrOfRepresentations() == 1);
		//set empty connections
		rev.setAndConnectRepresentations(new ArrayList<Representation>());
		assert(rev.getNrOfRepresentations() == 0);
	}
	
	@Test
	public void testConnectionDeferment() {
		Representation rep = new Representation();
		Revision rev = new Revision();
		Model mod = new Model();
		rep.connectRevision(rev);
		rev.connectModel(mod);
		assert(rep.getRevision() == rev);
		assert(rev.getModel() == mod);
		assert(rep.getModel() == mod);
	}
	
	@Test
	public void testMetadata() {
		Revision r = new Revision();
		r.setMetadata(null);
		assert(r.getMetadata().size() == 0);
		r.addMetadataAtKey("k1", "v1");
		r.addMetadataAtKey("k1", "v2");
		String[] readValues = r.getMetadataAtKey("k1");
		assert(r.getMetadata().size() == 1);
		assert(readValues.length == 2);
		
		//no new key
		String[] values = {"",""};
		r.setMetadataAtKey("", values);
		assert(r.getMetadata().size() == 1);
		assert(r.getMetadata().containsKey("") == false);
		
		r.addMetadataAtKey(null, "v1");
		assert(r.getMetadata().size() == 1);
		assert(r.getMetadata().containsKey("") == false);
	}
}
