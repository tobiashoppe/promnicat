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
import java.util.Collection;
import java.util.logging.Logger;

import org.jbpt.pm.bpmn.Bpmn;
import org.jbpt.pm.epc.Epc;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.config.DbFilterConfig;
import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;
import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.builder.IUnitChainBuilder;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.builder.impl.CorrectionUnitChainBuilder;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.impl.UnitChain;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.impl.UnitData;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.impl.UnitDataJbpt;

/**
 * Parse just all BPM AI process models that are in your database (enable all or only
 * latest revisions) and only parse the ones that could correctly be parsed. Emit the 
 * amount of all process models and of those that could be parsed correctly (split off
 * to BPMN and EPC process models).
 * @author Cindy Fähnrich, Tobias Hoppe
 *
 */
public class CorrectModels implements IAnalysisModule {

	private final static Logger logger = Logger.getLogger(CorrectModels.class.getName());

	public static void main(String[] args) throws IllegalTypeException, IOException {
		CorrectModels correctModels = new CorrectModels();
		correctModels.execute(args);		
	}

	@Override
	public Object execute(String[] parameter) throws IOException, IllegalTypeException {
		//configure for time measurement
		long startTime = System.currentTimeMillis();

		//build up chain
		IUnitChainBuilder chainBuilder = new CorrectionUnitChainBuilder("configuration(full).properties", UnitDataJbpt.class);
		buildUpUnitChain(chainBuilder);

		logger.info(chainBuilder.getChain().toString());

		//run chain
		@SuppressWarnings("unchecked")
		Collection<UnitData<Object> > result = (Collection<UnitData<Object>>) chainBuilder.getChain().execute();

		//print result
		printResult(result);

		//finish time measurement
		long time = System.currentTimeMillis() - startTime;
		logger.info("Time needed: " + (time / 1000 / 60) + " min " + (time / 1000 % 60) + " sec \n\n");
		
		return result;
	}

	/**
	 * Configures and builds up the {@link UnitChain} by invoking the corresponding builder methods.
	 * @param chainBuilder
	 * @throws IllegalTypeException 
	 */
	private void buildUpUnitChain(IUnitChainBuilder chainBuilder) throws IllegalTypeException {
		//build db query
		DbFilterConfig dbFilter = new DbFilterConfig();
		dbFilter.addOrigin(Constants.ORIGINS.BPMAI);
		dbFilter.addNotation(Constants.NOTATIONS.EPC);
		dbFilter.addNotation(Constants.NOTATIONS.BPMN1_1);
		dbFilter.addNotation(Constants.NOTATIONS.BPMN2_0);
		dbFilter.addFormat(Constants.FORMATS.BPMAI_JSON);
		//dbFilter.setLatestRevisionsOnly(true);
		chainBuilder.addDbFilterConfig(dbFilter);
		//transform to jbpt - set strictness attribute to true to only take the correctly parsed ones
		chainBuilder.createBpmaiJsonToJbptUnit(true);

		//collect results
		chainBuilder.createSimpleCollectorUnit();
	}

	/**
	 * Iterates through the results and prints whether the process models are connected or not and the amount
	 * of entries and exists. 
	 * @param results from the execution of the {@link UnitChain}
	 */
	private void printResult(Collection<UnitData<Object> > results){
		int all = 0;
		int bpmn = 0;
		int epc = 0;
		for (UnitData<Object> result: results){
			all++;
			if (result.getValue() != null) {
				if (result.getValue() instanceof Bpmn){
					bpmn++;
				}
				if (result.getValue() instanceof Epc){
					epc++;
				}
			} 
			/*if (result.getRawData().equals("all")) {
				System.out.println("Overall models parsed: " + result.getValue());
			} 
			if (result.getRawData().equals("bpmn")) {
				System.out.println("BPMN models parsed: " + result.getValue());
			}
			if (result.getRawData().equals("epc")) {
				System.out.println("EPC models parsed: " + result.getValue());
			}*/
		}
		logger.info("Correctly parsed models from BPMN: " + bpmn + " EPC: " + epc + " Overall collection size: " + all);

	}
}