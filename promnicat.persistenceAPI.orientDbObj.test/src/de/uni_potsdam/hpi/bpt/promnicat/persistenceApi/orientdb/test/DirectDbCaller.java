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
package de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdb.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.ORecordAbstract;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IModel;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IRepresentation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.config.DbFilterConfig;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Model;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Revision;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.impl.PersistenceApiOrientDbObj;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.pojos.AnalysisRun;
import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;
import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;


public class DirectDbCaller {

	PersistenceApiOrientDbObj papi;
	static OObjectDatabaseTx db;
	
	DirectDbCaller() {
		try {
			papi = PersistenceApiOrientDbObj.getInstance("configuration.properties");
		} catch (Exception e) {
			e.printStackTrace();
		}
		papi.openDb();
		db = papi.getInternalDbAccess();
	}
	
	/**
	 * @param papi
	 * @throws IllegalTypeException 
	 */
	private void run() throws IllegalTypeException {
		
//		papi.dropDB();
//		papi.registerPojoClass(LabelStorage.class);
//		papi.registerPojoClass(AnalysisRun.class);
//		papi.deleteAllFromClass(AnalysisRun.class);
		
//		Representation r = RepresentationFactory.createRepresentationWithMultipleLinks();
//		papi.savePojo(r.getModel());
//		
//		LabelStorage l = new LabelStorage("my label", "Event");
//		AnalysisRun a = new AnalysisRun("my comment");
//		a.addStorage(l);
//		db.getEntityManager().registerEntityClasses(LabelStorage.class.getPackage().getName());
//		papi.savePojo(a);

		countClasses();

		inspectAllInClass("Representation");
		inspectAllInClass("Revision");
		inspectAllInClass("Model");
		inspectAllInClass("LabelStorage");
		inspectAllInClass("AnalysisRun");
		
		inspectAllDocsInClass("Representation");
		inspectAllDocsInClass("Revision");
		inspectAllDocsInClass("Model");
		inspectAllDocsInClass("LabelStorage");
		
		inspectAllDocsInClass("Model");
		
		System.out.println(papi.loadCompleteModelWithDbId("#5:0").toStringExtended()); 
		System.out.println(papi.loadRepresentation("#7:1"));
		System.out.println(papi.loadCompleteModelWithImportedId("420056253"));
		
		syncQuery();
		syncQueryWithConfig();
		asyncQueryWithConfig();
		asynchQueryWithChain();

		papi.closeDb();
	}
	
	public void command() {
		String delete = "DELETE FROM [#7:0]";
		papi.executeCommand(delete);
	}
	
	private void syncQuery() {
		String distinctModels = "SELECT distinct(revision.model) FROM representation WHERE" + 
		  		" notation in ['Bpmn2.0'] AND" +
		  		" format in ['Svg']";// AND " +
//		  		" revision.metadata containskey 'kX'" +
//		  		" and containsValueSubstrings(revision.metadata, ['v1']) = 'true'" +
//		  		" and revision.metadata['kX'] like 'vY'";
		
		List<Object> l = papi.load(distinctModels);
		int cnt = 0;
		for(Object o : l) {
			cnt++;
			System.out.println(o);
		}

		System.out.println("found: " + cnt);
	}

	private void syncQueryWithConfig() {
		DbFilterConfig config = new DbFilterConfig();
		
//		config.addNotation(Constants.NOTATIONS.EPC);
		config.addFormat(Constants.FORMATS.BPMAI_JSON);
//		config.addTitle("of a");
//		config.addOrigin(Constants.ORIGINS.BPMAI);
//		config.setLatestRevisionsOnly(true);
//		config.addLanguage("English");
//		config.addMetadataEntry("language", "English");
//		config.addMetadataValue("English");
//		config.addMetadataKey("typelanguage");
//		config.addMetadataEntry("k2", "v2");
//		config.addMetadataEntry("kX", "vY");
//		config.addMetadataEntry("k1", "v1");
//		config.addMetadataEntry("k1", "v1a");
//		config.addMetadataKey("k1");
//		config.addMetadataKey("k2");
//		config.addMetadataKey("kX");
//		config.addMetadataValue("v1");
//		config.addMetadataValue("vY");
//		config.addMetadataValue("v1a");
//		config.addMetadataValue("v2");

		List<IRepresentation> i = papi.loadRepresentations(config);
		int cnt = 0;
		for(IRepresentation rep : i) {
			IModel mod = rep.getModel();
			System.out.println(mod.toStringExtended() + "\n");
			cnt++;
		}
		System.out.println("found: " + cnt);
	}
	
	private void asyncQueryWithConfig() {
		DbFilterConfig config = new DbFilterConfig();
		
		config.addFormat(Constants.FORMATS.BPMAI_JSON);
//		config.addNotation(Constants.NOTATIONS.EPC);
//		config.addOrigin(Constants.ORIGINS.BPMAI);
		config.setLatestRevisionsOnly(true);
//		config.addLanguage("English");
		
		DbListener listener = new DbListener();
		
		papi.loadRepresentationsAsync(config, listener);
		listener.printResult();
	}
	
	/**
	 * @param papi
	 */
	private void countClasses() {
		System.out.println("Models: " + papi.countClass(Model.class));
		System.out.println("Revisions: " + papi.countClass(Revision.class));
		System.out.println("Representations: " + papi.countClass(Representation.class));
	}
	
	private void inspectAllInClass(String name) {
		for (Object o : db.browseClass(name)) {
			if(o instanceof Model) {
				System.out.println(((Model) o).toStringExtended());
			} else if(o instanceof AnalysisRun) {
				System.out.println(((AnalysisRun) o).toStringExtended());
			} else {
			  System.out.println(o);
			}
		}
	}
	
	@SuppressWarnings("unused")
	private void inspectAllDocsInCluster(String name) {
		ODatabaseDocumentTx dbDoc = (ODatabaseDocumentTx) db.getUnderlying();
		for (ORecordAbstract<?> doc : dbDoc.browseCluster(name)) {
			  System.out.println(doc);
		}
	}
	
	private void inspectAllDocsInClass(String name) {
		ODatabaseDocumentTx dbDoc = (ODatabaseDocumentTx) db.getUnderlying();
		for (ORecordAbstract<?> doc : dbDoc.browseClass(name)) {
			  System.out.println(doc);
		}
	}
	
	private void asynchQueryWithChain() {
		DbListener dbl = new DbListener();
		List<String> dbIds = new ArrayList<String>();
		//INFO this is just a test for one specific import, ids can change
		dbIds.add("#8:2");
		dbIds.add("#8:1");
		papi.loadRepresentationsAsync(dbIds, dbl);
		dbl.printResult();
	}
	
	/**
	 * @param args
	 * @throws IllegalTypeException 
	 */
	public static void main(String[] args) throws IllegalTypeException {
		long start = System.currentTimeMillis();

		(new DirectDbCaller()).run();
		
		long time = System.currentTimeMillis() - start;
		System.out.println("Time used: " 
					+ (time/1000 / 60) + " min "
					+ (time/1000 % 60) + " sec");
	}
	
	class DbListener implements Observer {
			public int cnt;

			@Override
			public void update(Observable o, Object arg) {
				System.out.println("updated with " + arg);
				cnt++;
			}
				
			public void printResult() {
				System.out.println("count: " + cnt);
			}
	}
}
