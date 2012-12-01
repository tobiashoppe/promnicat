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
package de.uni_potsdam.hpi.bpt.promnicat.analysisModules.correctionModule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.correctors.AbstractCorrector;
import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.correctors.BundledEdgeCorrector;
import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.correctors.DirectedEdgeAttacher;
import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.correctors.EdgeTypeCorrector;
import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.utilityUnits.ErrorDetectorUnit;
import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.utilityUnits.UnparsableModelFilterUnit;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.config.DbFilterConfig;
import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;
import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.builder.IUnitChainBuilder;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.builder.impl.CorrectionUnitChainBuilder;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.impl.UnitData;


/**
 * Module for correcting structural incorrect BPMN process models or EPC models
 * @author Christian Kieschnick
 *
 */
public class DiagramCorrectorModule  {
	private final static Logger logger = Logger.getLogger(DiagramCorrectorModule.class.getName());
	
	/**
	 * the corrector which should be applied to incorrect models
	 */
	private List<AbstractCorrector> correctors = new ArrayList<AbstractCorrector>();

	public DiagramCorrectorModule(){
		correctors.add(new BundledEdgeCorrector());
		correctors.add(new DirectedEdgeAttacher());
		correctors.add(new EdgeTypeCorrector());
	}
	
	/**
	 * prepare the unit chain to load BPMN 1.1, BPMN 2.0 and EPC models of the BPMAI model collection
	 * @param onlyLatestRevision load only the latest revision
	 * @return the prepared unit chain
	 * @throws IOException
	 */
	private static CorrectionUnitChainBuilder prepareUnitChainBuilder(boolean onlyLatestRevision) throws IOException{
		CorrectionUnitChainBuilder chainBuilder = new CorrectionUnitChainBuilder("configuration(full).properties", UnitData.class);
		
		//build db query
		DbFilterConfig dbFilter = new DbFilterConfig();
		dbFilter.addOrigin(Constants.ORIGINS.BPMAI);		
		dbFilter.addFormat(Constants.FORMATS.BPMAI_JSON);
		dbFilter.addNotation(Constants.NOTATIONS.BPMN1_1);
		dbFilter.addNotation(Constants.NOTATIONS.BPMN2_0);
		dbFilter.addNotation(Constants.NOTATIONS.EPC);
	
		dbFilter.setLatestRevisionsOnly(onlyLatestRevision);
		chainBuilder.addDbFilterConfig(dbFilter);
		return chainBuilder;
	}
	
	/**
	 * 
	 * @param headRevision
	 * @throws IOException
	 * @throws IllegalTypeException
	 */
	@SuppressWarnings("unused")
	private static void collectFaultyModels(boolean headRevision) throws IOException, IllegalTypeException{
		//build up chain
		//set to true if the whole DB data should be used for analysis otherwise only a small sub set is used.
		CorrectionUnitChainBuilder chainBuilder = prepareUnitChainBuilder(headRevision);
		chainBuilder.createBpmaiErrorsModelCollection(true);
		chainBuilder.createSimpleCollectorUnit();
		
		logger.info(chainBuilder.getChain().toString() + "\n");

		//run chain
		@SuppressWarnings("unchecked")
		Collection<UnitData<String> > result = (Collection<UnitData<String>>) chainBuilder.getChain().execute();
		// result contains the identifier of the models and may be exported when needed
		logger.info("\n\tEPC accepted: "+ UnparsableModelFilterUnit.importedEpc+" rejected: "+UnparsableModelFilterUnit.rejectedEpc
				  + "\n\tBPMN accepted: "+ UnparsableModelFilterUnit.importedBpmn+" rejected: "+UnparsableModelFilterUnit.rejectedBpmn
			 	  + "\n\tALL accepted: "+UnparsableModelFilterUnit.all+ " rejected: "+UnparsableModelFilterUnit.unsupported
			 	  + "\n" + AbstractCorrector.correctionStatistics());
	}
	
	private static void collectStatisticsBeforeAndAfterCorrection(boolean headRevision) throws IOException, IllegalTypeException{
		//build up chain
		//set to true if the whole DB data should be used for analysis otherwise only a small sub set is used.
		CorrectionUnitChainBuilder chainBuilder = prepareUnitChainBuilder(headRevision);
		ErrorDetectorUnit before = new ErrorDetectorUnit();
		ErrorDetectorUnit after = new ErrorDetectorUnit();
		chainBuilder.createBpmaiCorrectorModelCollectionWithStatistics(true, before, after);
		
		logger.info(chainBuilder.getChain().toString() + "\n");

		//run chain
		chainBuilder.getChain().execute();
		
		logger.info("\n\tEPC accepted: "+ UnparsableModelFilterUnit.importedEpc+" rejected: "+UnparsableModelFilterUnit.rejectedEpc
				  + "\n\tBPMN accepted: "+ UnparsableModelFilterUnit.importedBpmn+" rejected: "+UnparsableModelFilterUnit.rejectedBpmn
			 	  + "\n\tALL accepted: "+UnparsableModelFilterUnit.all+ " rejected: "+UnparsableModelFilterUnit.unsupported
			 	  + "\n" + AbstractCorrector.correctionStatistics()
			 	  + "\nBefore:"
			 	  + "\n" + before.printStatistics()
			 	  + "\nAfter:"
			 	  + "\n" + after.printStatistics());
	}
	
	
	@SuppressWarnings("unused")
	private static void collectStatisticsWithoutCorrection(boolean headRevision) throws IOException, IllegalTypeException{
		//build up chain
		//set to true if the whole DB data should be used for analysis otherwise only a small sub set is used.
		CorrectionUnitChainBuilder chainBuilder = prepareUnitChainBuilder(headRevision);
		ErrorDetectorUnit detector = new ErrorDetectorUnit();
		chainBuilder.createBpmaiJsonToJbptWithStatistics(true, detector);
		
		logger.info(chainBuilder.getChain().toString() + "\n");

		//run chain
		chainBuilder.getChain().execute();
		
		logger.info("\n\tEPC accepted: "+ UnparsableModelFilterUnit.importedEpc+" rejected: "+UnparsableModelFilterUnit.rejectedEpc
				  + "\n\tBPMN accepted: "+ UnparsableModelFilterUnit.importedBpmn+" rejected: "+UnparsableModelFilterUnit.rejectedBpmn
			 	  + "\n\tALL accepted: "+UnparsableModelFilterUnit.all+ " rejected: "+UnparsableModelFilterUnit.unsupported
			 	  + "\n" + AbstractCorrector.correctionStatistics()
			 	  + "\n" + detector.printStatistics());
	}
	
	/**
	 * This entry point is only for debugging purposes - the best way to use the correction
	 * is to use the fix method of the {@link DiagramCorrectorModule} or include the createCorrectedDiagramUnit 
	 * of the {@link IUnitChainBuilder} between the createBpmaiJsonToDiagramUnit and further units
	 * @param args
	 * @throws IllegalTypeException 
	 * @throws IllegalArgumentException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IllegalArgumentException, IllegalTypeException, IOException {
		boolean headRevision = true;
//		correctModels(headRevision);
		collectStatisticsBeforeAndAfterCorrection(headRevision);
//		inspectModelsWithStatistics(headRevision);
	}
}
