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
package de.uni_potsdam.hpi.bpt.promnicat.analysisModules.classification;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jbpt.pm.ProcessModel;

import de.uni_potsdam.hpi.bpt.promnicat.analysisModules.IAnalysisModule;
import de.uni_potsdam.hpi.bpt.promnicat.analysisModules.metrics.ProcessMetrics;
import de.uni_potsdam.hpi.bpt.promnicat.configuration.ConfigurationParser;
import de.uni_potsdam.hpi.bpt.promnicat.parser.BpmnParser;
import de.uni_potsdam.hpi.bpt.promnicat.parser.EpcParser;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPersistenceApi;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.config.DbFilterConfig;
import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;
import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.builder.IUnitChainBuilder;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.builder.impl.UnitChainBuilder;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitDataClassification;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.impl.UnitDataClassification;

/**
 * Analysis module to classify business process models according to:
 * 		- BPMN Process Modeling Conformance (Descriptive, Analytic, Common Executable)
 * 		- Soundness(Classic, Relaxed, Weak, Lazy)
 * 		- Model Type(workflow net, S/T-net, free choice)
 * 		- Structuring(is structured, is structurable)
 * 
 * @author Tobias Hoppe
 *
 */
public class ProcessClassification implements IAnalysisModule{
	
	/**
	 * split element for CSV file values
	 */
	private static final String ITEMSEPARATOR = ";";

	private static final String RESULT_FILE_PATH = new File("").getAbsolutePath() + "/src/de/uni_potsdam/hpi/bpt/promnicat/analysisModules/classification/result.csv";
	private static final Logger logger = Logger.getLogger(ProcessMetrics.class.getName());

	/**
	 * @param args
	 * @throws IllegalTypeException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, IllegalTypeException {
		ProcessClassification analysis = new ProcessClassification();
		analysis.execute(args);
	}
	
	@Override
	public Object execute(String[] parameter) throws IOException, IllegalTypeException {
		//configure for time measurement
		long startTime = System.currentTimeMillis();
		
		//build up chain
		IUnitChainBuilder chainBuilder = buildUpUnitChain();
		
		logger.info(chainBuilder.getChain().toString() + "\n");
		
		//parser should not log parsing errors
		Logger epcParserLog = Logger.getLogger(EpcParser.class.getName());
		epcParserLog.setLevel(Level.SEVERE);
		Logger bpmnParserLog = Logger.getLogger(BpmnParser.class.getName());
		bpmnParserLog.setLevel(Level.SEVERE);
	
		//run chain
		@SuppressWarnings("unchecked")
		Collection<IUnitDataClassification<Object> > result = (Collection<IUnitDataClassification<Object>>) chainBuilder.getChain().execute();
	
		//finish time measurement
		long time = System.currentTimeMillis() - startTime;
		
		//print result
		writeResultToFile(result, time);
		
		logger.info("Time needed for process model classification: " + (time / 1000 / 60) + " min " + (time / 1000 % 60) + " sec \n\n");			
		return result;
	}

	/**
	 * Create an new unit chain builder and builds up
	 * a chain to get structural information of the {@link ProcessModel}s from the given database.
	 * @param useFullDB use full data set or if set to <code>false</code> only a small excerpt
	 * @return the builder with the created chain
	 * @throws IOException if the given configuration file path could not be found
	 * @throws IllegalTypeException if the units of the chain have incompatible input/output types
	 */
	private IUnitChainBuilder buildUpUnitChain() throws IOException, IllegalTypeException {
		IUnitChainBuilder chainBuilder = null;
		//set to true if the whole DB data should be used for analysis otherwise only a small sub set is used.
		String configPath = "configuration(full).properties";
		ConfigurationParser configParser = new ConfigurationParser(configPath);
		IPersistenceApi persistenceApi = configParser.getDbInstance();
		persistenceApi.openDb();
		chainBuilder = new UnitChainBuilder(persistenceApi, configParser.getThreadCount(), UnitDataClassification.class);
		//build db query
		DbFilterConfig dbFilter = new DbFilterConfig();
		dbFilter.addOrigin(Constants.ORIGINS.BPMAI);		
		dbFilter.addFormat(Constants.FORMATS.BPMAI_JSON);
		dbFilter.addNotation(Constants.NOTATIONS.BPMN1_1);
		dbFilter.addNotation(Constants.NOTATIONS.BPMN2_0);
		dbFilter.addNotation(Constants.NOTATIONS.EPC);
		dbFilter.setLatestRevisionsOnly(true);
		chainBuilder.addDbFilterConfig(dbFilter);
		//transform to jBPT
		chainBuilder.createBpmaiJsonToJbptUnit(false);
		//check conformance level
		chainBuilder.createBpmnConformanceLevelCheckerUnit();
		//perform structural checks
		chainBuilder.createModelStructuringUnit();
		//transform to PetriNet
		chainBuilder.createProcessModelToPetriNetUnit();
		//analyse Petri net
		chainBuilder.createPetriNetAnalyzerUnit();
		
		//collect results
		chainBuilder.createSimpleCollectorUnit();
		return chainBuilder;
	}
	
