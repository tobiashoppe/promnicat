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

import java.util.Collection;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Representation;

/**
 * This is an example of how multiple different indices can be combined to return the intersection of their result sets.
 * All individual indices would load all objects, but the {@link IndexIntersection} calculates the intersection of the
 * database ids first and only loads the relevant objects.
 * 
 * @author Andrina Mascher
 * 
 */
public class ExampleIntersection {

	public static void main(String[] args) {

		PersistenceApiOrientDbObj papi = PersistenceApiOrientDbObj.getInstance("configuration.properties");

		// choose indices that already exist
		StringIndex<Representation> labelIndex = new StringIndex<Representation>("testLabelIndex", papi);
		labelIndex.setSelectContains("account");

		NumberIndex<Integer, Representation> myNodeCountIndex = new NumberIndex<Integer, Representation>(
				"nodeCountIndex", papi);
		myNodeCountIndex.setSelectGreaterOrEquals(5);

		NumberIndex<Double, Representation> connectivityIndex = new NumberIndex<Double, Representation>(
				"connectivityIndex", papi);
		connectivityIndex.setSelectLessOrEquals(1.1);

		// intersect them
		IndexIntersection<Representation> intersection = new IndexIntersection<Representation>(papi);
		intersection.add(labelIndex);
		intersection.add(myNodeCountIndex);
		intersection.add(connectivityIndex);
		Collection<IndexCollectionElement<Representation>> result = intersection.load();

		// inspect result
		for (IndexCollectionElement<Representation> element : result) {
			System.out.println("found:");
			for (Object o : element.getIndexElements()) {
				@SuppressWarnings("unchecked")
				IndexElement<Object, Representation> ie = (IndexElement<Object, Representation>) o;
				Representation rep = ie.getPojo();
				Object key = ie.getKey();
				String indexName = ie.getIndex().getName();

				System.out.println("\t in " + indexName + ": " + key + " -> " + rep);
			}
		}
	}
}
