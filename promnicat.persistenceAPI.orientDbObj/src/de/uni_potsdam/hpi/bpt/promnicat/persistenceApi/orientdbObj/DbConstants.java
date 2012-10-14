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
package de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.Model;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.Revision;

/**
 * This class represents the class names and attribute names in 
 * {@link Model}, {@link Revision} and {@link Representation} as 
 * a helper to build NoSQL for {@link PersistenceApiOrientDbObj}.
 * 
 * @author Andrina Mascher
 *
 */
public interface DbConstants {

	//class model and its attributes
	final String CLS_MODEL = "Model";
	final String ATTR_ORIGIN = "origin";
	final String ATTR_TITLE = "title";
	final String ATTR_IMPORTED_ID = "importedId";
	final String ATTR_REVISIONS = "revisions";
	
	//class revision and its attributes
	final String CLS_REVISION = "Revision";
	final String ATTR_LATEST_REVISION = "latestRevision";
	final String ATTR_LANGUAGE = "language";
	final String ATTR_MODEL = "model";
	final String ATTR_AUTHOR = "author";
	final String ATTR_METADATA = "internalMetadata";
	final String ATTR_REVISION_NUMBER = "revisionNumber";
	final String ATTR_REPRESENTATIONS = "representations";
	
	//class representation and its attributes
	final String CLS_REPRESENTATION = "Representation";
	final String ATTR_DATA_CONTENT = "dataContent";
	final String ATTR_REVISION = "revision";
	final String ATTR_DATA_PATH = "dataPath";
	final String ATTR_NOTATION = "notation";
	final String ATTR_FORMAT = "format";
}
