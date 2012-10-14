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
import java.util.List;

import com.orientechnologies.orient.core.db.object.ODatabaseObjectTx;
import com.orientechnologies.orient.core.exception.OStorageException;
import com.orientechnologies.orient.core.id.ORecordId;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.AbstractPojo;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.PersistenceApiOrientDbObj;

/**
 * This class is used to store index elements with string keys and any values of type V.
 * In order to realize all functionality with OrientDB, we use clusters instead of OrientDB indices.
 * Each instance of a StringIndex is saved in a new cluster with the specified name as a separate file on the disk.
 * All criteria are case-insensitive.
 * 
 * @author Andrina Mascher
 * 
 * @param <V> the Valuetype of the key/value index elements
 */
public class StringIndex<V extends AbstractPojo> extends AbstractIndex<String,V>{
	
	String cluster = "";
	ODatabaseObjectTx papiAccess = papi.getInternalDbAccess();
	ODatabaseObjectTx db;
	
	public StringIndex(String name, PersistenceApiOrientDbObj papi) {
		super(name, papi);
		cluster = name.toLowerCase() + "cluster"; //will be converted to lowerCase by OrientDb anyway
		db = papi.getInternalDbAccess();
		setSelectAll();
	}

	/* (non-Javadoc)
	 * @see de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.index.AbstractIndex#createIndex()
	 */
	@Override
	public void createIndex() {	
		if(indexMngr.contains(name)) {
			// consider: only add warning to log, but don't throw exception?
			throw new IllegalStateException("cannot create index " + name + ", it already exists, drop or clear it");
		}
		indexMngr.addStringIndex(name);
		papi.saveIndexMngr();
		papiAccess.addPhysicalCluster(cluster);
	}
	
	/* (non-Javadoc)
	 * @see de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.index.AbstractIndex#dropIndex()
	 */
	@Override
	public void dropIndex() {
		indexMngr.removeStringIndex(name);
		papi.saveIndexMngr();
		try{
			papiAccess.dropCluster(cluster);
		} catch (OStorageException e) {
			throw new IllegalStateException("cannot drop index, not found");
		}
	}
	
	/* (non-Javadoc)
	 * @see de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.index.AbstractIndex#clearIndex()
	 */
	@Override
	public void clearIndex() {
		papi.executeCommand("DELETE FROM cluster:" + cluster); 
	}
	
	
	//////////////////////// add load  ////////////////////////////////
	
	/* (non-Javadoc)
	 * @see de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.index.AbstractIndex#add(java.lang.Object, java.lang.String)
	 */
	@Override
	public void add(String key, String dbId) {
		//wrong input
		if(key == null || dbId == null || key == "" || dbId == "")
			throw new IllegalArgumentException("don't add empty key or dbId to index");
		try{
			@SuppressWarnings("unused")
			ORecordId rid = new ORecordId(dbId);
		} catch(Exception e) {
			throw new IllegalArgumentException("this is no dbId: " + dbId);
		}
		
		//right input
		try{
			StringIndexStorage e = new StringIndexStorage(key,dbId);
			db.save(e, cluster); 
		} catch(IllegalArgumentException e) {
			throw new IllegalStateException("Create index first");
		}
	}
	
	/* (non-Javadoc)
	 * @see de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.index.AbstractIndex#convertToElements(java.util.List, boolean)
	 */
	@Override
	List<IndexElement<String,V>> convertToElements(List<Object> oList, boolean loadPojo) {
		List<IndexElement<String,V>> eList = new ArrayList<IndexElement<String,V>>();
		for(Object o: oList) {
			StringIndexStorage s = (StringIndexStorage) o;
			IndexElement<String,V> e = new IndexElement<String,V>(s.getOriginalKey(), s.getDbIdReference());
			e.setIndex(this);
			if(loadPojo)
				e.loadPojo(papi);
			eList.add(e);
		}
		return eList;
	}
	
	//////////////////////// select ////////////////////////////////
	
	/**
	 * @return the SQL prefix
	 */
	private String selectFromCluster() {
		return "SELECT FROM cluster:" + cluster;
	}
	
	/* (non-Javadoc)
	 * @see de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.index.AbstractIndex#setSelectAll()
	 */
	@Override
	public void setSelectAll() {
		super.setNoSql(selectFromCluster());
	}
	
	/**
	 * @param criterion for equality selection
	 */
	public void setSelectEquals(String criterion) {
		//idea: use real OrientDB index? it might be faster
		if(criterion == null || criterion == "")
			throw new IllegalArgumentException("Selection criterion may not be empty");
		super.setNoSql(selectFromCluster() + " WHERE key = '" + criterion.toLowerCase() + "'");
	}
	
	/**
	 * @param criterion for substring search
	 */
	public void setSelectContains(String criterion) {
		if(criterion == null || criterion == "")
			throw new IllegalArgumentException("Selection criterion may not be empty");
		super.setNoSql(selectFromCluster() + " WHERE key like '%" + criterion.toLowerCase() + "%'");
	}
	
	/**
	 * Sets the criteria for the next load: all criteria have to be a substring of the key.
	 * 
	 * @param criteria for multiple substring search
	 */
	public void setSelectContains(String[] criteria) {
		if(criteria.length == 0) {
			throw new IllegalArgumentException("selection criteria may not be empty");
		}
		
		String sql = selectFromCluster() + " WHERE";
		for(int i = 0; i<criteria.length; i++) {
			if(i > 0) {
				sql += " and";
			}
			if(criteria[i].length() == 0) {
				throw new IllegalArgumentException("selection criteria may not be empty");
			}
			sql += " key like '%" + criteria[i].toLowerCase() + "%'";
		}
		super.setNoSql(sql);
	}
	
	/**
	 * Sets the criteria for the next load: the regular expression.
	 * Use lower-case letters only, because index is stored as lower case internally.
	 * 
	 * @param regEx for search based on regular expressions
	 */
	public void setSelectRegEx(String regEx) {
		if(regEx == null || regEx == "") 
			throw new IllegalArgumentException("Selection criterion may not be empty");
		
		super.setNoSql(selectFromCluster() + " WHERE key MATCHES '" + regEx + "'");
	}
}
