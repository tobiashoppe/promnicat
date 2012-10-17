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

/**
 * OrientDB specific implementation of the {@link IPojoFactory}.
 * @author Tobias Hoppe
 *
 */
public class OrientDbPojoFactory implements IPojoFactory {
	
	private static final IPojoFactory instance = new OrientDbPojoFactory();

	/**
	 * Private constructor. Use {@link IPojoFactory#getInstance()}
	 * to get the shared instance of this class.
	 */
	private OrientDbPojoFactory(){
	}

	@Override
	public IPojoFactory getInstance() {
		return instance;
	}

	@Override
	public OrientDbModel createModel() {
		return new OrientDbModel();
	}

	@Override
	public OrientDbModel createModel(String title, String origin) {
		return new OrientDbModel(title, origin);
	}

	@Override
	public OrientDbModel createModel(String title, String origin, String id) {
		return new OrientDbModel(title, origin, id);
	}

	@Override
	public OrientDbRevision createRevision() {
		return new OrientDbRevision();
	}

	@Override
	public OrientDbRevision createRevision(Integer revNumber) {
		return new OrientDbRevision(revNumber);
	}

	@Override
	public OrientDbRepresentation createRepresentation() {
		return new OrientDbRepresentation();
	}

	@Override
	public OrientDbRepresentation createRepresentation(String format, String notation) {
		return new OrientDbRepresentation(format, notation);
	}

	@Override
	public OrientDbRepresentation createRepresentation(String format, String notation, File dataFile) {
		return new OrientDbRepresentation(format, notation, dataFile);
	}

	@Override
	public OrientDbRepresentation createRepresentation(String format, String notation, byte[] dataContent) {
		return new OrientDbRepresentation(format, notation, dataContent);
	};
}
