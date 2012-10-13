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
package de.uni_potsdam.hpi.bpt.promnicat.processEvolution.api;

import java.util.Map;

import de.uni_potsdam.hpi.bpt.promnicat.processEvolution.model.ProcessEvolutionModel;

/**
 * The interface for every Process Evolution Analysis.
 * Every analysis must be able to return the analyzed models
 * and must be able to return the analysis as a result string.
 *  
 * @author Tobias Metzke
 *
 */
public interface IAnalysis {

	/**
	 * delivers the results of the analysis in a CSV-formatted String
	 * @return the formatted results as a string
	 */
	public String toResultCSVString();
	
	/**
	 * delivers the analyzed models and merges with the results of prior
	 * analyses if given
	 * @return map of analyzed models
	 */
	public Map<String, ProcessEvolutionModel> getAnalyzedModels();
}
