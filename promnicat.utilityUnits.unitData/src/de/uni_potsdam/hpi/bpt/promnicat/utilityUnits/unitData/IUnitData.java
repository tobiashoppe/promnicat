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

import java.util.UUID;


/**
 * Interface for classes that can be used as {@link IUnit} input and output.
 * The id of the used {@link Representation} as well as the result value of the last {@link IUnit} of
 * the {@link IUnitChain} is stored.
 * 
 * @author Tobias Hoppe
 */
public interface IUnitData<V extends Object> {
	
	/**
	 * Get the result of this {@link IUnit}.
	 * @return the value of the stored result
	 */
	public V getValue();

	/**
	 * Set the result of this {@link IUnit}.
	 * @param value to set as result
	 */
	public void setValue(V value);
	
	/**
	 * Get the dbId of the process model behind this {@link IUnit}.
	 * @return {@link Representation}'s id of the used process model
	 */
	public UUID getDbId();

	/**
	 * Set the dbId of the process model behind this {@link IUnit}.
	 * @param dbId of used {@link Representation}
	 */
	public void setDbId(UUID dbId);

}
