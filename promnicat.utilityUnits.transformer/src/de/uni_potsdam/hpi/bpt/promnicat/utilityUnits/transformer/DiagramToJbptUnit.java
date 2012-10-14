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
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.transformer;

import org.jbpt.pm.ProcessModel;

import de.uni_potsdam.hpi.bpt.ai.diagram.Diagram;
import de.uni_potsdam.hpi.bpt.promnicat.parser.ModelParser;
import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitDataJbpt;

/**
 * Utility Unit to Transform a {@link Diagram} object into a jBPT {@link ProcessModel}.
 * 
 * The expected input type is {@link IUnitData}<{@link Diagram}>.
 * The output type is {@link IUnitData}<{@link ProcessModel}>.
 * 
 * @author Tobias Hoppe, Cindy Fähnrich
 *
 */
public class DiagramToJbptUnit implements IUnit<IUnitData<Object>, IUnitData<Object> > {

	/**
	 * Indicates whether the unit only parses "correct" process models (true) or also
	 * the erroneous ones (false)
	 */
	private boolean strictness;

	/**
	 * Transforms a {@link Diagram} into a {@link ProcessModel}.
	 * Erroneous models will be skipped.
	 */
	public DiagramToJbptUnit() {
		this(false);
	}
	
	/**
	 * Transforms a {@link Diagram} into a {@link ProcessModel}.
	 * @param strictness set to <code>true</code> if erroneous models should be skipped.
	 */
	public DiagramToJbptUnit(boolean strictness) {
		this.strictness = strictness;
	}
	
	@Override
	public IUnitData<Object> execute(IUnitData<Object> input) throws IllegalTypeException {
		if (input == null || input.getValue() == null) {
			throw new IllegalArgumentException("Got an invalid null pointer input!");
		}
		if (!(input.getValue() instanceof Diagram)){
			throw new IllegalTypeException(Diagram.class, input.getValue().getClass(), "Got wrong input type in" + this.getName());
		}
		ProcessModel processModel = new ModelParser(this.strictness).transformProcess((Diagram) input.getValue());
		if (input instanceof IUnitDataJbpt<?>) {
			((IUnitDataJbpt<Object>) input).setProcessModel(processModel);
		}
		input.setValue(processModel);
		return input;
	}

	@Override
	public String getName(){
		return "DiagramToJbptUnit";
	}

	@Override
	public Class<?> getInputType() {
		return Diagram.class;
	}

	@Override
	public Class<?> getOutputType() {
		return ProcessModel.class;
	}
}