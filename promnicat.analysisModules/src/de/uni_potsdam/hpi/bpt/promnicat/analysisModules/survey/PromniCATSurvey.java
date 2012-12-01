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
package de.uni_potsdam.hpi.bpt.promnicat.analysisModules.survey;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import org.jbpt.pm.ProcessModel;

import de.uni_potsdam.hpi.bpt.promnicat.analysisModules.IAnalysisModule;
import de.uni_potsdam.hpi.bpt.promnicat.configuration.ConfigurationParser;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPersistenceApi;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.config.DbFilterConfig;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;
import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.builder.IUnitChainBuilder;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.builder.impl.UnitChainBuilder;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.impl.UnitData;

/**
 * Used to get process models for surveys and store the results
 * 
 * @author Tobias Hoppe
 *
 */
public class PromniCATSurvey implements IAnalysisModule {

	private static final Logger logger = Logger.getLogger(PromniCATSurvey.class.getName());
	
	/**
	 * @param args
	 * @throws IllegalTypeException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, IllegalTypeException {
		PromniCATSurvey survey = new PromniCATSurvey();
		survey.execute(args);
	}
	
	@Override
	public Object execute(String[] parameter) throws IOException, IllegalTypeException {
		//set to true if the whole DB data should be used for analysis otherwise only a small sub set is used.
		boolean useFullDB = false;
		//build up chain
		IUnitChainBuilder chainBuilder = buildUpUnitChain(useFullDB);
	
		logger.info(chainBuilder.getChain().toString() + "\n");		

		//configure for time measurement
		long startTime = System.currentTimeMillis();

		//run chain
		@SuppressWarnings("unchecked")
		Collection<IUnitData<Object> > result = (Collection<IUnitData<Object>>) chainBuilder.getChain().execute();

		//finish time measurement
		long time = System.currentTimeMillis() - startTime;
		logger.info("Time needed for unit chain execution: " + (time / 1000 / 60) + " min " + (time / 1000 % 60) + " sec \n\n");
		
		Collection<String> svgs = new ArrayList<String>();
		for(IUnitData<Object> unitData : result) {
			Representation repr = (Representation) unitData.getValue();
			//TODO do something with the result here
			String svg = repr.convertDataContentToString();
			svgs.add(svg);
			logger.info(svg);
		}
		return svgs;
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
		String configPath = "";
		if (useFullDB){
			configPath = "configuration(full).properties";
		}
		ConfigurationParser configParser = new ConfigurationParser(configPath);
		IPersistenceApi persistenceApi = configParser.getDbInstance();
		chainBuilder = new UnitChainBuilder(persistenceApi, configParser.getThreadCount(), UnitData.class);
		//build db query
		DbFilterConfig dbFilter = new DbFilterConfig();
		dbFilter.addOrigin(Constants.ORIGINS.BPMAI);		
		dbFilter.addFormat(Constants.FORMATS.SVG);
		dbFilter.addNotation(Constants.NOTATIONS.BPMN1_1);
		dbFilter.addNotation(Constants.NOTATIONS.BPMN2_0);
		dbFilter.addNotation(Constants.NOTATIONS.EPC);
//		dbFilter.setLatestRevisionsOnly(true);
		chainBuilder.addDbFilterConfig(dbFilter);
		
		//collect results
		chainBuilder.createSimpleCollectorUnit();
		return chainBuilder;
	}
}
