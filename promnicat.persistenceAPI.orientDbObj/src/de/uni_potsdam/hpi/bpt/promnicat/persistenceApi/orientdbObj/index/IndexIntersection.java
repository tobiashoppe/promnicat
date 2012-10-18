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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.collections.list.TreeList;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPojo;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.impl.PersistenceApiOrientDbObj;


/**
 * To combine multiple index search criteria, the indices results can be intersected. Each individual index would load all selected database objects, but this class calculates the intersection of the referenced database ids first, before loading some of them.
 * When loading an index intersection, the intersection is based on the referenced database id. Therefore the
 * results are grouped by this database id. For each id, a {@link IndexCollectionElement} is created that points to
 * multiple {@link IndexElement}s, with at least one {@link IndexElement} from each index.
 * 
 * @author Andrina Mascher
 * 
 * @param <V>
 *            the Valuetype of these key/value indices, such as {@link Representation}.
 */
public class IndexIntersection<V extends IPojo> {

	@SuppressWarnings("rawtypes")
	ArrayList<AbstractIndex> indices = new ArrayList<AbstractIndex>();
	PersistenceApiOrientDbObj papi = null;
	
	public IndexIntersection(PersistenceApiOrientDbObj papi) {
		this.papi = papi;
	}
	
	/**
	 * Add an index to the intersection.
	 * 
	 * @param index the index to add
	 */
	@SuppressWarnings("rawtypes")
	public void add(AbstractIndex index) {
		indices.add(index);
	}
	
	/**
	 * Load the intersecting referenced objects from the specified indices.
	 * First load the database ids from all indices, intersect them, and load the remaining ids.
	 * 
	 * @return the resulting {@link IndexCollectionElement}s
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Collection<IndexCollectionElement<V>> load() {
		
		//load dbIds only and sort them by result set size
		TreeList rawResults = new TreeList(); //no generics possible
		int maxSize = 0;
		for(AbstractIndex index : indices) {
			ResultSet<V> oneResultSet = new ResultSet<V>(index.loadIdsOnly(), index.getName());
			rawResults.add(oneResultSet);
			maxSize = Math.max(maxSize,oneResultSet.getSize());
		}
		
		// create a list of intersecting dbIds
		// start with the smallest result set and intersect with the second smallest, intersect this result with the third smallest a.s.o.
		HashSet<String> intersectingDbIds = new HashSet<String>(maxSize);
		for(Object r : rawResults) {
			ResultSet<V> aResult = (ResultSet<V>) r;
			
			if(intersectingDbIds.isEmpty()) {
				intersectingDbIds.addAll(aResult.getDbIds());
			}
			else {
				intersectingDbIds.retainAll(aResult.getDbIds());
			}
			
			if(intersectingDbIds.isEmpty()) {
				break;
			}
		}
		
		//create Map of IndexElements each, i.e. group by referenced id. Every group is stored in a IndexCollectedElement
		HashMap<String,IndexCollectionElement<V>> finalElements = new HashMap<String,IndexCollectionElement<V>>(indices.size());
		for(Object r : rawResults) {
			ResultSet<V> aResult = (ResultSet<V>) r;
			for(IndexElement indexElement : aResult.getList()) {
				String currentString = indexElement.getDbId();
				if(intersectingDbIds.contains(currentString)) {
					if( !finalElements.containsKey(currentString) ) {
						finalElements.put(currentString, new IndexCollectionElement<V>(currentString));
					}
					finalElements.get(currentString).addIndexElements(indexElement);
				}
			}
		}
		
		//load pojos
		for(IndexCollectionElement<V> collectionElement : finalElements.values()) {
			collectionElement.loadPojo(papi);
		}
		
		return finalElements.values();
	}
}

/**
 * Each index loads a set of IndexElements stored in this class, together with its size.
 * This size is used to compare different instances to finally sort them. 
 * 
 * @author Andrina Mascher
 *
 * @param <V> the Valuetype of the {@link IndexElement}s
 */
@SuppressWarnings("rawtypes")
class ResultSet<V extends IPojo> implements Comparable<ResultSet>{
	List<IndexElement> resultSet;
	Integer size = null;
	String indexName = "";
	
	public ResultSet(List<IndexElement> aList, String name) {
		setList(aList);
		this.indexName = name;
	}
	
	/**
	 * Find all dbIds within the IndexElements
	 * 
	 * @return all dbIds
	 */
	public Collection<String> getDbIds() {
		HashSet<String> dbIds = new HashSet<String>();
		for(IndexElement e : resultSet) {
			dbIds.add(e.getDbId());
		}
		return dbIds;
	}
	
	/**
	 * @return the indexName
	 */
	public String getIndexName() {
		return indexName;
	}
	/**
	 * @param indexName the indexName to set
	 */
	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}
	/**
	 * @return the list
	 */
	public List<IndexElement> getList() {
		return resultSet;
	}
	/**
	 * @param aList the list to set
	 */
	public void setList(List<IndexElement> aList) {
		this.resultSet = aList;
		this.size = aList.size();
	}
	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}
	
	@Override
	public int compareTo(ResultSet other) {
		return size.compareTo(other.size);
	}
}
