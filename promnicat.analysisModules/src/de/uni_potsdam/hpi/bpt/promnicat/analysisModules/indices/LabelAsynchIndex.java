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
package de.uni_potsdam.hpi.bpt.promnicat.analysisModules.indices;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jbpt.pm.FlowNode;
import org.jbpt.pm.NonFlowNode;
import org.jbpt.pm.ProcessModel;
import org.json.JSONException;

import de.uni_potsdam.hpi.bpt.ai.diagram.Diagram;
import de.uni_potsdam.hpi.bpt.ai.diagram.DiagramBuilder;
import de.uni_potsdam.hpi.bpt.promnicat.parser.BpmnParser;
import de.uni_potsdam.hpi.bpt.promnicat.parser.EpcParser;
import de.uni_potsdam.hpi.bpt.promnicat.parser.ModelParser;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.config.DbFilterConfig;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;
import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;

/**
 * This is an example of how to use PromniCAT indices to store the labels of elements in {@link Representation}
 * instances. To handle a large data collection, the models are read asynchronously. This means one element at a time is
 * loaded, parsed, and it's labels are saved, before the next model is processed.
 * 
 * @author Andrina Mascher
 * 
 */
public class LabelAsynchIndex {

	PersistenceApiOrientDbObj papi;
	ProcessMetricsCalculator metrics;
	ModelParser parser;
	StringIndex<Representation> labelIndex;

	LabelAsynchIndex() {
		papi = PersistenceApiOrientDbObj.getInstance("configuration(full).properties");
		labelIndex = new StringIndex<Representation>("testLabelIndex", papi);
	}

	/**
	 * @param papi
	 * @throws IllegalTypeException
	 */
	private void run() throws IllegalTypeException {
		// labelIndex.dropIndex();
		labelIndex.createIndex();
		writeIndex();
		readIndex();
		papi.closeDb();
	}

	private void writeIndex() {
		DbFilterConfig config = new DbFilterConfig();
		config.setLatestRevisionsOnly(true);
		config.addFormat(Constants.FORMAT_BPMAI_JSON);
		// config.addNotation(Constants.NOTATION_EPC);
		config.addNotation(Constants.NOTATION_BPMN2_0);
		config.addNotation(Constants.NOTATION_BPMN1_1);
		DbListener observer = new DbListener();
		// get Representations asynchronously for larger collections
		papi.loadRepresentationsAsync(config, observer);
		observer.printResult();
	}

	private void readIndex() {
		List<IndexElement<String, Representation>> list;

		// System.out.println("--all");
		// labelIndex.setSelectAll();
		// list = labelIndex.load();
		// for(IndexElement<String,Representation> e : list) {
		// System.out.println(e.toString());
		// }

		System.out.println("--equals");
		labelIndex.setSelectEquals("join team");
		list = labelIndex.load();
		for (IndexElement<String, Representation> e : list) {
			System.out.println(e.toString());
		}

		String contains = "user has";
		// String contains = "(temp)";
		// String contains = "tätigen";
		// String contains = "from";
		System.out.println("--contains: " + contains);
		labelIndex.setSelectContains(contains);
		list = labelIndex.load();
		for (IndexElement<String, Representation> e : list) {
			System.out.println(e.toString());
		}

		String[] array = { "uSer", "acC" };
		System.out.println("--multiple contains: " + Arrays.asList(array));
		labelIndex.setSelectContains(array);
		list = labelIndex.load();
		for (IndexElement<String, Representation> e : list) {
			System.out.println(e.toString());
		}

		String regex = ".*(user|account).*";
		System.out.println("--regex: " + regex);
		labelIndex.setSelectRegEx(regex);
		list = labelIndex.load();
		for (IndexElement<String, Representation> e : list) {
			System.out.println(e.toString());
			String key = e.getKey();
			Representation r = e.getPojo();
			System.out.println("key " + key + " with " + e.getDbId() + " to " + r);
		}
	}

	/**
	 * Get all labels from this process model in all nodes.
	 * 
	 * @param processModel
	 * @return
	 */
	private Collection<String> getLabels(ProcessModel processModel) {
		Collection<String> labels = new HashSet<String>();
		for (FlowNode node : processModel.getVertices()) {
			labels.add(node.getLabel());
		}
		for (NonFlowNode node : processModel.getNonFlowNodes()) {
			labels.add(node.getLabel());
		}
		return labels;
	}

	public static void main(String[] args) throws IllegalTypeException {
		long start = System.currentTimeMillis();

		(new LabelAsynchIndex()).run();

		long time = System.currentTimeMillis() - start;
		System.out.println("Time used: " + (time / 1000 / 60) + " min " + (time / 1000 % 60) + " sec");
	}

	/**
	 * This observer is used to load the data content asynchronously. It processes one model at a time, calculates the
	 * labels, and stores them in the index.
	 * 
	 * @author Andrina Mascher
	 * 
	 */
	class DbListener implements Observer {
		int cntLabel = 0;
		int cntModel = 0;

		DbListener() {
			// calculate labels and save in index
			metrics = new ProcessMetricsCalculator();
			parser = new ModelParser(false);
			// don't print parsing log messages
			Logger epcParserLog = Logger.getLogger(EpcParser.class.getName());
			epcParserLog.setLevel(Level.SEVERE);
			Logger bpmnParserLog = Logger.getLogger(BpmnParser.class.getName());
			bpmnParserLog.setLevel(Level.SEVERE);
		}

		@Override
		public void update(Observable o, Object arg) {
			try {
				cntModel++;
				Representation rep = (Representation) arg;
				String jsonString = rep.convertDataContentToString();
				Diagram bpmaiDiagram = DiagramBuilder.parseJson(jsonString);
				ProcessModel processModel = parser.transformProcess(bpmaiDiagram);

				// calc and add labels
				Collection<String> labels = getLabels(processModel);
				for (String label : labels) {
					if (label.isEmpty())
						continue;
					labelIndex.add(label, rep.getDbId());
					cntLabel++;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		public void printResult() {
			System.out.println("inserted " + cntLabel + " labels from " + cntModel + " models ");
		}
	}
}
