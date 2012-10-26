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

import javax.persistence.Id;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IRevision;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.AbstractPojo;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Revision;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.IPojoFactoryOrientDb;

/**
 * Wrapper for OrientDb specific {@link Revision} class.
 * @author Tobias Hoppe
 *
 */
public class RevisionOrientDb extends Revision implements IRevision {
	
	/**
	 * the id used by OrientDb, can not use the id defined in {@link AbstractPojo}
	 * due to OrientDb's injections.
	 */
	@Id
	private String dbId;

	/**
	 * This constructor is used by Orient DB. For manually instantiation use
	 * {@link IPojoFactoryOrientDb#createRevision()}.
	 */
	public RevisionOrientDb() {
		super();
	}

	/**
	 * Calls only super-constructor
	 */
	protected RevisionOrientDb(Integer number) {
		super(number);
	}
	
	/**
	 * Wrapper method to met OrientDb's method name constraints.
	 * @return {@link Revision#isLatestRevision()}
	 */
	public boolean getLatestRevision() {
		return isLatestRevision();
	}
	
	@Override
	public String getDbId() {
		return this.dbId;
	}

	@Override
	public boolean hasDbId() {
		return this.dbId != null;
	}
}
