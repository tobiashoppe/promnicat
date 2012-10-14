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

import org.jbpt.pm.ProcessModel;

import de.uni_potsdam.hpi.bpt.ai.diagram.Diagram;

/**
 * Interface for all parser classes.
 * @author Cindy Fähnrich
 *
 */
public interface IParser {
	
	/**
	 * Parses the stencil set out of the diagram and creates the corresponding process model in jBPT
	 * by delegating the diagram to the corresponding parser
	 * @param process to transform into {@link ProcessModel}
	 * @return the corresponding process model
	 */
	public ProcessModel transformProcess(Diagram process);

}
