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
package de.uni_potsdam.hpi.bpt.promnicat.persistenceAPI.db4o.util;

import com.db4o.query.Query;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceAPI.db4o.PersistenceApiDb4o;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Model;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Revision;

/**
 * This interface contains the attribute names in {@link Model}, {@link Revision}
 * and {@link Representation} and acts as a helper to build {@link Query} for
 * {@link PersistenceApiDb4o}.
 * 
 * @author Andrina Mascher, Tobias Hoppe
 */
public interface DbConstants {

	//class model and its attributes
	public static final String ATTR_ORIGIN = "origin";
	public static final String ATTR_TITLE = "title";
	public static final String ATTR_IMPORTED_ID = "importedId";
	public static final String ATTR_REVISIONS = "revisions";
	
	//class revision and its attributes
	public static final String ATTR_LATEST_REVISION = "latestRevision";
	public static final String ATTR_LANGUAGE = "language";
	public static final String ATTR_MODEL = "model";
	public static final String ATTR_AUTHOR = "author";
	public static final String ATTR_METADATA = "internalMetadata";
	public static final String ATTR_REVISION_NUMBER = "revisionNumber";
	public static final String ATTR_REPRESENTATIONS = "representations";
	
	//class representation and its attributes
	public static final String ATTR_DATA_CONTENT = "dataContent";
	public static final String ATTR_REVISION = "revision";
	public static final String ATTR_DATA_PATH = "dataPath";
	public static final String ATTR_NOTATION = "notation";
	public static final String ATTR_FORMAT = "format";
}
