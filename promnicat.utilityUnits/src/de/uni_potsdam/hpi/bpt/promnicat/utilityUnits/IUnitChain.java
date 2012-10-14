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

import java.util.Collection;
import java.util.Observer;

import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;

/**
 * Interface class for {@link UnitChain}.
 * @author Tobias Hoppe, Cindy Fähnrich
 */
public interface IUnitChain<InputType extends IUnitData<Object>, OutputType extends IUnitData<Object> > extends Observer {
	
	/**
	 * Registers a given {@link IUnit} to the {@link UnitChain}.
	 * @param unit
	 * @return the resulting {@link UnitChain}
	 */
	IUnitChain<InputType, OutputType> register(IUnit<InputType, OutputType> unit);
	
	/**
	 * Registers a given {@link Collection} of {@link IUnit}s to the {@link UnitChain}.
	 * @param units
	 * @return the resulting {@link UnitChain}
	 */
	IUnitChain<InputType, OutputType> register(Collection<IUnit<InputType, OutputType>> units);
	
	/**
	 * Registers a given {@link UnitChain} to the {@link UnitChain}.
	 * @param unitChain
	 * @return the resulting {@link UnitChain}
	 */
	IUnitChain<InputType, OutputType> register(IUnitChain<InputType, OutputType> unitChain);
	
	/**
	 * Triggers the execution of the {@link UnitChain}, by that starting the execution of the
	 * chain's first element.
	 * @return the resulting output from the last element of the chain.
	 * @throws IllegalTypeException if a mismatch of {@link IUnit}'s input/output is detected.
	 * @throws IllegalArgumentException if a null pointer was given as {@link IUnit}'s input.
	 */
	Collection<? extends OutputType> execute() throws IllegalTypeException, IllegalArgumentException;
	
	/**
	 * @return a {@link Collection} of all units contained in the chain.
	 */
	Collection<IUnit<InputType, OutputType>> getUnits();
	
	/**
	 * @return the names of the elements contained in the {@link UnitChain} in appropriate order.
	 */
	String toString();
	
	/**
	 * @return the last element of the {@link UnitChain}.
	 */
	IUnit<IUnitData<Object>, IUnitData<Object> > getLastUnit();

	/**
	 * @return the first element of the {@link UnitChain}.
	 */
	IUnit<IUnitData<Object>, IUnitData<Object> > getFirstUnit();
}