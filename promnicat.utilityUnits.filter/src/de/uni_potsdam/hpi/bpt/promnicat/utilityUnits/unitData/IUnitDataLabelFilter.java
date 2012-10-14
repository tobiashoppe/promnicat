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
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData;

import java.util.Collection;
import java.util.Map;

import org.jbpt.hypergraph.abs.IVertex;
import org.jbpt.pm.ProcessModel;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnitChain;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.extractor.ElementExtractorUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.extractor.ProcessModelLabelExtractorUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.filter.LabelFilterUnit;

/**
 * Interface for classes that can be used as {@link IUnit} input and output.
 * The id of the used {@link Representation} as well as the result value of the last {@link IUnit} of
 * the {@link IUnitChain} is stored. Furthermore, the parsed jBPT {@link ProcessModel}, the filtered model elements
 * including their labels as well as a list of filtered labels is stored.
 * 
 * @author Tobias Hoppe
 */
public interface IUnitDataLabelFilter<V extends Object> extends IUnitDataJbpt<V>{

	/**
	 * @return the model elements extracted by the {@link ElementExtractorUnit}.
	 */
	public Collection<? extends IVertex> getModelElements();
	
	/**
	 * @param modelElements the model elements extracted by the {@link ElementExtractorUnit}.
	 */
	public void setModelElements(Collection<? extends IVertex> modelElements);
	
	/**
	 * @return the labels of the process model extracted with the {@link ProcessModelLabelExtractorUnit}.
	 */
	public Map<String, Collection<String>> getLabels();
	
	/**
	 * @param labels the labels of the process model
	 */
	public void setLabels(Map<String, Collection<String>> labels);
	
	/**
	 * @return the labels filtered out by the {@link LabelFilterUnit}.
	 */
	public Map<String, Collection<String>> getFilteredLabels();
	
	/**
	 * @param filteredLabels the labels of a process model filtered out by a certain criteria
	 */
	public void setFilteredLabels(Map<String, Collection<String>> filteredLabels);
}
