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

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPojoFactory;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IRepresentation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IRevision;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Model;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.PojoFactory;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Revision;

/**
 * Test class for methods on {@link PersistanceApiOrientDB} that change database content 
 * such as save and delete.
 * @author Andrina Mascher
 *
 */
public class PojosTest {

	static Model mockModel = null;
	private static final PojoFactory fac = (PojoFactory) IPojoFactory.INSTANCE;

	@Test
	public void testSetLatestRevision() {
		Model model = fac.createModel();
		Revision rev1 = fac.createRevision();
		Revision rev2 = fac.createRevision();
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
		
		Revision rev3 = fac.createRevision();
		model.connectLatestRevision(rev3);
		assertTrue(model.getNrOfRevisions() == 3); 
		assertTrue(model.getLatestRevision() == rev3);
		assertTrue(rev3.isLatestRevision());
		assertFalse(rev2.isLatestRevision());
	}
	
	@Test
	public void testConnectRevisions() {
		Model model = fac.createModel();
		Revision rev1 = fac.createRevision();
		model.connectRevision(rev1);
		assertTrue(model.getNrOfRevisions() == 1);
		
		//set empty revisions list
		Set<IRevision> list = new HashSet<IRevision>();
		model.setRevisions(list);
		assertTrue(model.getNrOfRevisions() == 0);
		assertNull(model.getLatestRevision());
		
		//add 1 revision
		model.connectRevision(rev1);
		assertTrue(model.getNrOfRevisions() == 1);
		
		//set 2 other revisions
		Revision rev2 = fac.createRevision();
		Revision rev3 = fac.createRevision();
		list.add(rev2);
		list.add(rev3);
		model.setRevisions(list);
		assertTrue(model.getNrOfRevisions() == 2); //not 3
		assertNull(model.getLatestRevision()); //still no latestRevision set		
	}
	
	@Test
	public void testConnectNull() {
		Model model = fac.createModel();
		Revision rev = fac.createRevision();
		Representation rep = fac.createRepresentation();
		
		model.connectRevision(rev);
		assertTrue(model.getNrOfRevisions() == 1);
		rev.connectRepresentation(rep);
		assertTrue(rev.getNrOfRepresentations() == 1);

		//set empty revisions list
		model.setRevisions(null);
		assertTrue(model.getNrOfRevisions() == 0);
		
		//set empty reps list
		rev.setRepresentations(null);
		assertTrue(rev.getNrOfRepresentations() == 0);
	}

	@Test
	public void testUnconnectedObjects() {
		Representation rep = fac.createRepresentation();
		assertNull(rep.getRevision());
		assertNull(rep.getModel());
		assertNull(rep.getTitle());
		assertNull(rep.getRevisionNumber());
		assertFalse(rep.belongsToLatestRevision());
		assertFalse(rep.hasDataContent());
		
		Revision rev = fac.createRevision();
		assertNull(rev.getModel());
		assertNull(rev.getTitle());
		assert(rev.getNrOfRepresentations() == 0);
		
		//connect both
		rev.connectRepresentation(rep);
		assert(rev.getNrOfRepresentations() == 1);
		//set empty connections
		rev.setRepresentations(new HashSet<IRepresentation>());
		assert(rev.getNrOfRepresentations() == 0);
	}
	
	@Test
	public void testConnectionDeferment() {
		Representation rep = fac.createRepresentation();
		Revision rev = fac.createRevision();
		Model mod = fac.createModel();
		rep.setRevision(rev);
		rev.connectModel(mod);
		assert(rep.getRevision() == rev);
		assert(rev.getModel() == mod);
		assert(rep.getModel() == mod);
	}
	
	@Test
	public void testMetadata() {
		Revision r = fac.createRevision();
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
