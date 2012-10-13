
/*
 * XXX: 
 * THIS TEST CLASS USES PROMNICAT TO IMPORT PROCESS MODELS.
 * IF PROMNICAT IS AVAILABLE, YOU CAN UNCOMMENT THIS FILE
 * AND RUN SOME GENERAL-PURPOSE TESTS.
 */

package de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.test;
//
//import java.io.BufferedOutputStream;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.UnsupportedEncodingException;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Enumeration;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipException;
//import java.util.zip.ZipFile;
//
//import org.apache.log4j.Level;
//import org.apache.log4j.Logger;
//import org.jbpt.alignment.IEntity;
//import org.jbpt.alignment.LabelEntity;
//import org.jbpt.pm.ProcessModel;
//import org.json.JSONException;
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import de.hpi.bpt.pma.Alignment;
//import de.hpi.bpt.pma.ProcessModelAlignment;
//import de.hpi.bpt.pma.assignment.Assignment;
//import de.hpi.bpt.pma.assignment.DirectAssignment;
//import de.hpi.bpt.pma.assignment.HungarianAlgorithm;
//import de.hpi.bpt.pma.assignment.StableMarriage;
//import de.hpi.bpt.pma.label.LabelAlignment;
//import de.hpi.bpt.pma.label.preprocessing.ReplaceSynonyms;
//import de.hpi.bpt.pma.label.preprocessing.PorterStemming;
//import de.hpi.bpt.pma.label.preprocessing.PreProcessingStep;
//import de.hpi.bpt.pma.label.preprocessing.StopWords;
//import de.hpi.bpt.pma.label.preprocessing.ToLowerCase;
//import de.hpi.bpt.pma.label.similarity.LabelSimilarity;
//import de.hpi.bpt.pma.label.similarity.SemanticSimilarity;
//import de.hpi.bpt.pma.label.similarity.StringEditDistance;
//import de.hpi.bpt.pma.label.similarity.matrix.ISimilarityMatrix;
//import de.uni_potsdam.hpi.bpt.ai.collection.BPMAIExport;
//import de.uni_potsdam.hpi.bpt.ai.collection.BPMAIExportBuilder;
//import de.uni_potsdam.hpi.bpt.ai.collection.Model;
//import de.uni_potsdam.hpi.bpt.ai.diagram.Diagram;
////import de.uni_potsdam.hpi.bpt.ai.collection.BPMAIExport;
////import de.uni_potsdam.hpi.bpt.ai.collection.BPMAIExportBuilder;
////import de.uni_potsdam.hpi.bpt.ai.collection.Model;
////import de.uni_potsdam.hpi.bpt.ai.diagram.Diagram;
////import de.uni_potsdam.hpi.bpt.promnicat.importer.bpmai.BpmaiImporter;
////import de.uni_potsdam.hpi.bpt.promnicat.parser.ModelParser;
//import de.uni_potsdam.hpi.bpt.promnicat.parser.ModelParser;
//
public class ProcessModelAlignmentTest {
//	private static final Logger log = Logger.getLogger(ProcessModelAlignmentTest.class);
//	private enum ProcessName {
//		PaperProcessA,PaperProcessB,PaperProcessC,
//		Example1ForPresentation, Example2ForPresentation;
//		
//		public ProcessModel model;
//	}
//
//	private static Level level = Level.ALL;
//
//	
//	@AfterClass
//	public static void tearDown() {
//		Logger.getRootLogger().setLevel(level);
//	}
//	
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	@Test
//	public void testLabelAlignment() {
//		ProcessModelAlignment pma = new ProcessModelAlignment();
//		LabelAlignment labelAlignment = new LabelAlignment();
//		labelAlignment.addPreProcessingStep(new ToLowerCase());
//		labelAlignment.addPreProcessingStep(new StopWords());
//		labelAlignment.addPreProcessingStep(new ReplaceSynonyms());
//		labelAlignment.addPreProcessingStep(new PorterStemming());
//		labelAlignment.setSimilarity(new StringEditDistance());
//		labelAlignment.setAssignment(new HungarianAlgorithm());
//		
//		pma.setAlignment((Alignment)labelAlignment);
//		ISimilarityMatrix<IEntity> result = pma.align(
//				ProcessName.Example1ForPresentation.model,
//				ProcessName.Example2ForPresentation.model
//		);
//		log.info(result);
//	}
//	
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	@Test
//	public void testPaperProcesses() {
//		ProcessModelAlignment pma = new ProcessModelAlignment();
//		LabelAlignment labelAlignment = new LabelAlignment();
//		pma.setAlignment((Alignment)labelAlignment);
//		
//		labelAlignment.addPreProcessingStep(new ToLowerCase());
//		labelAlignment.addPreProcessingStep(new StopWords());
//		labelAlignment.addPreProcessingStep(new PreProcessingStep() {
//			@Override
//			public void processAll(Collection<LabelEntity> labels1, Collection<LabelEntity> labels2) {
//				for (LabelEntity each : labels1) {
//					each.setLabel(each.getLabel().replaceAll("oldproduct1","newproduct1"));
//					each.setLabel(each.getLabel().replaceAll("oldproduct2","newproduct2"));
//				}
//			}
//		});
//
//		ArrayList<LabelSimilarity> sims = new ArrayList<LabelSimilarity>();
//		sims.add(new SemanticSimilarity());
//		ArrayList<Assignment> assigns = new ArrayList<Assignment>();
//		assigns.add(new DirectAssignment());
//		assigns.add(new StableMarriage());
//		assigns.add(new HungarianAlgorithm());
//		for (LabelSimilarity similarity : sims) {
//			labelAlignment.setSimilarity(similarity);
//			for (Assignment assign : assigns) {
//				labelAlignment.setAssignment(assign);
//				ISimilarityMatrix<IEntity> result = pma.align(ProcessName.PaperProcessA.model, ProcessName.PaperProcessB.model);
//				log.info(result);
//				result = pma.align(ProcessName.PaperProcessA.model, ProcessName.PaperProcessC.model);
//				log.info(result);
//			}
//		}
//	}
//
//	@BeforeClass
//	public static void setUp() {
//		level = Logger.getRootLogger().getLevel();
//		Logger.getRootLogger().setLevel(Level.INFO);
//		File rootDir = new File("processes/");
//		File dummyFolder = new File(rootDir + File.separator + "dummy");
//		dummyFolder.mkdir();
//		try {
//			extractAvailableSgxArchives(rootDir,dummyFolder);
//		} catch (ZipException e) {
//			log.fatal(e);
//		} catch (IOException e) {
//			log.fatal(e);
//		}
//
//		BPMAIExport directoryWalker = BPMAIExportBuilder.parseDirectory(rootDir);
//		ModelParser parser = new ModelParser();
//		for (Model bpmAiModel : directoryWalker.getModels()) {
//			try {
//				Diagram diagram = bpmAiModel.getRevisions().iterator().next().getDiagram();
//				ProcessModel model = parser.transformProcess(diagram);
//				ProcessName modelName = null;
//				if (model.toString().contains("Receive")) {
//					modelName = ProcessName.Example1ForPresentation;
//				} else if (model.toString().contains("Recieve")) {
//					modelName = ProcessName.Example2ForPresentation;
//				} else if (model.toString().contains("OldProduct")) {
//					modelName = ProcessName.PaperProcessA;
//				} else if (model.toString().contains("NewProduct")) {
//					modelName = ProcessName.PaperProcessB;
//				} else if (model.toString().contains("Stakeholder")) {
//					modelName = ProcessName.PaperProcessC;
//				}
//				modelName.model = model;
//			} catch (UnsupportedEncodingException e) {
//				log.fatal(e);
//			} catch (FileNotFoundException e) {
//				log.fatal(e);
//			} catch (JSONException e) {
//				log.fatal(e);
//			} catch (IOException e) {
//				log.fatal(e);
//			}
//		}
//	}
//	/**
//	 * <h3>!!! COPIED FROM PromniCAT {@link BpmaiImporter} !!!</h3>	 * 
//	 * 
//	 * Scans the given root directory for sgx-archives and extracts them into the dummy folder.
//	 * The extracted models can be parsed like any other process models from the BPM AI.
//	 * @param rootDir container of archives to extract
//	 * @param dummyFolder folder to extract the models to
//	 * @throws ZipException if archive extraction went wrong
//	 * @throws IOException if one of the given paths can not be read or written
//	 */
//	private static void extractAvailableSgxArchives(File rootDir, File dummyFolder) throws ZipException, IOException {
//		log.info("Extracting Archives...");
//		for(File file : rootDir.listFiles()) {
//			if((!file.isDirectory()) && (file.getName().endsWith(".sgx"))) {
//				ZipFile zipFile = new ZipFile(file);
//				Enumeration<? extends ZipEntry> entries = zipFile.entries();
//				//iterate through files of an zip archive
//				while(entries.hasMoreElements()) {
//					ZipEntry entry = (ZipEntry)entries.nextElement();
//					String entryName = entry.getName();
//					log.debug("Extracting "+entryName+"...");
//					if(entryName.contains("/")) {
//						//ignore meta data files
//						if(entryName.endsWith("_meta.json")){
//							continue;
//						}
//						//remove directory folder to fit into expected structure
//						String[] pathParts = entryName.split("/");
//						if(entryName.contains("directory_")) {
//							entryName = "";
//							for (int i = 0; i < pathParts.length; i++) {
//								if (!(pathParts[i].startsWith("directory_"))) {
//									entryName = entryName.concat(pathParts[i] + "/");
//								}
//							}
//							entryName = entryName.substring(0, entryName.length() - 1);
//						}
//						//rename process model files
//						String oldModelName = pathParts[pathParts.length - 1];
//						String[] nameParts = oldModelName.split("_");
//						if (nameParts.length > 2) {
//							String modelName = pathParts[pathParts.length - 2].split("_")[1] + "_rev" + nameParts[1] + nameParts[2];
//							entryName = entryName.replace(oldModelName, modelName);
//						}
//						//create directories
//						(new File(dummyFolder.getPath() + File.separatorChar + entryName.substring(0, entryName.lastIndexOf("/")))).mkdirs();
//					}
//					//extract process model
//					copyInputStream(zipFile.getInputStream(entry), dummyFolder.getPath() + File.separatorChar + entryName);
//				}
//				zipFile.close();
//			}
//		}
//	}
//
//	/**
//	 * <h3>!!! COPIED FROM PromniCAT {@link BpmaiImporter} !!!</h3>	 * 
//	 * 
//	 * Reads the given content and writes it into a  file with the given path.
//	 * @param in stream to read
//	 * @param targetPath path to write the read content to
//	 * @throws IOException if the specified path does not exists.
//	 */
//	private static void copyInputStream(InputStream in, String targetPath)	throws IOException {
//		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(targetPath));
//		byte[] buffer = new byte[1024];
//		int len;
//		while((len = in.read(buffer)) >= 0) {
//			bufferedOutputStream.write(buffer, 0, len);
//		}
//		in.close();
//		bufferedOutputStream.close();
//	}
}
