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
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IRepresentation;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnitChain;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;

/**
 * Executes an {@link IUnitChain} in a separate {@link Thread}.
 * Attention: The first element is skipped, because database unit is only executed ones.
 *            Synchronization of shared resources have to be done by caller.
 * 
 * @author Tobias Hoppe
 *
 */
public class UnitChainExecutor implements Runnable {

	private final static Logger logger = Logger.getLogger(UnitChainExecutor.class.getName());
	
	private Vector<IUnit<IUnitData<Object>, IUnitData<Object>>> units = null;
	private boolean throwErrors = true;
	private Collection<Exception> errors = null;
	private Object value = null;
	private Class<?> unitDataType = null;
	
	/**
	 * Executes an {@link IUnitChain} in a separate {@link Thread}.
	 * Attention: The first element is skipped, because database unit is only executed ones.
	 *            Synchronization of shared resources have to be done by caller.
	 * 
	 * @param units the chain to execute (the first element is skipped, because database unit is only executed ones)
	 * @param throwErrors indicates whether occurred errors should be thrown or just logged.
	 * @param errors a list of all occurred errors
	 * @param value to start with in the first chain being executed
	 */
	public UnitChainExecutor(Class<?> unitDataType, Vector<IUnit<IUnitData<Object>, IUnitData<Object>>> units, boolean throwErrors, Collection<Exception> errors, Object value) {
		this.units = units;
		this.throwErrors = throwErrors;
		this.errors = errors;
		this.value = value;
		this.unitDataType = unitDataType;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		// execute further units for this database result
		// finally synchronized by collector unit
		Iterator<IUnit<IUnitData<Object>, IUnitData<Object>>> i = this.units.iterator();
		i.next();
		IUnitData<Object> result = null;
		try {
			result = (IUnitData<Object>) this.unitDataType.newInstance();
			result.setDbId(((IRepresentation) value).getDbId());
			result.setValue(value);
		} catch (Exception e) {
			if (this.throwErrors) {
				this.errors.add(e);
				return;
			} else {
				logger.severe("An unhandeled exception occured. The result may be incorrect! Got error message:\n" + e);
			}
		}
		while (i.hasNext()) {
			try {
				result = i.next().execute(result);
			} catch (Exception e) {
				if (this.throwErrors) {
					this.errors.add(e);
				} else {
					logger.severe("An unhandeled exception occured. The result may be incorrect! Got error message:\n" + e);
				}
				break;
			}
		}

	}

}
