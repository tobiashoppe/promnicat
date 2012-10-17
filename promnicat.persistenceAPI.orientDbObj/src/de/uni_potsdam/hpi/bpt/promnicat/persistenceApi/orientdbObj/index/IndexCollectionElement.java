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

import java.util.HashSet;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.AbstractPojo;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.PersistenceApiOrientDbObj;

/**
 * This represents one entry in an index intersection and is given to the user after loading from the intersecting
 * indices. When loading an index intersection, the intersection is based on the referenced database id. Therefore the
 * results are grouped by this database id. For each id, a {@link IndexCollectionElement} is created that points to
 * multiple {@link IndexElement}s, with at least one {@link IndexElement} from each index.
 * 
 * @author Andrina Mascher
 * 
 * @param <V>
 *            the Valuetype of these key/value index elements, such as {@link Representation}.
 */
public class IndexCollectionElement<V extends AbstractPojo> {

	protected String valueDbId = "";
	protected V valuePojo = null;
	protected HashSet<IndexElement<Object,V>> indexElements = new HashSet<IndexElement<Object,V>>();
	
	public IndexCollectionElement(String dbId) {
		this.valueDbId = dbId;
	}

	/**
	 * @return the indexElements
	 */
	public HashSet<IndexElement<Object,V>> getIndexElements() {
		return indexElements;
	}

	/**
	 * @param indexElements the indexElements to set
	 */
	public void setIndexElements(HashSet<IndexElement<Object,V>> indexElements) {
		this.indexElements = indexElements;
	}

	/**
	 * @param indexElements the indexElements to set
	 */
	public void addIndexElements(IndexElement<Object,V> indexElement) {
		this.indexElements.add(indexElement);
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
		
		for(IndexElement<Object,V> e : indexElements) {
			e.setPojo(valuePojo);
		}
		return loaded;
	}
	
	public String toString() {
		String s = "IndexCollectionElement<" //pojo.getClass().getSimpleName() 
		+ ">[ " + indexElements.size() + " IndexElements for " +  valueDbId + ": " + valuePojo + "]";
		for(IndexElement<Object,V> e : indexElements) {
			s += "\n\t" + e.toString();
		}
		return s;
	}
}
