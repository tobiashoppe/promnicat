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

import java.util.ArrayList;
import java.util.Collection;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;

/**
 * This class collects all incoming results.
 * The collected elements can be received by {@link SimpleCollectorUnit#getResult()}.
 * <br/><br/>
 * The expected input type is {@link IUnitData}<{@link Object}>.
 * The output type is the same as the input type.
 * 
 * @author Tobias Hoppe
 *
 */
public class SimpleCollectorUnit implements ICollectorUnit<IUnitData<Object>, IUnitData<Object> > {

	private Collection<IUnitData<Object> > collectedResult = new ArrayList<IUnitData<Object> >();
	
	@Override
	public IUnitData<Object> execute(IUnitData<Object> input) {
		if (input == null) {
			throw new IllegalArgumentException("Got an invalid null pointer input!");
		}
		this.collectedResult.add(input);
		return input;
	}

	@Override
	public Collection<IUnitData<Object>> getResult() {
		return this.collectedResult;
	}

	@Override
	public void reset() {
		this.collectedResult.clear();
	}
	
	@Override
	public String getName(){
		return "SimpleCollectorUnit";
	}

	@Override
	public Class<?> getInputType() {
		return Object.class;
	}

	@Override
	public Class<?> getOutputType() {
		return null;
	}

}
