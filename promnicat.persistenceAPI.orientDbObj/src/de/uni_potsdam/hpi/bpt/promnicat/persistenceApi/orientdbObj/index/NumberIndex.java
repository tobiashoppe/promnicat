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

import com.orientechnologies.orient.core.exception.OCommandExecutionException;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.AbstractPojo;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.PersistenceApiOrientDbObj;

/**
 * This class is used to store index elements with numeric keys and any values of type V.
 * In order to realize all functionality with OrientDB, we use manual indices of OrientDB:
 * see http://code.google.com/p/orient/wiki/Indexes
 * 
 * @author Andrina Mascher
 * 
 * @param <K> the numeric Keytype of the key/value index elements, e.g. Integer, Double, Float, Long
 * @param <V> the Valuetype of the key/value index elements
 */
public class NumberIndex<K extends Number, V extends AbstractPojo> 
						extends AbstractIndex<K, V>{
	
	public NumberIndex(String name, PersistenceApiOrientDbObj papi) {
		super(name, papi);
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
		indexMngr.addNumberIndex(name);
		papi.saveIndexMngr();
		papi.executeCommand("CREATE INDEX " + name + " NOTUNIQUE"); 
	}
	
	/* (non-Javadoc)
	 * @see de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.index.AbstractIndex#dropIndex()
	 */
	@Override
	public void dropIndex() {
		indexMngr.removeNumberIndex(name);
		papi.saveIndexMngr();
		try{
			papi.executeCommand("DROP INDEX " + name); 
		} catch(OCommandExecutionException e) {
			throw new IllegalStateException("cannot drop index, not found");
		}
	}
	
	/* (non-Javadoc)
	 * @see de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.index.AbstractIndex#clearIndex()
	 */
	@Override
	public void clearIndex() {
		try{
			papi.executeCommand("DELETE FROM index:" + name); 
		} catch(OCommandExecutionException e) {
			throw new IllegalStateException("Create index first");
		}
	}
	
	////////////////////////add load ////////////////////////////////
	
	/* (non-Javadoc)
	 * @see de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.index.AbstractIndex#add(java.lang.Object, java.lang.String)
	 */
	@Override
	public void add(K key, String dbId) {
		//wrong input
		if(key == null || dbId == null || dbId == "")
			throw new IllegalArgumentException("don't add empty key or dbId to index");
		try{
			@SuppressWarnings("unused")
			ORecordId rid = new ORecordId(dbId);
		} catch(Exception e) {
			throw new IllegalArgumentException("this is no dbId: " + dbId);
		}		
		
		//right input
		try{
			String sql = "INSERT INTO index:" + name + " (key, rid)" +
					" VALUES (" + key.toString() + "," + dbId + ")";
			papi.executeCommand(sql);
		} catch(OCommandExecutionException e) {
			throw new IllegalStateException("Create index first");
		}	
	}


	/* (non-Javadoc)
	 * @see de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.index.AbstractIndex#convertToElements(java.util.List, boolean)
	 */
	@Override
	List<IndexElement<K, V>> convertToElements(List<Object> oList, boolean loadPojo) {
		List<IndexElement<K, V>> iList = new ArrayList<IndexElement<K, V>>();
		for(Object o : oList) {
			ODocument doc = (ODocument) o;
			IndexElement<K, V> element = new IndexElement<K, V>(readKey(doc), readDbId(doc));
			element.setIndex(this);
			if(loadPojo)
				element.loadPojo(papi);
			iList.add(element);
		}
		return iList;
	}
	
	/**
	 * @param doc the internal index result
	 * @return the index element's key as numerical
	 */
	protected K readKey(ODocument doc) { 
		return doc.field("key");
	}
	
	/**
	 * @param doc the internal index result
	 * @return the index element's value as database id
	 */
	protected String readDbId(ODocument doc) {
		return doc.field("rid", ORecordId.class).toString();
	}
	
	////////////////////////select ////////////////////////////////
	
	/* (non-Javadoc)
	 * @see de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.index.AbstractIndex#setSelectAll()
	 */
	@Override
	public void setSelectAll() {
		super.setNoSql("SELECT FROM index:" + name);
	}
	
	/**
	 * @param whereClause to build and set the nosql command for loading
	 */
	private void selectFromWhere(String whereClause) {
		super.setNoSql("SELECT FROM index:" + name + " WHERE " + whereClause);
	}
	
	/**
	 * @param i the criterion for a equality search
	 */
	public void setSelectEquals(K i) {
		if(i == null) throw new IllegalArgumentException("Selection criterion may not be null");
		// FIXME why do we need "key between 5.0 and 5.0"
		// why does "key = 5" not work?
		selectFromWhere("key between " + i + " and " + i);
	}
	
	/**
	 * @param i the limit for >= search
	 */
	public void setSelectGreaterOrEquals(K i) {
		if(i == null) throw new IllegalArgumentException("Selection criterion may not be null");
		selectFromWhere("key >= " + i);
	}
	
	/**
	 * @param i the limit for <= search
	 */
	public void setSelectLessOrEquals(K i) {
		if(i == null) throw new IllegalArgumentException("Selection criterion may not be null");
		selectFromWhere("key <= " + i);
	}

	/**
	 * Select this index keys from a range.
	 * 
	 * @param start
	 * @param end
	 */
	public void setSelectBetween(K start, K end) {
		if(start == null || end == null) 
			throw new IllegalArgumentException("Selection criterion may not be null");
		
		if(start.doubleValue() > end.doubleValue()) {
			K k = start;
			start = end;
			end = k;
		}
		selectFromWhere("key between " + start + " and " + end);
	} 
	
	/**
	 * Select this index keys from a list of keys.
	 * List elements where no key is found are skipped.
	 * 
	 * @param keys
	 */
	public void setSelectElementsOf(List<K> keys) {
		if(keys == null) 
			throw new IllegalArgumentException("Selection criterion may not be null");
		
		//convert to a string
		String listString = "";
		for(K key : keys) {
			if(key == null)
				throw new IllegalArgumentException("Selection criterion may not be null");
			
			if( !listString.isEmpty() ) {
				listString += ","; 
			}
			listString += key;
		}
		listString = "[" + listString + "]";
		
		selectFromWhere("key in " + listString);
	}
}
