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
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.test;

import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.impl.AbstractUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;

/**
 * Mock for an {@link IUnit} implementation without any functionality.
 * 
 * @author Tobias Hoppe
 */
public class MockUnit extends AbstractUnit implements IUnit<IUnitData<Object>, IUnitData<Object>> {
	
	/**
	 * Mock for an {@link IUnit} implementation without any functionality.
	 * @param clazz unused
	 */
	public MockUnit(Class<?> clazz) {
	}

	/**
	 * Mock for an {@link IUnit} implementation without any functionality.
	 */
	public MockUnit() {
	}
	
	@Override
	public IUnitData<Object> execute(IUnitData<Object> input) throws IllegalTypeException {
		return null;
	}

	@Override
	public String getName() {
		return MockUnit.class.getName();
	}

	@Override
	public Class<?> getInputType() {
		return null;
	}

	@Override
	public Class<?> getOutputType() {
		return null;
	}

}
