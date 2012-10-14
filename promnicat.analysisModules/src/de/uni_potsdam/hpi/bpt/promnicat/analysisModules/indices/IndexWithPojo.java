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

import de.uni_potsdam.hpi.bpt.promnicat.analysisModules.metrics.ProcessMetricsCalculator;
import de.uni_potsdam.hpi.bpt.promnicat.analysisModules.nodeName.pojos.LabelStorage;
import de.uni_potsdam.hpi.bpt.promnicat.parser.ModelParser;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.PersistenceApiOrientDbObj;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.index.IndexElement;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.index.NumberIndex;
import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;

/**
 * This is an example of how to use PromniCAT indices to store some pojos, in this case {@link LabelStorage}.
 * 
 * @author Andrina Mascher
 * 
 */
public class IndexWithPojo {

	PersistenceApiOrientDbObj papi;
	ProcessMetricsCalculator metrics;
	ModelParser parser;
	NumberIndex<Integer, LabelStorage> pojoIndex;

	IndexWithPojo() {
		papi = PersistenceApiOrientDbObj.getInstance("configuration.properties");
		pojoIndex = new NumberIndex<Integer, LabelStorage>("myPojoIndex", papi);
	}

	private void run() throws IllegalTypeException {
		// pojoIndex.dropIndex();
		pojoIndex.createIndex();
		writeIndex();
		readIndex();
		papi.closeDb();
	}

	private void writeIndex() {

		LabelStorage pojo = new LabelStorage("this is a label", "this is a class name");
		String dbId = papi.savePojo(pojo);
		pojoIndex.add(5, dbId);

		LabelStorage pojo2 = new LabelStorage("this is another label", "this is another class name");
		String dbId2 = papi.savePojo(pojo2);
		pojoIndex.add(12, dbId2);

		LabelStorage pojo3 = new LabelStorage("this is a third label", "this is a class name");
		String dbId3 = papi.savePojo(pojo3);
		pojoIndex.add(5, dbId3);

		System.out.println("3 written");
	}

	private void readIndex() {
		papi.registerPojoClass(LabelStorage.class);

		System.out.println("--all");
		pojoIndex.setSelectAll();
		List<IndexElement<Integer, LabelStorage>> list = pojoIndex.load();
		for (IndexElement<Integer, LabelStorage> e : list) {
			System.out.println(e.toString());
			e.loadPojo(papi);
			System.out.println(e.getPojo());
		}

		System.out.println("--- = 5");
		pojoIndex.setSelectEquals(5);
		list = pojoIndex.load();
		for (IndexElement<Integer, LabelStorage> e : list) {
			System.out.println(e.toString());
		}

		System.out.println("--- >= 12");
		pojoIndex.setSelectGreaterOrEquals(12);
		list = pojoIndex.load();
		;
		for (IndexElement<Integer, LabelStorage> e : list) {
			System.out.println(e.toString());
		}

		System.out.println("--- <= 12");
		pojoIndex.setSelectLessOrEquals(12);
		list = pojoIndex.load();
		for (IndexElement<Integer, LabelStorage> e : list) {
			System.out.println(e.toString());
		}

		System.out.println("--- between 10 and 12");
		pojoIndex.setSelectBetween(10, 12);
		list = pojoIndex.load();
		for (IndexElement<Integer, LabelStorage> e : list) {
			System.out.println(e.toString());
		}

		System.out.println("--- list 5,20,13,14");
		ArrayList<Integer> keys = new ArrayList<Integer>();
		keys.add(5);
		keys.add(20);
		keys.add(13);
		keys.add(14);
		pojoIndex.setSelectElementsOf(keys);
		list = pojoIndex.load();
		for (IndexElement<Integer, LabelStorage> e : list) {
			System.out.println(e.toString());
		}
	}

	public static void main(String[] args) throws IllegalTypeException {
		long start = System.currentTimeMillis();

		(new IndexWithPojo()).run();

		long time = System.currentTimeMillis() - start;
		System.out.println("Time used: " + (time / 1000 / 60) + " min " + (time / 1000 % 60) + " sec");
	}
}
