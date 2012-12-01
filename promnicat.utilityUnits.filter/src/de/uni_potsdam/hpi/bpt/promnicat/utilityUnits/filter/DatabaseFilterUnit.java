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
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.filter;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPersistenceApi;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.config.DbFilterConfig;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnitChain;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;

/**
 * This class executes a query on the database, given a specific configuration.
 * 
 * The expected input type is {@link IUnitData}<{@link IUnitChain}>.
 * The output type is the same as the input type.
 * 
 * @author Tobias Hoppe, Cindy Fähnrich
 *
 */
public class DatabaseFilterUnit implements IUnit<IUnitData<Object>, IUnitData<Object>> {
	
	private IPersistenceApi papi = null;
	private DbFilterConfig config = null;

	public DatabaseFilterUnit(IPersistenceApi papi){
		this.papi = papi;
	}
	
	/* (non-Javadoc)
	 * @see de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit#execute(java.lang.Object)
	 */
	@Override
	public IUnitData<Object> execute(IUnitData<Object> input) throws IllegalTypeException {
		if (input == null) {
			throw new IllegalArgumentException("Got an invalid null pointer input!");
		}
		if (!(input.getValue() instanceof IUnitChain<?, ?>)){
			throw new IllegalTypeException(IUnitChain.class, input.getValue().getClass(), "Got wrong input type in" + this.getName());
		}
		//expected input pair contains as second argument the unit chain instance to pass the database results to
		if (config == null) {
			throw new IllegalArgumentException("A configuration must be provided!");
		}
		papi.loadRepresentationsAsync(this.config, (IUnitChain<?, ?>) input.getValue());
		return input;
	}

	/**
	 * @return the config
	 */
	public DbFilterConfig getDatabaseConfig() {
		return config;
	}

	/**
	 * @param config the config to set
	 */
	public void setDatabaseConfig(DbFilterConfig config) {
		this.config = config;
	}
	
	@Override
	public String getName(){
		return "DatabaseFilterUnit";
	}

	@Override
	public Class<?> getInputType() {
		return IUnitChain.class;
	}

	@Override
	public Class<?> getOutputType() {
		return Representation.class;
	}

	@Override
	public boolean isValidRootUnit() {
		return true;
	}
}