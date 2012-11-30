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
package de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IModel;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IRepresentation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IRevision;

/**
 * A {@link Revision} represents one version of a {@link Model}, with one {@link Revision} being the latest/newest.
 * Each {@link Revision} can hold several {@link Representation}s. 
 * Each {@link Revision} has a unique number, author, language and metadata with any key/values pairs.
 * 
 * @author Andrina Mascher, Tobias Hoppe
 *
 */
public class Revision extends AbstractPojo implements IRevision {

	//the revision number
	protected Integer revisionNumber = null;
	//connected model
	protected IModel model = null;
	//connected representations
	protected Set<IRepresentation> representations = new HashSet<IRepresentation>();
	//metadata with key/values
	protected Map<String, Collection<String>> metadata = new HashMap<String, Collection<String>>();
	//is true if this is the latest revision of the model
	protected boolean latestRevision = false;
	//name of the authors
	protected String author = "";

	protected Revision() {
	}

	protected Revision(Integer number) {
		this.revisionNumber = number;
	}

	@Override
	public String toString() {
		return "Revision [dbId=" + getDbId() + ", revisionNumber=" + getRevisionNumber() + ", latestRevison=" + isLatestRevision()
				+ ", modelTitle=" + getTitle()
				+ ", #representations=" + getNrOfRepresentations()
				+ ", author=" + getAuthor()
				+ ", #metadata=" + getMetadata().size() + "]"
				;
	}
	
	@Override
	public boolean isCompletelyLoaded() {
		if(getModel() == null) {
			return false;
		}
		return getModel().getCompletelyLoaded();
	}

	@Override
	public void connectModel(IModel newModel) {
		//defer responsibility
		if(newModel != null) {
			newModel.connectRevision(this); 
		}
	}

	@Override
	public void connectRepresentation(IRepresentation representation) {
		if(representation != null) {
			if(!this.representations.contains(representation)) {
				this.representations.add((Representation) representation);				
				((Representation) representation).setRevision(this);
			}
		}
	}

	@Override
	public String getTitle() {
		if (getModel() == null) {
			return null;
		}
		return getModel().getTitle();
	}
	
	@Override
	public int getNrOfRepresentations() {
		return representations.size();
	}

	@Override
	public String getAuthor() {
		return author;
	}

	@Override
	public void setAuthor(String author) {
		this.author = author;
	}

	@Override
	public boolean isLatestRevision() {
		return latestRevision;
	}

	@Override
	public void setLatestRevision(boolean latestRevision) {
		this.latestRevision = latestRevision;
	}

	@Override
	public Integer getRevisionNumber() {
		return revisionNumber;
	}

	@Override
	public void setRevisionNumber(Integer number) {
		this.revisionNumber = number;
	}

	@Override
	public IModel getModel() {
		return model;
	}

	@Override
	public Set<IRepresentation> getRepresentations() {
		return representations;
	}

	@Override
	public void setRepresentations(Set<IRepresentation> representations) {
		this.representations.clear();
		if(representations == null) {
			return;
		}
		for (IRepresentation rep : representations) {
			this.representations.add(rep);
			connectRepresentation(rep);
		}
	}

	@Override
	public Map<String, Collection<String>> getMetadata() {
		return metadata;
	}

	@Override
	public Collection<String> getMetadataAtKey(String key) {
		return metadata.get(key);
	}
	
	@Override
	public void setMetadata(Map<String, Collection<String>> metadata) {
		if(metadata == null) {
			this.metadata.clear();
			return;
		}
		for(Entry<String, Collection<String>> e : metadata.entrySet()) {
			setMetadataAtKey(e.getKey(), e.getValue());
		}
	}
	
	@Override
	public void setMetadataAtKey(String key, Collection<String> values) {
		if(key == null || key.isEmpty()) {
			return;
		}
		this.metadata.put(key, values);
	}
	
	@Override
	public void addMetadataAtKey(String key, String value) {
		if(key == null || key.isEmpty()) {
			return;
		} else if (!metadata.containsKey(key)) {
			Collection<String> values = new ArrayList<String>();
			values.add(value);
			metadata.put(key, values);
		} else {
			Collection<String> newValue = metadata.get(key);
			newValue.add(value);
			metadata.put(key, newValue);
		}
	}

	public void setModel(IModel modelToSet) {
		model = modelToSet;
	}
}
