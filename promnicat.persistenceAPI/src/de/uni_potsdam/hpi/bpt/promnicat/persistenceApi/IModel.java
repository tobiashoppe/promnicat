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
package de.uni_potsdam.hpi.bpt.promnicat.persistenceApi;

import java.util.Set;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Model;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Revision;

/**
 * A model represents a collection of {@link Revision} that again store a collection of {@link Representation}.
 * All of them share the same title and origin. 
 * For performance reasons, sometimes not all revisions are loaded from the database. For this case 
 * the nrOfRevisionsInDb tell the total number of revisions found in the database.
 * 
 * @author Andrina Mascher, Tobias Hoppe
 *
 */
public interface IModel extends IPojo {
	
	public String toStringExtended();
	
	/**
	 * Resets all values in this model by the values found in the database for this dbId.
	 * Loads all revisions from the database and all their representations.
	 * 
	 * @param papi the IPersistanceApi to use
	 * @return the new Model
	 */
	public IModel loadCompleteModel(IPersistenceApi papi);

	/**
	 * @return the number of connected and loaded {@link Representation}s that are connected to the set of {@link Revision}s
	 */
	public int getNrOfRepresentations();
	
	/**
	 * @return the number of connected and loaded {@link Revision}s
	 */
	public int getNrOfRevisions();
	
	/**
	 * Get all Revisions currently connected/loaded.
	 * 
	 * @return all connected {@link Revision}s
	 */
	public Set<IRevision> getRevisions();

	/**
	 * Does not set a latestRevision, it will be null afterwards. Call connectLatestRevision.
	 * @param revisions
	 */
	public void setRevisions(Set<IRevision> revisions);
	
	/**
	 * Connect a new {@link Revision}, no duplicates are added
	 * 
	 * @param revision
	 */
	public void connectRevision(IRevision revision);
	
	/**
	 * Connects a {@link Revision} (without creating duplicates) and sets it's status to the latest Revision.
	 * The previous lates revision is unset.
	 * 
	 * @param revision
	 */
	public void connectLatestRevision(IRevision revision);
	
	/**
	 * @return the latest revision, if any exists.
	 */
	public IRevision getLatestRevision();

	/**
	 * @return the id used for {@link Model} identification
	 */
	public String getImportedId();

	/**
	 * Set the id used to identify a {@link Model}
	 * @param id the id to set
	 */
	public void setImportedId(String id);

	/**
	 * @return the title
	 */
	public String getTitle();
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title);

	/**
	 * @return the original collection name
	 */
	public String getOrigin();

	/**
	 * @param origin the original collection name to set
	 */
	public void setOrigin(String origin);

	/**
	 * Indicates whether all {@link Revision}s and their {@link Representation}s are loaded from the database or just one of them.
	 * 
	 * @return the completelyLoaded
	 */
	public boolean getCompletelyLoaded();
	
	/**
	 * Set if all {@link Revision}s and their {@link Representation}s are loaded from the database or just one of them.
	 * 
	 * @param completelyLoaded
	 */
	public void setCompletelyLoaded(boolean completelyLoaded);
}
