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
package de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl;

import java.io.File;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPojoFactory;


/**
 * @author Tobias Hoppe
 *
 */
public class PojoFactory implements IPojoFactory {
	
	private static final IPojoFactory instance = new PojoFactory();
	
	/**
	 * Private constructor. Use {@link IPojoFactory#init()}
	 * to get the shared instance of this class.
	 */
	private PojoFactory() {	
	};

	public static IPojoFactory init() {
		return instance;
	}

	@Override
	public AbstractModel createModel() {
		return new Model();
	}

	@Override
	public AbstractModel createModel(String title, String origin) {
		return new Model(title, origin);
	}

	@Override
	public AbstractModel createModel(String title, String origin, String id) {
		return new Model(title, origin, id);
	}

	@Override
	public Revision createRevision() {
		return new Revision();
	}

	@Override
	public Revision createRevision(Integer revNumber) {
		return new Revision(revNumber);
	}

	@Override
	public Representation createRepresentation() {
		return new Representation();
	}

	@Override
	public Representation createRepresentation(String format, String notation) {
		return new Representation(format, notation);
	}

	@Override
	public Representation createRepresentation(String format, String notation, File dataFile) {
		return new Representation(format, notation, dataFile);
	}

	@Override
	public Representation createRepresentation(String format, String notation, byte[] dataContent) {
		return new Representation(format, notation, dataContent);
	}
}
