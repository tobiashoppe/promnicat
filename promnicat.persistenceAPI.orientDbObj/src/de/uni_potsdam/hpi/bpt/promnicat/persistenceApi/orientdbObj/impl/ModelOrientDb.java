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

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IModel;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.AbstractPojo;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Model;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.IPojoFactoryOrientDb;

/**
 * Wrapper for OrientDb specific {@link Model} class.
 * 
 * @author Tobias Hoppe
 * 
 */
public class ModelOrientDb extends Model implements IModel {
	
	/**
	 * the id used by OrientDb, can not use the id defined in {@link AbstractPojo}
	 * due to OrientDb's injections.
	 */
	@Id 
	private String dbId;

	/**
	 * This constructor is used by Orient DB. For manually instantiation use
	 * {@link IPojoFactoryOrientDb#createModel()}.
	 */
	public ModelOrientDb() {
		super();
	}
	
	/**
	 * Calls only super-constructor
	 */
	protected ModelOrientDb(String title, String origin) {
		super(title, origin);
	}

	/**
	 * Calls only super-constructor
	 */
	protected ModelOrientDb(String title, String origin, String id) {
		super(title, origin, id);
	}

	@Override
	public String getDbId() {
		return dbId;
	}

	@Override
	public boolean hasDbId() {
		return dbId != null;
	}
		
}
