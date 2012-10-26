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

import java.util.Map;
import java.util.Set;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Model;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Revision;

/**
 * A {@link Revision} represents one version of a {@link Model}, with one {@link Revision} being the latest/newest.
 * Each {@link Revision} can hold several {@link Representation}s. 
 * Each {@link Revision} has a unique number, author, language and metadata with any key/values pairs.
 * For performance reasons, sometimes not all revisions are loaded from the database. For this case 
 * the nrOfRevisionsInDb tell the total number of revisions found in the database.
 * 
 * @author Andrina Mascher, Tobias Hoppe
 *
 */
public interface IRevision extends IPojo {

	public boolean isCompletelyLoaded();

	/**
	 * connect a {@link Model} and vice versa
	 * 
	 * @param newModel the model to connect to
	 */
	public void connectModel(IModel newModel);

	/**
	 * connect to a {@link Representation} and vice versa
	 * 
	 * @param representation
	 */
	public void connectRepresentation(IRepresentation representation);

	/**
	 * @return the title of the connected {@link Model}
	 */
	public String getTitle();
	
	/**
	 * @return the number of connected and loaded {@link Representation}s
	 */
	public int getNrOfRepresentations();

	/**
	 * @return the author
	 */
	public String getAuthor();

	/**
	 * @param author the author to set
	 */
	public void setAuthor(String author);

	/**
	 * @return the latestRevision
	 */
	public boolean isLatestRevision();

	/**
	 * @param latestRevision true if this is the latest revision of a {@link Model}
	 */
	public void setLatestRevision(boolean latestRevision);

	/**
	 * @return the number of this revision
	 */
	public Integer getRevisionNumber();

	/**
	 * @param number the revision number to set
	 */
	public void setRevisionNumber(Integer number);

	/**
	 * @return the connected {@link IModel}
	 */
	public IModel getModel();

	/**
	 * @return all connected and loaded {@link Representation}
	 */
	public Set<IRepresentation> getRepresentations();

	/**
	 * Set the new {@link Representation}s and connect each of them.
	 * Previous connections are destroyed.
	 * 
	 * @param representations the {@link Representation} to connect
	 */
	public void setRepresentations(Set<IRepresentation> representations);

	/**
	 * @return all key/values pairs in metadata
	 */
	public Map<String, String[]> getMetadata();

	/**
	 * @param key
	 * @return the key/values pair at the key
	 */
	public String[] getMetadataAtKey(String key);
	
	/**
	 * Set all metadata key/values pairs. 
	 * All previous entries are removed.
	 * @param metadata
	 */
	public void setMetadata(Map<String, String[]> metadata);
	
	/**
	 * Set a key/values pair in the metadata.
	 * The previous values for this key are removed.
	 * @param key
	 * @param values
	 */
	public void setMetadataAtKey(String key, String[] values);
	
	/**
	 * Adds the value to the associated key in the metadata 
	 * or creates a new key/values pair in the metadata, if the key did not exist before.
	 * * @param key
	 * @param value
	 */
	public void addMetadataAtKey(String key, String value);

	/**
	 * @return the modelId
	 */
	public String getModelId();

	/**
	 * @param modelId the modelId to set
	 */
	public void setModelId(String modelId);
}
