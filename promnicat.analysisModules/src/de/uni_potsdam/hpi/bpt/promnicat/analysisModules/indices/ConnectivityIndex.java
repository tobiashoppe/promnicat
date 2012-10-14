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
import de.uni_potsdam.hpi.bpt.promnicat.analysisModules.metrics.ProcessMetricsCalculator;
import de.uni_potsdam.hpi.bpt.promnicat.parser.BpmnParser;
import de.uni_potsdam.hpi.bpt.promnicat.parser.EpcParser;
import de.uni_potsdam.hpi.bpt.promnicat.parser.ModelParser;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.DbFilterConfig;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.PersistenceApiOrientDbObj;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.index.IndexElement;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.index.NumberIndex;
import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;
import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;

/**
 * This is an example of how to use PromniCAT indices to store the connectivity values of {@link Representation}
 * instances.
 * 
 * @author Andrina Mascher
 * 
 */
public class ConnectivityIndex {

	PersistenceApiOrientDbObj papi;
	ProcessMetricsCalculator metrics;
	ModelParser parser;
	NumberIndex<Double, Representation> connectivityIndex; //remark: also works with Float

	ConnectivityIndex() {
		papi = PersistenceApiOrientDbObj.getInstance("configuration.properties");
		connectivityIndex = new NumberIndex<Double, Representation>("connectivityIndex", papi);
	}

	private void run() throws IllegalTypeException {
		// connectivityIndex.dropIndex();
		connectivityIndex.createIndex();
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

				// calculate and add connectivity
				double connectivity = metrics.getCoefficientOfConnectivity(processModel, false);
				System.out.println(connectivity + " is connectivity in " + rep);
				connectivityIndex.add((Double) connectivity, rep.getDbId());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		System.out.println("found: " + cnt);
	}

	private void readIndex() {
		System.out.println("--all");
		connectivityIndex.setSelectAll();
		List<IndexElement<Double, Representation>> list = connectivityIndex.load();
		for (IndexElement<Double, Representation> e : list) {
			System.out.println(e.toString());
		}

		System.out.println("--- = 1.1");
		connectivityIndex.setSelectEquals(1.1);
		list = connectivityIndex.load();
		for (IndexElement<Double, Representation> e : list) {
			System.out.println(e.toString());
		}

		System.out.println("--- >= 0.8");
		connectivityIndex.setSelectGreaterOrEquals(0.8);
		list = connectivityIndex.load();
		for (IndexElement<Double, Representation> e : list) {
			System.out.println(e.toString());
		}

		System.out.println("--- <= 0.8");
		connectivityIndex.setSelectLessOrEquals(0.8);
		list = connectivityIndex.load();
		for (IndexElement<Double, Representation> e : list) {
			System.out.println(e.toString());
		}

		System.out.println("--- between 0.7 and 1.1");
		connectivityIndex.setSelectBetween(0.7, 1.0);
		list = connectivityIndex.load();
		for (IndexElement<Double, Representation> e : list) {
			System.out.println(e.toString());
		}

		System.out.println("--- list 0.7, 0.8");
		ArrayList<Double> keys = new ArrayList<Double>();
		keys.add(5.0);
		keys.add(20.0);
		keys.add(13.0);
		keys.add(14.0);
		connectivityIndex.setSelectElementsOf(keys);
		list = connectivityIndex.load();
		for (IndexElement<Double, Representation> e : list) {
			System.out.println(e.toString());
		}
	}

	public static void main(String[] args) throws IllegalTypeException {
		long start = System.currentTimeMillis();

		(new ConnectivityIndex()).run();

		long time = System.currentTimeMillis() - start;
		System.out.println("Time used: " + (time / 1000 / 60) + " min " + (time / 1000 % 60) + " sec");
	}
}
