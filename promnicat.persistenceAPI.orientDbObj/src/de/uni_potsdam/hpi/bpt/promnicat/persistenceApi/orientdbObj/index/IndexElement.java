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
package de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.index;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.AbstractPojo;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.PersistenceApiOrientDbObj;

/**
 * This represents one entry in the index and is given to the user after loading from the index.
 * 
 * @author Andrina Mascher
 *
 * @param <K> the Keytype of this key/value index element, such as String or Numbers
 * @param <V> the Valuetype of this key/value index element, such as {@link Representation}.
 */
public class IndexElement<K, V extends AbstractPojo> { 

	protected AbstractIndex<K,V> index = null;	// the index this element belongs to
	protected String valueDbId = "";			// the value's database id
	protected V valuePojo = null;				// the value as pojo, if loaded
	protected K key = null;						// the key, such as a analysis result
	
	public IndexElement(K key, String dbId) {
		this.valueDbId = dbId;
		this.key = key;
	}
	
	
	/**
	 * @return the index
	 */
	public AbstractIndex<K, V> getIndex() {
		return index;
	}


	/**
	 * @param index the index to set
	 */
	public void setIndex(AbstractIndex<K, V> index) {
		this.index = index;
	}


	/**
	 * @return the key
	 */
	public K getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(K key) {
		this.key = key;
	}

	/**
	 * @return the dbId
	 */
	public String getDbId() {
		return valueDbId;
	}
	
	/**
	 * @param dbId the dbId to set
	 */
	public void setDbId(String dbId) {
		this.valueDbId = dbId;
	}
	
	/**
	 * @return the pojo
	 */
	public V getPojo() {
		return valuePojo;
	}
	
	/**
	 * @param pojo the pojo to set
	 */
	public void setPojo(V pojo) {
		this.valuePojo = pojo;
	}
	
	/**
	 * @param papi
	 * @return loads and sets the pojo belonging to the database id
	 */
	@SuppressWarnings("unchecked")
	public V loadPojo(PersistenceApiOrientDbObj papi) {
		if(valueDbId == null || valueDbId.isEmpty()) 
			return null;
		
		V loaded = null;
		if(papi.isRepresentation(valueDbId)) {
			valuePojo = (V) papi.loadRepresentation(valueDbId);
		} else {
			valuePojo = (V) papi.loadPojo(valueDbId);
		}
		return loaded;
	}
	
	public String toString() {
		return "IndexElement[" + key + " -> " +  valueDbId + " from " + index.getName() + " with " + valuePojo + "]";
	}
}
