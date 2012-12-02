package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jbpt.alignment.IEntity;
import org.jbpt.alignment.LabelEntity;
import org.jbpt.pm.ProcessModel;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.ai.collection.BPMAIExport;
import de.uni_potsdam.hpi.bpt.ai.collection.BPMAIExportBuilder;
import de.uni_potsdam.hpi.bpt.ai.collection.Model;
import de.uni_potsdam.hpi.bpt.ai.diagram.Diagram;
import de.uni_potsdam.hpi.bpt.promnicat.parser.ModelParser;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.Alignment;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.ProcessModelAlignment;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.assignment.Assignment;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.assignment.DirectAssignment;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.assignment.HungarianAlgorithm;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.assignment.StableMarriage;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.LabelAlignment;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.preprocessing.PorterStemming;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.preprocessing.PreProcessingStep;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.preprocessing.ReplaceSynonyms;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.preprocessing.StopWords;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.preprocessing.ToLowerCase;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.similarity.LabelSimilarity;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.similarity.SemanticSimilarity;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.similarity.StringEditDistance;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.similarity.matrix.ISimilarityMatrix;

public class ProcessModelAlignmentTest {
	private static final Logger log = Logger.getLogger(ProcessModelAlignmentTest.class.getName());
	private enum ProcessName {
		PaperProcessA,PaperProcessB,PaperProcessC,
		Example1ForPresentation, Example2ForPresentation;
		
		public ProcessModel model;
	}

	private static Level level = Level.ALL;
	
	@AfterClass
	public static void tearDown() {
		log.setLevel(level);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testLabelAlignment() {
		ProcessModelAlignment pma = new ProcessModelAlignment();
		LabelAlignment labelAlignment = new LabelAlignment();
		labelAlignment.addPreProcessingStep(new ToLowerCase());
		labelAlignment.addPreProcessingStep(new StopWords());
		labelAlignment.addPreProcessingStep(new ReplaceSynonyms());
		labelAlignment.addPreProcessingStep(new PorterStemming());
		labelAlignment.setSimilarity(new StringEditDistance());
		labelAlignment.setAssignment(new HungarianAlgorithm());
		
		pma.setAlignment((Alignment)labelAlignment);
		ISimilarityMatrix<IEntity> result = pma.align(
				ProcessName.Example1ForPresentation.model,
				ProcessName.Example2ForPresentation.model
		);
		log.info(result.toString());
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testPaperProcesses() {
		ProcessModelAlignment pma = new ProcessModelAlignment();
		LabelAlignment labelAlignment = new LabelAlignment();
		pma.setAlignment((Alignment)labelAlignment);
		
		labelAlignment.addPreProcessingStep(new ToLowerCase());
		labelAlignment.addPreProcessingStep(new StopWords());
		labelAlignment.addPreProcessingStep(new PreProcessingStep() {
			@Override
			public void processAll(Collection<LabelEntity> labels1, Collection<LabelEntity> labels2) {
				for (LabelEntity each : labels1) {
					each.setLabel(each.getLabel().replaceAll("oldproduct1","newproduct1"));
					each.setLabel(each.getLabel().replaceAll("oldproduct2","newproduct2"));
				}
			}
		});

		ArrayList<LabelSimilarity> sims = new ArrayList<LabelSimilarity>();
		sims.add(new SemanticSimilarity());
		ArrayList<Assignment> assigns = new ArrayList<Assignment>();
		assigns.add(new DirectAssignment());
		assigns.add(new StableMarriage());
		assigns.add(new HungarianAlgorithm());
		for (LabelSimilarity similarity : sims) {
			labelAlignment.setSimilarity(similarity);
			for (Assignment assign : assigns) {
				labelAlignment.setAssignment(assign);
				ISimilarityMatrix<IEntity> result = pma.align(ProcessName.PaperProcessA.model, ProcessName.PaperProcessB.model);
				log.info(result.toString());
				result = pma.align(ProcessName.PaperProcessA.model, ProcessName.PaperProcessC.model);
				log.info(result.toString());
			}
		}
	}

	@BeforeClass
	public static void setUp() {
		level = log.getLevel();
		log.setLevel(Level.INFO);		
		File rootDir = new File("processes/");
		BPMAIExport directoryWalker = BPMAIExportBuilder.parseDirectory(rootDir);
		ModelParser parser = new ModelParser();
		for (Model bpmAiModel : directoryWalker.getModels()) {
			try {
				Diagram diagram = bpmAiModel.getRevisions().iterator().next().getDiagram();
				ProcessModel model = parser.transformProcess(diagram);
				ProcessName modelName = null;
				if (model.toString().contains("Receive")) {
					modelName = ProcessName.Example1ForPresentation;
				} else if (model.toString().contains("Recieve")) {
					modelName = ProcessName.Example2ForPresentation;
				} else if (model.toString().contains("OldProduct")) {
					modelName = ProcessName.PaperProcessA;
				} else if (model.toString().contains("NewProduct")) {
					modelName = ProcessName.PaperProcessB;
				} else if (model.toString().contains("Stakeholder")) {
					modelName = ProcessName.PaperProcessC;
				}
				modelName.model = model;
			} catch (Exception e) {
				log.severe(e.getMessage());
			}
		}
	}
}
