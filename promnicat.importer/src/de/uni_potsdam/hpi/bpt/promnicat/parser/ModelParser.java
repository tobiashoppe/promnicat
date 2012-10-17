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
package de.uni_potsdam.hpi.bpt.promnicat.parser;

import java.util.HashMap;
import java.util.logging.Logger;

import org.jbpt.pm.ProcessModel;

import de.uni_potsdam.hpi.bpt.ai.diagram.Diagram;
import de.uni_potsdam.hpi.bpt.promnicat.util.modelConstants.Bpmn1_1Constants;
import de.uni_potsdam.hpi.bpt.promnicat.util.modelConstants.Bpmn2_0Constants;
import de.uni_potsdam.hpi.bpt.promnicat.util.modelConstants.EpcConstants;

/**
 * Parser for transforming a given JSON process model into jBPT format. Delegates the transformation
 * to the actually responsible parsers (EPC, BPMN).
 * @author Cindy Fähnrich, Tobias Hoppe
 *
 */
public class ModelParser implements IParser {

	/**
	 * Hashmap for delegater parsers
	 */
	private HashMap<String, IParser> delegates;

	public ModelParser() {
		this(false);
	}
	
	public ModelParser(boolean strictness) {
		delegates = new HashMap<String, IParser>();

		delegates.put("bpmn2.0#", new BpmnParser(new Bpmn2_0Constants(), strictness));
		delegates.put("bpmn1.1#", new BpmnParser(new Bpmn1_1Constants(), strictness));
		delegates.put("epc#", new EpcParser(new EpcConstants(), strictness));
	}

	@Override
	public ProcessModel transformProcess(Diagram process){

		String namespace = process.getStencilset().getNamespace();
		String stencilset = namespace.substring(namespace.lastIndexOf("/") + 1);

		IParser parser = this.delegates.get(stencilset);
		if (parser != null) {
			return parser.transformProcess(process);
		} else {
			Logger.getLogger(ModelParser.class.getName()).warning("Model with stencilset '" + stencilset + "' could not be parsed!");
			return null;
		}
	}
}
