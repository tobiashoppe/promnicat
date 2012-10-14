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
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.collector;

import java.util.Collection;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;

/**
 * Interface for units that collect all the results from the parallel executed chains.
 * @author Tobias Hoppe
 *
 */
public interface ICollectorUnit<InputType extends IUnitData<Object>, OutputType extends IUnitData<Object> > extends IUnit<InputType, OutputType> {

	/**
	 * @return the wrapped result of all executions
	 */
	Collection<OutputType> getResult();
	
	/**
	 * resets the internal state
	 */
	void reset();
}
