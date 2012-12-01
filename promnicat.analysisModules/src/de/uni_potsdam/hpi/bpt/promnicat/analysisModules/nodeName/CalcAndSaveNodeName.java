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
package de.uni_potsdam.hpi.bpt.promnicat.analysisModules.nodeName;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.jbpt.pm.FlowNode;

import de.uni_potsdam.hpi.bpt.promnicat.analysisModules.IAnalysisModule;
import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;
import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.builder.IUnitChainBuilder;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitDataLabelFilter;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.impl.UnitDataLabelFilter;

/**
 * Represents following scenario:
 * Find all diagrams from database that
 * are from BPMAI,
 * written in EPC,
 * and contain a node with text "customer" in title.
 * Use the latest versions only.
 * 
 * @author Andrina Mascher, Tobias Hoppe, Cindy Fähnrich
 *
 */
public class CalcAndSaveNodeName implements IAnalysisModule {
	
	private static final String CONFIGURATION_FILE = "configuration.properties";
	private static final String SEARCHCRITERION = "Customer";
	private PersistenceApiOrientDbObj papi;

	private static final Logger logger = Logger
			.getLogger(CalcAndSaveNodeName.class.getName());
	
	public static void main(String[] args) throws IllegalTypeException, IOException {
		CalcAndSaveNodeName analysis = new CalcAndSaveNodeName();
		analysis.execute(args);
	}

	@Override
	public Object execute(String[] parameter) throws IOException,
			IllegalTypeException {
		//configure for time measurement
		long startTime = System.currentTimeMillis();
		
		//build chain
		IUnitChainBuilder chainBuilder = new UnitChainBuilder(CONFIGURATION_FILE, Constants.DATABASE_TYPES.ORIENT_DB, UnitDataLabelFilter.class);
		buildUpUnitChain(chainBuilder);
		
		logger.info(chainBuilder.getChain().toString());
		
		//run chain
		@SuppressWarnings("unchecked")
		Collection<IUnitDataLabelFilter<Object>> result = (Collection<IUnitDataLabelFilter<Object>>) chainBuilder.getChain().execute();
		
		//print result
		printResult(result);
    
		//finish time measurement
		long time = System.currentTimeMillis() - startTime;
		System.out.println("Time needed: "
			+ (time / 1000 / 60) + " min " + (time / 1000 % 60) + " sec \n\n");
		return result;
	}

	/**
	 * Configures and builds up the unit chain by invoking the corresponding builder methods.
	 * @param chainBuilder
	 * @throws IllegalTypeException 
	 */
	private void buildUpUnitChain(IUnitChainBuilder chainBuilder) throws IllegalTypeException {
		//build db query
		DbFilterConfig dbFilter = new DbFilterConfig();
		dbFilter.addOrigin(Constants.ORIGINS.BPMAI);
		dbFilter.addNotation(Constants.NOTATIONS.EPC);
		dbFilter.addFormat(Constants.FORMATS.BPMAI_JSON);
		dbFilter.setLatestRevisionsOnly(true);
		chainBuilder.addDbFilterConfig(dbFilter);
		
		//transform to jbpt and extract flow nodes with search criterion 
		chainBuilder.createBpmaiJsonToJbptUnit();
		chainBuilder.createProcessModelFilterUnit(FlowNode.class);
		chainBuilder.createProcessModelLabelExtractorUnit();
		chainBuilder.createLabelFilterUnit(SEARCHCRITERION);
		
		//collect results
		chainBuilder.createSimpleCollectorUnit();
	}
	
	/**
	 * Iterates through the results and prints whether the activities contain the search criteria or not.
	 * @param results from the execution of the {@link IUnitChain}
	 */
	private void printResult(Collection<IUnitDataLabelFilter<Object> > result){

		AnalysisRun run = new AnalysisRun("searched for " + SEARCHCRITERION);
		
		for ( IUnitDataLabelFilter<Object> diagramResult : result) {
			if ((diagramResult != null)  && (diagramResult.getValue() != null)){
				int count = 0;
				String foundElements = "";
				Object labelElementMap = diagramResult.getValue();
				for (Entry<?, ?> entry : ((Map<?,?>)labelElementMap).entrySet()){
					String elementClass = entry.getKey().toString();
					Collection<?> labels = (Collection<?>) entry.getValue();
					for (Object label : labels) {
						if (label instanceof String) {
							foundElements = foundElements.concat("Class: " + elementClass 
									+ " Full name: " + label + "\n");
							//create LabelStorage and connect it to analyis run
							run.addStorage(createLabelStorage(label.toString(), elementClass, diagramResult.getDbId()));
							count++;									
						} else {
							logger.warning("Found unexpected result type!\n" + label.toString());
						}
					}
				}				
				logger.info("Found " + count + " node(s) with label " + SEARCHCRITERION + "\n" + foundElements);			
			} else {
				logger.info("No node contains label " + SEARCHCRITERION + "\n");
			}
		}
		
		//save analysis run and print out its database id
		papi = PersistenceApiOrientDbObj.getInstance(CONFIGURATION_FILE);
		papi.registerPojoPackage(run.getClass().getPackage().getName());
		papi.savePojo(run);
		logger.info("saved analysis run " + run.getDbId());
	}

	private LabelStorage createLabelStorage(String label, String elementClass, String repId) {
		LabelStorage labelStorage = new LabelStorage(label, elementClass);
		labelStorage.setRepresentationId(repId);
		System.out.println("created " + labelStorage);
		return labelStorage;
	}
}