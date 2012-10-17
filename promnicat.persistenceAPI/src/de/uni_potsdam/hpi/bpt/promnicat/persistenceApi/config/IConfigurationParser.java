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
package de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.config;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPersistenceApi;

/**
 * This interface provides the convenience methods for the
 * PromniCAT configuration file parsing.
 * @author Tobias Hoppe
 *
 */
public interface IConfigurationParser {

	/**
	 * The configuration file is parsed and a new {@link IPersistenceApi}
	 * with the specified access rights and database path is created.
	 * 
	 * @return a {@link IPersistenceApi} instance as specified in the given configuration.
	 */
	public IPersistenceApi getDbInstance();
	
	/**
	 * The configuration file is parsed and the maximum number of {@link Thread}s to be used for unit chain
	 * execution is extracted.
	 * 
	 * @return the maximum number of {@link Thread}s used for unit chain execution.
	 */
	public Integer getThreadCount();
	
}
