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
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits;

import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;

/**
 * Interface for the utility units.
 * 
 * @author Tobias Hoppe, Cindy Fähnrich
 */
public interface IUnit<InputType extends IUnitData<Object>, OutputType extends IUnitData<Object> > {

	/**
	 * @param input to be used for calculation
	 * @return the result of the unit's execution
	 * @throws IllegalTypeException if the given input type is not the expected one.
	 */
	public OutputType execute(InputType input) throws IllegalTypeException;
	
	/**
	 * @return the name of the current {@link IUnit}.
	 */
	public String getName();
	
	/**
	 * @return the expected source type class
	 */
	public Class<?> getInputType();
	
	/**
	 * @return the output type class of this {@link IUnit}.
	 */
	public Class<?> getOutputType();
}
