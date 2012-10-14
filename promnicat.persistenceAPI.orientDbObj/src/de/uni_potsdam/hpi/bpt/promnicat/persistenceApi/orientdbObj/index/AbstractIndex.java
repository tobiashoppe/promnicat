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

import java.util.List;

import com.orientechnologies.orient.core.exception.OCommandExecutionException;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.AbstractPojo;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.PersistenceApiOrientDbObj;

/**
 * This class is used to store index elements with keys of type K and any values of type V that can be saved in the database.
 * Different selection criteria define the next load result. They are usually based on the index keys.
 * 
 * @author Andrina Mascher
 *
 * @param <K> the Keytype of the key/value index elements, such as String or Numbers
 * @param <V> the Valuetype of the key/value index elements, such as {@link Representation}.
 */
public abstract class AbstractIndex<K, V extends AbstractPojo> {

	protected String name = "";
	protected PersistenceApiOrientDbObj papi;
	protected String noSql = "";
	IndexManager indexMngr = null;
	
	public AbstractIndex(String name, PersistenceApiOrientDbObj papi) {
		if(name == null || name == "") {
			throw new IllegalArgumentException("index name must not be empty");
		}
		if(papi == null) {
			throw new IllegalArgumentException("Persistence API must be provided");
		}
		this.name = name;
		this.papi = papi;
		setSelectAll();
		indexMngr = papi.getIndexMngr();
	}
	
	/**
	 * Create a new index, the index name must be unique.
	 */
	public abstract void createIndex();
	/**
	 * Deletes the index from disk.
	 */
	public abstract void dropIndex();
	/**
	 * Deletes all index content but not the index itself.
	 */
	public abstract void clearIndex();
	/**
	 * Adds a key/value pair to the index, duplicates are allowed.
	 * @param key the measurement or result to be stored, e.g. number of nodes or a label
	 * @param dbId the reference to the analyzed object, e.g. {@link Representation}.
	 */
	public abstract void add(K key, String dbId); 
	/**
	 * Sets the selection criterion for the next index load to all elements, this is default.
	 */
	public abstract void setSelectAll();
	/**
	 * Creates a list of {@link IndexElement}s from the internal index result list.
	 * 
	 * @param oList
	 * @param loadPojo if true, the referenced pojo will be loaded from the database.
	 * @return a list of {@link IndexElement}s
	 */
	abstract List<IndexElement<K,V>> convertToElements(List<Object> oList, boolean loadPojo);
	/**
	 * Loads all {@link IndexElement}s that fit the previously set selection criterion.
	 * Per default, all elements are loaded.
	 * 
	 * @return a list of {@link IndexElement}s
	 */
	public List<IndexElement<K, V>> load() {
		try{
			List<Object> oList = papi.load(noSql);
			return convertToElements(oList,true);
		} catch(OCommandExecutionException e) {
			throw new IllegalStateException("Unable to load, create index first");
		}
	}
	/**
	 * Loads all {@link IndexElement}s that fit the previously set selection criterion.
	 * Per default, all elements are loaded.
	 * Does not load the actual pojos from the database, but only stores their dbIds.
	 * 
	 * @return a list of {@link IndexElement}s
	 */
	List<IndexElement<K, V>> loadIdsOnly() {
		List<Object> oList = papi.load(noSql);
		return convertToElements(oList,false);
	}
	
	/**
	 * @return the papi
	 */
	public PersistenceApiOrientDbObj getPapi() {
		return papi;
	}

	/**
	 * @param papi the papi to set
	 */
	public void setPapi(PersistenceApiOrientDbObj papi) {
		this.papi = papi;
	}

	/**
	 * @param name the name to set
	 */
	protected void setNoSql(String noSql) {
		this.noSql = noSql;
	}
	
	/**
	 * @return the noSql
	 */
	public String getNoSql() {
		return noSql;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}