	/**
	 * Write the result into a CSV file
	 * @param resultSet the collected result of the chain execution
	 */
	private void writeResultToFile(Collection<IUnitDataClassification<Object>> resultSet, long time) {
		BufferedWriter writer = null;
		try {
			//open file for writing
			writer = new BufferedWriter( new FileWriter(RESULT_FILE_PATH));
			//add date as first line
			StringBuilder resultString = new StringBuilder("Finished calculation at: " + new Date().toString() + "\n");
			resultString.append("Time needed for classification calculation: " + (time / 1000 / 60) + " min " + (time / 1000 % 60) + " sec \n\n");
			resultString.append(addHeader());
			//collect result from each model
			for(IUnitDataClassification<Object> resultItem : resultSet){
				//do not print dot representation of petri net
				resultString.append(resultItem.toCsv(ITEMSEPARATOR, false));
			}
			writer.write(resultString.toString());
		} catch (IOException e) {
			logger.warning("Could not open file '" + RESULT_FILE_PATH + "' for writing!");
		} finally {
			try {
				if (writer != null) {
					writer.close(); 
				}
			} catch (IOException e) {
				logger.warning("Could not close file '" + RESULT_FILE_PATH + "' after writing!");
			}
		}	
	}
	
	/**
	 * @return a String representation of the process model metrics
	 * separated by the {@link ProcessMetrics#ITEMSEPARATOR}.
	 */
	private String addHeader() {
		StringBuilder builder = new StringBuilder();
		builder.append("Process Model Path" + ITEMSEPARATOR);
		builder.append("DB ID" + ITEMSEPARATOR);
		builder.append("Descriptive Modeling Conform" + ITEMSEPARATOR);
		builder.append("Analytic Modeling Conform" + ITEMSEPARATOR);
		builder.append("Common Executable Modeling Conform" + ITEMSEPARATOR);		
		builder.append("boundedness" + ITEMSEPARATOR);
		builder.append("liveness" + ITEMSEPARATOR);
		builder.append("quasi liveness" + ITEMSEPARATOR);
		builder.append("relaxed sound" + ITEMSEPARATOR);
		builder.append("weak sound" + ITEMSEPARATOR);
		builder.append("classical sound" + ITEMSEPARATOR);
		builder.append("transitioncover" + ITEMSEPARATOR);
		builder.append("dead transitions" + ITEMSEPARATOR);
		builder.append("uncovered transitions" + ITEMSEPARATOR);
		builder.append("unbounded places" + ITEMSEPARATOR);		
		builder.append("isCyclic" + ITEMSEPARATOR);
		builder.append("isFreeChoice" + ITEMSEPARATOR);
		builder.append("isExtendedFreeChoice" + ITEMSEPARATOR);
		builder.append("isSNet" + ITEMSEPARATOR);
		builder.append("isTNet" + ITEMSEPARATOR);
		builder.append("isWorkFlowNet" + ITEMSEPARATOR);
		builder.append("isStructured" + ITEMSEPARATOR);
		builder.append("Petri Net as DOT");
		builder.append("\n");
		return builder.toString();
	}

}
