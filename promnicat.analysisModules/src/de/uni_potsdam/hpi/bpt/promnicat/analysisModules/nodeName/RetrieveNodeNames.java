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
package de.uni_potsdam.hpi.bpt.promnicat.analysisModules.nodeName;

import java.io.IOException;

import de.uni_potsdam.hpi.bpt.promnicat.analysisModules.IAnalysisModule;
import de.uni_potsdam.hpi.bpt.promnicat.analysisModules.nodeName.pojos.AnalysisRun;
import de.uni_potsdam.hpi.bpt.promnicat.analysisModules.nodeName.pojos.LabelStorage;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.AbstractPojo;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.PersistenceApiOrientDbObj;
import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;

public class RetrieveNodeNames implements IAnalysisModule {

	private static final String analysisRunId = "#11:1"; // see output of CalcAndSaveNodeName.java for correct id
	private PersistenceApiOrientDbObj papi;
	private static final String CONFIGURATION_FILE = "configuration.properties";
	
	/**
	 * @param args
	 * @throws IllegalTypeException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, IllegalTypeException {
		RetrieveNodeNames analysis = new RetrieveNodeNames();
		analysis.execute(args);
	}

	@Override
	public Object execute(String[] parameter) throws IOException,
			IllegalTypeException {
		papi = PersistenceApiOrientDbObj.getInstance(CONFIGURATION_FILE);
		papi.registerPojoPackage(LabelStorage.class.getPackage().getName());

		//load analysis run
		AnalysisRun analyse = (AnalysisRun)papi.loadPojo(analysisRunId);
		System.out.println(analyse);
		
		//load all representations for each labelStorage
		for(AbstractPojo storage : analyse.getStorages()) {
			LabelStorage ls = (LabelStorage) storage;
			System.out.println(ls);
			Representation rep = (Representation) papi.loadPojo(ls.getRepresentationId());
			System.out.println(rep);
		}
		return analyse;
	}
}
