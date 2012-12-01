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

import java.util.logging.Logger;

import org.jbpt.pm.ProcessModel;
import org.json.JSONException;

import de.uni_potsdam.hpi.bpt.ai.diagram.Diagram;
import de.uni_potsdam.hpi.bpt.ai.diagram.DiagramBuilder;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;

/**
 * Utility Unit to parse a process model given as BPM AI JSON string into a {@link Diagram}.
 * This {@link Diagram} can be transformed into a JBPT {@link ProcessModel} using the {@link DiagramToJbptUnit}.
 * The transformation code was reused from the BPM AI parsing implementation. 
 * The expected input type is {@link IUnitData}<{@link String}>.
 * The output type is {@link IUnitData}<{@link Diagram}>.
 * 
 * @author Tobias Hoppe, Cindy Fähnrich
 *
 */
public class BpmaiJsonToDiagramUnit implements IUnit<IUnitData<Object>, IUnitData<Object> > {
	
	private Logger logger = Logger.getLogger(BpmaiJsonToDiagramUnit.class.getName());
	
	@Override
	public IUnitData<Object> execute(IUnitData<Object> input) throws IllegalTypeException {
		if (input == null) {
			throw new IllegalArgumentException("Got an invalid null pointer input!");
		}
		if (!(input.getValue() instanceof Representation)){
			throw new IllegalTypeException(Representation.class, input.getValue().getClass(), "Got wrong input type in" + this.getName());
		}
		if(input instanceof IUnitDataProcessMetrics<?>){
			((IUnitDataProcessMetrics<?>) input).setModelPath(((Representation)input.getValue()).getOriginalFilePath());
		} else if (input instanceof IUnitDataClassification<?>) {
			((IUnitDataClassification<?>) input).setModelPath(((Representation)input.getValue()).getOriginalFilePath());
		}

		try {
			String json = ((Representation) input.getValue()).convertDataContentToString();
			Diagram diagram = DiagramBuilder.parseJson(json);
			diagram.setPath(((Representation)input.getValue()).getOriginalFilePath());
			input.setValue(diagram);
			return input;
		} catch (JSONException e) {
			logger.severe("JSON parsing failed, got message:\n" + e.getMessage());
			input.setValue(null);
			return input;
		}
	}

	@Override
	public String getName(){
		return "BpmaiJsonToDiagramUnit";
	}

	@Override
	public Class<?> getInputType() {
		return Representation.class;
	}

	@Override
	public Class<?> getOutputType() {
		return Diagram.class;
	}
}
