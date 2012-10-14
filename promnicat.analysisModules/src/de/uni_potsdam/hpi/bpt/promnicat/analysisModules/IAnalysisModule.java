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
package de.uni_potsdam.hpi.bpt.promnicat.analysisModules;

import java.io.IOException;
import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;

/**
 * Interface for analysis modules offering standardized access.<br/>
 * <b>Attention: Changes to this interface might influence the {@link AnalysisModuleLauncher}.</b>
 * 
 * @author Tobias Hoppe
 */
public interface IAnalysisModule {

	/**
	 * Executes the current analysis module and returns the result.
	 * @param parameter for analysis module's execution
	 * @return the result of the analysis module run.
	 * @throws Exception if the execution of the analysis module fails
	 */
	public Object execute(String[] parameter) throws IOException, IllegalTypeException;
}
