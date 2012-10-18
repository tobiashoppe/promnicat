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

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.impl.AbstractPojoOrientDb;

/**
 * This class is used to store the available index names for later retrieval and internal checking.
 * All index names must be unique.
 * 
 * @author Andrina Mascher
 *
 */
public class IndexManager extends AbstractPojoOrientDb { 

	HashSet<String> numberIndices = new HashSet<String>();
	HashSet<String> stringIndices = new HashSet<String>();
	
	/**
	 * @return the numberIndices
	 */
	public HashSet<String> getNumberIndices() {
		return numberIndices;
	}

	/**
	 * @param numberIndices the numberIndices to set
	 */
	public void setNumberIndices(HashSet<String> numberIndices) {
		this.numberIndices = numberIndices;
	}

	/**
	 * @param numberIndex to be added to the numberIndices
	 */
	public void addNumberIndex(String numberIndex) {
		this.numberIndices.add(numberIndex);
	}
	
	/**
	 * @param numberIndex to be removed from the numberIndices
	 */
	public void removeNumberIndex(String numberIndex) {
		this.numberIndices.remove(numberIndex);
	}
	
	/**
	 * @return the stringIndices
	 */
	public HashSet<String> getStringIndices() {
		return stringIndices;
	}

	/**
	 * @param stringIndices the stringIndices to set
	 */
	public void setStringIndices(HashSet<String> stringIndices) {
		this.stringIndices = stringIndices;
	}
	
	/**
	 * @param stringIndex to be added to the stringIndices
	 */
	public void addStringIndex(String stringIndex) {
		this.stringIndices.add(stringIndex);
	}
	
	/**
	 * @param stringIndex to be removed from the stringIndices
	 */
	public void removeStringIndex(String stringIndex) {
		this.stringIndices.remove(stringIndex);
	}
	
	/**
	 * Returns true if this name is already present for some index in the database.
	 * 
	 * @param index the index name to check
	 * @return
	 */
	public boolean contains(String index) {
		return numberIndices.contains(index) || stringIndices.contains(index);
	}
}
