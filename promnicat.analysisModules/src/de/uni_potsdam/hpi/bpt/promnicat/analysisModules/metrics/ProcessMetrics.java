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
package de.uni_potsdam.hpi.bpt.promnicat.analysisModules.metrics;

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
import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;
import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.builder.IUnitChainBuilder;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitDataProcessMetrics;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.impl.UnitDataProcessMetrics;

/**
 * Analysis module to calculate metrics from all {@link ProcessModel}s of a given database.
 * 
 * @author Tobias Hoppe
 *
 */
public class ProcessMetrics implements IAnalysisModule {
	
	/**
	 * split element for CSV file values
	 */
	private static final String ITEMSEPARATOR = ";";

	private static final String RESULT_FILE_PATH = new File("").getAbsolutePath() + "/src/de/uni_potsdam/hpi/bpt/promnicat/analysisModules/metrics/result.csv";
	private static final Logger logger = Logger.getLogger(ProcessMetrics.class.getName());
	
	/**
	 * @param args
	 * @throws IllegalTypeException 
	 * @throws IllegalArgumentException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IllegalArgumentException, IllegalTypeException, IOException {
		ProcessMetrics analysis = new ProcessMetrics();
		analysis.execute(args);
	}

	@Override
	public Object execute(String[] parameter) throws IOException,
			IllegalTypeException {
		//configure for time measurement
		long startTime = System.currentTimeMillis();
		
		//build up chain
		//set to true if the whole DB data should be used for analysis otherwise only a small sub set is used.
		boolean useFullDB = false;
		IUnitChainBuilder chainBuilder = buildUpUnitChain(useFullDB);
		
		logger.info(chainBuilder.getChain().toString() + "\n");
		
		//parser should not log parsing errors
		Logger epcParserLog = Logger.getLogger(EpcParser.class.getName());
		epcParserLog.setLevel(Level.SEVERE);
		Logger bpmnParserLog = Logger.getLogger(BpmnParser.class.getName());
		bpmnParserLog.setLevel(Level.SEVERE);

		//run chain
		@SuppressWarnings("unchecked")
		Collection<IUnitDataProcessMetrics<Object> > result = (Collection<IUnitDataProcessMetrics<Object>>) chainBuilder.getChain().execute();

		//finish time measurement
		long time = System.currentTimeMillis() - startTime;
		
		//print result
		writeResultToFile(result, time);
		
		logger.info("Time needed for metric calculation: " + (time / 1000 / 60) + " min " + (time / 1000 % 60) + " sec \n\n");
			
		return result;
	}

	/**
	 * Write the result into a CSV file
	 * @param resultSet the collected result of the chain execution
	 */
	private void writeResultToFile(Collection<IUnitDataProcessMetrics<Object>> resultSet, long time) {
		BufferedWriter writer = null;
		try {
			//open file for writing
			writer = new BufferedWriter( new FileWriter(RESULT_FILE_PATH));
			//add date as first line
			StringBuilder resultString = new StringBuilder("Finished calculation at: " + new Date().toString() + "\n");
			resultString.append("Time needed for metric calculation: " + (time / 1000 / 60) + " min " + (time / 1000 % 60) + " sec \n\n");
			resultString.append(addHeader());
			//collect result from each model
			for(IUnitDataProcessMetrics<Object> resultItem : resultSet){
				resultString.append(resultItem.toCsv(ITEMSEPARATOR));
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
		builder.append("Number of Start Events" + ITEMSEPARATOR);
		builder.append("Number of Internal Events" + ITEMSEPARATOR);
		builder.append("Number of End Events" + ITEMSEPARATOR);
		builder.append("Number of Events" + ITEMSEPARATOR);
		builder.append("Number of Activities" + ITEMSEPARATOR);
		builder.append("Number of And-Splits" + ITEMSEPARATOR);
		builder.append("Number of And-Joins" + ITEMSEPARATOR);
		builder.append("Number of Xor-Splits" + ITEMSEPARATOR);
		builder.append("Number of Xor-Joins" + ITEMSEPARATOR);
		builder.append("Number of Or-Splits" + ITEMSEPARATOR);
		builder.append("Number of Or-Joins" + ITEMSEPARATOR);
		builder.append("Number of Gateways" + ITEMSEPARATOR);
		builder.append("Number of Nodes" + ITEMSEPARATOR);
		builder.append("Number of Edges" + ITEMSEPARATOR);
		builder.append("Number of Data Nodes" + ITEMSEPARATOR);
		builder.append("Number of Roles" + ITEMSEPARATOR);
		builder.append("Diameter" + ITEMSEPARATOR);
		builder.append("Density" + ITEMSEPARATOR);
		builder.append("Density related to number of Gateways" + ITEMSEPARATOR);
		builder.append("Coefficient of Connectivity" + ITEMSEPARATOR);
		builder.append("Coefficient of Network Complexity" + ITEMSEPARATOR);
		builder.append("Cyclomatic Number" + ITEMSEPARATOR);
		builder.append("Average Connector Degree" + ITEMSEPARATOR);
		builder.append("Maximum Connector Degree" + ITEMSEPARATOR);
		builder.append("Separability" + ITEMSEPARATOR);
		builder.append("Depth" + ITEMSEPARATOR);
		builder.append("Cycling" + ITEMSEPARATOR);
		builder.append("Controlflow Complexity" + ITEMSEPARATOR);
		builder.append("Cross Connectivity\n");
		return builder.toString();
	}

	/**
	 * Create an new unit chain builder and builds up
	 * a chain to get the metrics of the {@link ProcessModel}s from the given database.
	 * @param useFullDB use full data set or if set to <code>false</code> only a small excerpt
	 * @return the builder with the created chain
	 * @throws IOException if the given configuration file path could not be found
	 * @throws IllegalTypeException if the units of the chain have incompatible input/output types
	 */
	private IUnitChainBuilder buildUpUnitChain(boolean useFullDB) throws IOException, IllegalTypeException {
		IUnitChainBuilder chainBuilder = null;
		if (useFullDB){
			chainBuilder = new UnitChainBuilder("configuration(full).properties", Constants.DATABASE_TYPES.ORIENT_DB, UnitDataProcessMetrics.class);
		} else {
			chainBuilder = new UnitChainBuilder("", Constants.DATABASE_TYPES.ORIENT_DB, UnitDataProcessMetrics.class);
		}
		//build db query
		DbFilterConfig dbFilter = new DbFilterConfig();
		dbFilter.addOrigin(Constants.ORIGINS.BPMAI);		
		dbFilter.addFormat(Constants.FORMATS.BPMAI_JSON);
		dbFilter.addNotation(Constants.NOTATIONS.BPMN1_1);
		dbFilter.addNotation(Constants.NOTATIONS.BPMN2_0);
		dbFilter.addNotation(Constants.NOTATIONS.EPC);
//		dbFilter.setLatestRevisionsOnly(true);
		chainBuilder.addDbFilterConfig(dbFilter);
		//transform to jBPT and calculate metrics
		chainBuilder.createBpmaiJsonToJbptUnit(false);
		chainBuilder.createProcessModelMetricsCalulatorUnit(true);
		
		//collect results
		chainBuilder.createSimpleCollectorUnit();
		return chainBuilder;
	}

}
