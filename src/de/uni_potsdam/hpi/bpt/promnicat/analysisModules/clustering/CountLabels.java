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
package de.uni_potsdam.hpi.bpt.promnicat.analysisModules.clustering;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.jbpt.pm.Activity;


import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.DbFilterConfig;

import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;
import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnitChain;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnitChainBuilder;
//import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitDataJbpt;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.UnitChainBuilder;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitDataLabelFilter;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.UnitDataLabelFilter;
//import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.UnitDataJbpt;

/**
 * Represents following scenario:
 * Find all diagrams from database that
 * are from BPMAI, 
 * written in BPMN 1.1  and 2.0,
 * Extract all Activities and their labels
 * Use the latest version
 * 
 * @author Rami-Habib Eid-Sabbagh
 *
 */
public class CountLabels {
	
	private static final String CONFIGURATION_FILE = "configuration(full).properties";
	
	public static Integer labelcount = 0;
	public Integer actvitycount = 0;
	

	private static final Logger logger = Logger
			.getLogger(CountLabels.class.getName());
	
	public static void main(String[] args) throws IllegalTypeException, IOException {
		//configure for time measurement
		long startTime = System.currentTimeMillis();
		
		//build chain
		IUnitChainBuilder chainBuilder = new UnitChainBuilder(CONFIGURATION_FILE, Constants.DATABASE_TYPES.ORIENT_DB, UnitDataLabelFilter.class);
		buildUpUnitChain(chainBuilder);
		
		logger.info(chainBuilder.getChain().toString());
		
		//run chain
		@SuppressWarnings("unchecked")
		Collection<IUnitDataLabelFilter<Object>> result = (Collection<IUnitDataLabelFilter<Object>>) chainBuilder.getChain().execute();
		Integer numbermodels = result.size();
		System.out.println("Number of Process models:"+numbermodels);
		//print result
		printResult(result);
		System.out.println("Number of Process models:"+numbermodels);
		//finish time measurement
		long time = System.currentTimeMillis() - startTime;
		System.out.println("Time needed: "
			+ (time / 1000 / 60) + " min " + (time / 1000 % 60) + " sec \n\n");
	}

	/**
	 * Configures and builds up the unit chain by invoking the corresponding builder methods.
	 * @param chainBuilder
	 * @throws IllegalTypeException 
	 */
	private static void buildUpUnitChain(IUnitChainBuilder chainBuilder) throws IllegalTypeException {
		//build db query
		DbFilterConfig dbFilter = new DbFilterConfig();
		dbFilter.addOrigin(Constants.ORIGINS.BPMAI);		
		dbFilter.addFormat(Constants.FORMATS.BPMAI_JSON);
		//dbFilter.addNotation(Constants.NOTATIONS.BPMN1_1);
		dbFilter.addNotation(Constants.NOTATIONS.BPMN2_0);
		//dbFilter.addNotation(Constants.NOTATIONS.EPC);
		dbFilter.setLatestRevisionsOnly(true);
		chainBuilder.addDbFilterConfig(dbFilter);
		
		//transform to jbpt and extract flow nodes with search criterion 
		chainBuilder.createBpmaiJsonToJbptUnit();
		//chainBuilder.createProcessModelLabelExtractorUnit();
		chainBuilder.createElementExtractorUnit(Activity.class);
		//chainBuilder.createElementLabelExtractorUnit();
		
		
		
		//collect results
		chainBuilder.createSimpleCollectorUnit();
	}
	
	/**
	 * Iterates through the results and prints out labels
	 * @param results from the execution of the {@link IUnitChain}
	 */
	private static void printResult(Collection<IUnitDataLabelFilter<Object> > result){

			
		for ( IUnitDataLabelFilter<Object> diagramResult : result) {
			if ((diagramResult != null)  && (diagramResult.getValue() != null)){
//				int count = 0;
				//String foundElements = "";
				Object labelElementMap = diagramResult.getValue();
				for (Entry<?, ?> entry : ((Map<?,?>)labelElementMap).entrySet()){
					//String elementClass = entry.getKey().toString();
					Collection<?> labels = (Collection<?>) entry.getValue();
					for (Object label : labels) {
						if (label instanceof String) {
							//foundElements = foundElements.concat("Class: " + elementClass 
							//		+ " Full name: " + label + "\n");
							//count labels
							labelcount = labelcount+1 ;
							label = ((String)label).replaceAll("\n", " ");
							System.out.println(label+": labelcount :" + labelcount);
//							count++;									
						} else {
							logger.warning("Found unexpected result type!\n" + label.toString());
						}
					}
				}				
							
			} else {
				logger.info("No labels found");
			}
		}
		
	}


}