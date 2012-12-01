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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
 * This is an example of how to use PromniCAT indices to store the number of nodes of {@link Representation} instances.
 * 
 * @author Andrina Mascher
 * 
 */
public class NodeCountIndex {

	PersistenceApiOrientDbObj papi;
	ProcessMetricsCalculator metrics;
	ModelParser parser;
	NumberIndex<Integer, Representation> nodeCountIndex; // remark: also works with Long

	NodeCountIndex() {
		papi = PersistenceApiOrientDbObj.getInstance("configuration.properties");
		nodeCountIndex = new NumberIndex<Integer, Representation>("nodeCountIndex", papi);
	}

	private void run() throws IllegalTypeException {
		// nodeCountIndex.dropIndex();
		nodeCountIndex.createIndex();
		writeIndex();
		readIndex();
		papi.closeDb();
	}

	private void writeIndex() {
		// get representations
		DbFilterConfig config = new DbFilterConfig();
		config.setLatestRevisionsOnly(true);
		config.addFormat(Constants.FORMAT_BPMAI_JSON);
		// config.addNotation(Constants.NOTATION_EPC);
		List<Representation> reps = papi.loadRepresentations(config);

		// calculate metrices and save in index
		int cnt = 0;
		metrics = new ProcessMetricsCalculator();
		parser = new ModelParser(false);
		// don't print parsing log messages
		Logger epcParserLog = Logger.getLogger(EpcParser.class.getName());
		epcParserLog.setLevel(Level.SEVERE);
		Logger bpmnParserLog = Logger.getLogger(BpmnParser.class.getName());
		bpmnParserLog.setLevel(Level.SEVERE);
		for (Representation rep : reps) {
			try {
				cnt++;
				String jsonString = rep.convertDataContentToString();
				Diagram bpmaiDiagram = DiagramBuilder.parseJson(jsonString);
				ProcessModel processModel = parser.transformProcess(bpmaiDiagram);

				// calc and add nrOfNodes
				int nrOfNodes = metrics.getNumberOfNodes(processModel, false);
				System.out.println(nrOfNodes + " nodes in " + rep);
				nodeCountIndex.add(nrOfNodes, rep.getDbId());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		System.out.println("found: " + cnt);
	}

	private void readIndex() {
		System.out.println("--all");
		nodeCountIndex.setSelectAll();
		List<IndexElement<Integer, Representation>> list = nodeCountIndex.load();
		for (IndexElement<Integer, Representation> e : list) {
			System.out.println(e.toString());
		}

		System.out.println("--- = 5");
		nodeCountIndex.setSelectEquals(5);
		list = nodeCountIndex.load();
		for (IndexElement<Integer, Representation> e : list) {
			System.out.println(e.toString());
		}

		System.out.println("--- >= 14");
		nodeCountIndex.setSelectGreaterOrEquals(14);
		list = nodeCountIndex.load();
		for (IndexElement<Integer, Representation> e : list) {
			System.out.println(e.toString());
		}

		System.out.println("--- <= 14");
		nodeCountIndex.setSelectLessOrEquals(14);
		list = nodeCountIndex.load();
		for (IndexElement<Integer, Representation> e : list) {
			System.out.println(e.toString());
		}

		System.out.println("--- between 14 and 20");
		nodeCountIndex.setSelectBetween(14, 20);
		list = nodeCountIndex.load();
		for (IndexElement<Integer, Representation> e : list) {
			System.out.println(e.toString());
		}

		System.out.println("--- list 5,20,13,14");
		ArrayList<Integer> keys = new ArrayList<Integer>();
		keys.add(5);
		keys.add(20);
		keys.add(13);
		keys.add(14);
		nodeCountIndex.setSelectElementsOf(keys);
		list = nodeCountIndex.load();
		for (IndexElement<Integer, Representation> e : list) {
			System.out.println(e.toString());
		}
	}

	public static void main(String[] args) throws IllegalTypeException {
		Long start = System.currentTimeMillis();

		(new NodeCountIndex()).run();

		Long time = System.currentTimeMillis() - start;
		System.out.println("Time used: " + (time / 1000 / 60) + " min " + (time / 1000 % 60) + " sec");
	}
}
