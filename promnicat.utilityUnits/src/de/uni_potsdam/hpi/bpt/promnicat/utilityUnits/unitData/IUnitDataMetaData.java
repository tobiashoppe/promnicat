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
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData;

import java.util.Collection;
import java.util.Map;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnitChain;

/**
 * Interface for classes that can be used as {@link IUnit} input and output.
 * The id of the used {@link Representation} as well as the result value of the last {@link IUnit} of
 * the {@link IUnitChain} is stored. Furthermore, the extracted meta data information is stored.
 * @author Tobias Hoppe
 *
 */
public interface IUnitDataMetaData< V extends Object> extends IUnitData<V> {

	/**
	 * @return the collected meta data
	 */
	Map<String, Collection<String>>getMetaData();
	
	/**
	 * @param metaData the meta data to be set as result
	 */
	void setMetaData(Map<String, Collection<String>> metaData);
}
