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

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnitChain;

/**
 * {@link IUnitChain} result type implementation containing the {@link Representation} id of the used 
 * process model and the result value of the last {@link IUnit} in the {@link IUnitChain}.
 * @author Tobias Hoppe
 */
public class UnitData<V extends Object> implements IUnitData<V> {
	
	/**
	 * result value
	 */
	private V value = null;
	
	/**
	 * id of the used {@link Representation}
	 */
	private UUID dbId = null;
	
	/**
	 * An empty result with <code>null</code> elements
	 */
	public UnitData() {
		
	}
	
	/**
	 * A result type with the given value as result and <code>null</code>
	 *  as the database id of the used process model
	 * @param value the result of the {@link IUnit}
	 */
	public UnitData(V value) {
		this.value = value;
	}
	
	/**
	 * A result type with the given values.
	 * @param value the result of the {@link IUnit}
	 * @param dbId the database id of the {@link Representation} used for result value calculation
	 */
	public UnitData(V value, UUID dbId) {
		this.value = value;
		this.dbId = dbId;
	}

	@Override
	public String toString(){
		return "\nvalue: " + this.value +
				"\nDB Id: " + this.dbId;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public void setValue(V value) {
		this.value = value;
	}

	@Override
	public UUID getDbId() {
		return this.dbId;
	}

	@Override
	public void setDbId(UUID dbId) {
		this.dbId = dbId;
	}
}
