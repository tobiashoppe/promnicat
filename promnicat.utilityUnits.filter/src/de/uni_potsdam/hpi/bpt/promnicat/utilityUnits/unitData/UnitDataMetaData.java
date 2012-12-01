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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnitChain;

/**
 * {@link IUnitChain} result type implementation containing the {@link Representation} id of the used 
 * process model and the result value of the last {@link IUnit} in the {@link IUnitChain} as well as
 * the extracted metadata.
 * @author Tobias Hoppe
 */
public class UnitDataMetaData<V extends Object> extends UnitData<V> implements IUnitDataMetaData<V> {

	/**
	 * the extracted metadata
	 */
	private Map<String, Collection<String>> metaData = new HashMap<String, Collection<String>>();;
	
	/**
	 * An empty result with <code>null</code> elements and an empty map of metadata.
	 */
	public UnitDataMetaData() {
		super();
	}
	
	/**
	 * A result type with the given value as result and <code>null</code>
	 *  as the database id of the used process model. The metadata is set to an empty map.
	 * @param value the result of the {@link IUnit}
	 */
	public UnitDataMetaData(V value) {
		super(value);
	}
	
	/**
	 * A result type with the given values. The metadata is set to an empty map.
	 * @param value the result of the {@link IUnit}
	 * @param dbId the database id of the {@link Representation} used for result value calculation
	 */
	public UnitDataMetaData(V value, UUID dbId) {
		super(value, dbId);
	}
	
	/**
	 * A result type with the given value as result and <code>null</code>
	 *  as the database id of the used process model. The metadata is set to the given value.
	 * @param value the result of the {@link IUnit}
	 * @param metaData the extracted metadata
	 */
	public UnitDataMetaData(V value, Map<String, Collection<String>> metaData) {
		super(value);
		this.metaData = metaData;
	}
	
	/**
	 * A result type with the given values.
	 * @param value the result of the {@link IUnit}
	 * @param dbId the database id of the {@link Representation} used for result value calculation
	 * @param metaData the extracted metadata
	 */
	public UnitDataMetaData(V value, UUID dbId, Map<String, Collection<String>> metaData) {
		super(value, dbId);
		this.metaData = metaData;
	}
	
	@Override
	public Map<String, Collection<String>> getMetaData() {
		return this.metaData;
	}

	@Override
	public void setMetaData(Map<String, Collection<String>> metaData) {
		this.metaData = metaData;
	}

	@Override
	public String toString(){
		return super.toString() +
				"\nmeta data: " + this.metaData.toString();
	}
}
