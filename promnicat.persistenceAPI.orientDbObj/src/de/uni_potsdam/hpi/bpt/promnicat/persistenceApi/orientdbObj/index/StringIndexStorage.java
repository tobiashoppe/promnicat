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

import javax.persistence.Id;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPojo;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.AbstractPojo;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.impl.PersistenceApiOrientDbObj;

/**
 * This class is used to store one index element with a string as key.
 * For search mechanisms a lower case key is used, 
 * whereas for loading and presenting a result to the user, the original key is stored.
 * 
 * @author Andrina Mascher
 *
 */
public class StringIndexStorage extends AbstractPojo { 

	/**
	 * the id used by OrientDb, can not use the id defined in {@link AbstractPojo}
	 * due to OrientDb's injections.
	 */
	@Id 
	private String dbId;
	
	protected String dbIdReference; 	//caution: this is not the internal dbId of this object 
											//but the dbId of the referenced object
	protected String key; 			//lower case
	protected String originalKey; 	//case-sensitive
	
	public StringIndexStorage() {
	}
	
	public StringIndexStorage(String key, String dbId) {
		this.dbIdReference = dbId;
		setKey(key);
		setOriginalKey(key);
	}
	
	/**
	 * @return the originalKey
	 */
	public String getOriginalKey() {
		return originalKey;
	}

	/**
	 * @param originalKey the originalKey to set
	 */
	public void setOriginalKey(String originalKey) {
		this.originalKey = originalKey;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key.toLowerCase();
	}

	/**
	 * @return the dbId
	 */
	public String getDbIdReference() {
		return dbIdReference;
	}
	/**
	 * @param dbId the dbId to set
	 */
	public void setDbIdReference(String dbId) {
		this.dbIdReference = dbId;
	}
	
	public IPojo loadPojo(PersistenceApiOrientDbObj papi) {
		if(dbIdReference == null || dbIdReference.isEmpty()) 
			return null;
		return papi.loadPojo(dbIdReference);
	}
	
	public String toString() {
		return "StringIndexElement[" + key + " -> " +  dbIdReference + "]";
	}
	
	@Override
	public String getDbId() {
		return this.dbId;
	}

	@Override
	public boolean hasDbId() {
		return this.dbId != null;
	}
}
