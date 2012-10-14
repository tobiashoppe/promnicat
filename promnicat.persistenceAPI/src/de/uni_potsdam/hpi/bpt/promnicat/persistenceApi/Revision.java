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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;


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
public class Revision extends AbstractPojo{

	//the revision number
	private Integer revisionNumber = null;
	//connected model
	Model model = null;
	//connected representations
	private HashSet<Representation> representations = new HashSet<Representation>();
	//metadata with key/values, values are separated by MD_SPLIT to store only key/value in database
	private HashMap<String, String> internalMetadata = new HashMap<String, String>();
	//separator used to distinguish metadata values for 1 key
	private static final String MD_SPLIT = "\t####\t";
	//is true if this is the latest revision of the model
	private boolean latestRevision = false;
	//name of the authors
	private String author = "";

	public Revision() {
	}

	public Revision(Integer number) {
		this.revisionNumber = number;
	}

	@Override
	public String toString() {
		return "Revision [dbId=" + dbId + ", revisionNumber=" + revisionNumber + ", latestRevison=" + isLatestRevision()
				+ ", modelTitle=" + getTitle()
				+ ", #representations=" + getNrOfRepresentations()
				+ ", author=" + author
				+ ", #metadata=" + internalMetadata.size() + "]"
				;
	}
	
	public boolean isCompletelyLoaded() {
		if(model == null) {
			return false;
		}
		return model.isCompletelyLoaded();
	}

	/**
	 * connect a {@link Model} and vice versa
	 * 
	 * @param newModel the model to connect to
	 */
	public void connectModel(Model newModel) {
		//defer responsibility
		if(newModel != null) {
			newModel.connectRevision(this); 
		}
	}

	/**
	 * connect to a {@link Representation} and vice versa
	 * 
	 * @param representation
	 */
	public void connectRepresentation(Representation representation) {
		if(representation != null) {
			representation.revision = this;
			this.representations.add(representation);
		}
	}

	/**
	 * @return the title of the connected {@link Model}
	 */
	public String getTitle() {
		if (model == null) {
			return null;
		}
		return model.getTitle();
	}
	
	/**
	 * @return the number of connected and loaded {@link Representation}s
	 */
	public int getNrOfRepresentations() {
		return representations.size();
	}

	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @param author the author to set
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * @return the latestRevision
	 */
	public boolean isLatestRevision() {
		return latestRevision;
	}

	/**
	 * @param latestRevision true if this is the latest revision of a {@link Model}
	 */
	public void setLatestRevision(boolean latestRevision) {
		this.latestRevision = latestRevision;
	}

	/**
	 * @return the number of this revision
	 */
	public Integer getRevisionNumber() {
		return revisionNumber;
	}

	/**
	 * @param number the revision number to set
	 */
	public void setRevisionNumber(Integer number) {
		this.revisionNumber = number;
	}

	/**
	 * @return the connected {@link Model}
	 */
	public Model getModel() {
		return model;
	}

	/**
	 * @return all connected and loaded {@link Representation}
	 */
	public HashSet<Representation> getRepresentations() {
		return representations;
	}

	/**
	 * Set the new {@link Representation}s and connect each of them.
	 * Previous connections are destroyed.
	 * 
	 * @param representations the {@link Representation} to connect
	 */
	public void setAndConnectRepresentations(Collection<Representation> representations) {
		this.representations.clear();
		if(representations == null) {
			return;
		}
		for (Representation rep : representations) {
			this.representations.add(rep);
			connectRepresentation(rep);
		}
	}

	/**
	 * @return all key/values pairs in metadata
	 */
	public HashMap<String, String[]> getMetadata() {
		HashMap<String,String[]> newMd = new HashMap<String,String[]>();
		for(Entry<String,String> e : internalMetadata.entrySet()) {
			newMd.put(e.getKey(), convertMetadataValueToArray(e.getValue()));
		}
		return newMd;
	}

	/**
	 * @param key
	 * @return the key/values pair at the key
	 */
	public String[] getMetadataAtKey(String key) {
		return convertMetadataValueToArray(internalMetadata.get(key));
	}
	
	/**
	 * Convert a string into an array by splitting
	 * @param value
	 * @return the converted array
	 */
	private String[] convertMetadataValueToArray(String value) {
		return value.split(MD_SPLIT);
	}
	
	/**
	 * Convert a string array into a string. 
	 * This is a work around because key/value pairs can be stored in OrientDb, but key/values not.
	 * @param array
	 * @return the converted string
	 */
	private String convertMetadataValueFromArray(String[] array) {
		String s = "";
		for(int i=0; i<array.length; i++) {
			s += array[i] + MD_SPLIT;	
		}
		//don't start with MD_SPLIT
		s = s.substring(0, s.length() - MD_SPLIT.length());
		return s;
	}
	
	/**
	 * Set all metadata key/values pairs. 
	 * All previous entries are removed.
	 * @param metadata
	 */
	public void setMetadata(HashMap<String, String[]> metadata) {
		if(metadata == null) {
			this.internalMetadata.clear();
			return;
		}
		for(Entry<String,String[]> e : metadata.entrySet()) {
			setMetadataAtKey(e.getKey(), e.getValue());
		}
	}
	
	/**
	 * Set a key/values pair in the metadata.
	 * The previous values for this key are removed.
	 * @param key
	 * @param values
	 */
	public void setMetadataAtKey(String key, String[] values) {
		if(key == null || key.isEmpty()) {
			return;
		}
		this.internalMetadata.put(key, convertMetadataValueFromArray(values));
	}
	
	/**
	 * Adds the value to the associated key in the metadata 
	 * or creates a new key/values pair in the metadata, if the key did not exist before.
	 * * @param key
	 * @param value
	 */
	public void addMetadataAtKey(String key, String value) {
		if(key == null || key.isEmpty()) {
			return;
		} else if (!internalMetadata.containsKey(key)) {
			internalMetadata.put(key, value);
		} else {
			String s = internalMetadata.get(key);
			if(s.length() == 0) {
				s = value;
			} else {
				s += MD_SPLIT + value;
			}
			internalMetadata.put(key, s);
		}
	}
}
