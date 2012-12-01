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

import org.jbpt.pm.ProcessModel;

import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.AbstractUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;

/**
 * This class filters out all process models that are not connected.
 * 
 * The expected input type is {@link IUnitData}<{@link ProcessModel}>.
 * The output type is the same as the input type. If the given {@link ProcessModel} is <b>not connected</b>,
 * the second parameter of {@link IUnitData} is set to <code>null</code>.
 *
 * @author Tobias Hoppe, Cindy Fähnrich
 *
 */
public class ConnectednessFilterUnit extends AbstractUnit implements IUnit<IUnitData<Object>, IUnitData<Object> > {
	
	/* (non-Javadoc)
	 * @see de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit#execute(java.lang.Object)
	 */
	@Override
	public IUnitData<Object> execute(IUnitData<Object> input) throws IllegalTypeException {
		if (input == null) {
			throw new IllegalArgumentException("Got an invalid null pointer input!");
		}
		if (input.getValue() == null){
			return input;
		}
		if (!(input.getValue() instanceof ProcessModel)){
			throw new IllegalTypeException(ProcessModel.class, input.getValue().getClass(), "Got wrong input type in" + this.getName());
		}
		if (((ProcessModel) input.getValue()).isConnected()) {
			return input;			
		} else {
			input.setValue(null);
			return input;
		}		
	}

	
	/* (non-Javadoc)
	 * @see de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit#getName()
	 */
	@Override
	public String getName(){
		return "ConnectednessFilterUnit";
	}

	@Override
	public Class<?> getInputType() {
		return ProcessModel.class;
	}

	@Override
	public Class<?> getOutputType() {
		return ProcessModel.class;
	}
}
