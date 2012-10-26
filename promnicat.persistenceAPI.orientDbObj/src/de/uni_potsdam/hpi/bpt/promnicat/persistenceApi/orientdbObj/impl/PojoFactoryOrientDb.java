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
package de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.impl;

import java.io.File;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPojoFactory;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.IPojoFactoryOrientDb;

/**
 * OrientDB specific implementation of the {@link IPojoFactory}.
 * @author Tobias Hoppe
 *
 */
public class PojoFactoryOrientDb implements IPojoFactoryOrientDb {

	private static final IPojoFactoryOrientDb instance = new PojoFactoryOrientDb();
	
	/**
	 * Private constructor. Use {@link IPojoFactory#init()}
	 * to get the shared instance of this class.
	 */
	private PojoFactoryOrientDb(){
	}

	/**
	 * @return the shared instance
	 */
	public static IPojoFactoryOrientDb init() {
		return instance;
	}

	@Override
	public ModelOrientDb createModel() {
		return new ModelOrientDb();
	}

	@Override
	public ModelOrientDb createModel(String title, String origin) {
		return new ModelOrientDb(title, origin);
	}

	@Override
	public ModelOrientDb createModel(String title, String origin, String id) {
		return new ModelOrientDb(title, origin, id);
	}

	@Override
	public RevisionOrientDb createRevision() {
		return new RevisionOrientDb();
	}

	@Override
	public RevisionOrientDb createRevision(Integer revNumber) {
		return new RevisionOrientDb(revNumber);
	}

	@Override
	public RepresentationOrientDb createRepresentation() {
		return new RepresentationOrientDb();
	}

	@Override
	public RepresentationOrientDb createRepresentation(String format, String notation) {
		return new RepresentationOrientDb(format, notation);
	}

	@Override
	public RepresentationOrientDb createRepresentation(String format, String notation, File dataFile) {
		return new RepresentationOrientDb(format, notation, dataFile);
	}

	@Override
	public RepresentationOrientDb createRepresentation(String format, String notation, byte[] dataContent) {
		return new RepresentationOrientDb(format, notation, dataContent);
	};
}
