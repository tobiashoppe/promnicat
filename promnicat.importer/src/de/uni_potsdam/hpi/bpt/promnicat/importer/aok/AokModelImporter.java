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
package de.uni_potsdam.hpi.bpt.promnicat.importer.aok;

import java.io.IOException;

import org.json.JSONException;

import de.uni_potsdam.hpi.bpt.promnicat.importer.AbstractImporter;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPersistenceApi;

/**
 * This class is used to import models from the AOK.
 * 
 * @author Tobias Hoppe
 * 
 */
public class AokModelImporter extends AbstractImporter {

	@SuppressWarnings("unused")
	private IPersistenceApi persistenceApi = null;
	
	/**
	 * Creates a new {@link AokModelImporter} with the given {@link IPersistenceApi} used for database access.
	 * @param persistenceApi persistence API used by importer
	 */
	public AokModelImporter(IPersistenceApi persistenceApi) {
		this.persistenceApi = persistenceApi;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_potsdam.hpi.bpt.promnicat.importer.IImporter#importModelsFrom(java.net.URI)
	 */
	@Override
	public void importModelsFrom(String modelDirectory) throws IOException, JSONException {
		//File rootDir = super.checkModelPath(modelDirectory, false);
		// TODO Auto-generated method stub
		throw new AbstractMethodError();
	}

}
