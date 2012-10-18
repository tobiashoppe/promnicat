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

import com.orientechnologies.orient.core.annotation.OId;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IRepresentation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.AbstractRepresentation;

/**
 * @author Tobias Hoppe
 *
 */
public class RepresentationOrientDb extends AbstractRepresentation implements IRepresentation {

	// the id used in the database
	@OId //used on OrientDb
	protected String dbId = null;

	protected RepresentationOrientDb() {
		super();
	}
	
	protected RepresentationOrientDb(String format, String notation) {
		super(format, notation);
	}
	
	protected RepresentationOrientDb(String format, String notation, File dataFile) {
		super(format, notation, dataFile);
	}
	
	protected RepresentationOrientDb(String format, String notation, byte[] dataContent) {
		super(format, notation, dataContent);
	}
	
	/* (non-Javadoc)
	 * @see de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPojo#getDbId()
	 */
	@Override
	public String getDbId() {
		return dbId;
	}

	/* (non-Javadoc)
	 * @see de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPojo#hasDbId()
	 */
	@Override
	public boolean hasDbId() {
		return dbId != null;
	}
}
