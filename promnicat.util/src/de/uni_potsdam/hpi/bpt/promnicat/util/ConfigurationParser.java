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
package de.uni_potsdam.hpi.bpt.promnicat.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPersistenceApi;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.PersistenceApiOrientDbObj;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnitChain;

/**
 * This class parses the PromniCAT configuration file.
 * 
 * @author Tobias Hoppe
 *
 */
public class ConfigurationParser {

	private Properties properties;
	
	/**
	 * @param configPath the path to the configuration file being used. If an empty {@link String} is given,
	 *  the default path '{@link Constants#DEFAULT_CONFIG_PATH}' is used.
	 * @throws IOException if the configuration file could not be found.
	 */
	public ConfigurationParser(String configPath) throws IOException {
		this.properties = new Properties();
		if (configPath.isEmpty()) {
			configPath = Constants.DEFAULT_CONFIG_PATH;
		}
		BufferedInputStream stream = new BufferedInputStream(new FileInputStream(configPath));
		this.properties.load(stream);
		stream.close();
	}
	
	/**
	 * The configuration file is parsed and a new {@link IPersistenceApi}
	 * with the specified access rights and database path is created.
	 * 
	 * @param database specifies the type of {@link IPersistenceApi} to instantiate
	 * 
	 * @return a {@link IPersistenceApi} instance as specified in the given configuration.
	 */
	public IPersistenceApi getDbInstance(Constants.DATABASE_TYPES database) {
		switch (database){
			case ORIENT_DB: {
				return parseOrientDbConfiguration();
			}
			default: {
				return null;
			}
		}
	}
	
	/**
	 * The configuration file is parsed and the maximum number of {@link Thread}s to be used for {@link IUnitChain}
	 * execution is extracted.
	 * 
	 * @return the maximum number of {@link Thread}s used for {@link IUnitChain} execution.
	 */
	public Integer getThreadCount(){
		String maxNumberOfThreadsString = this.properties.getProperty(Constants.MAX_NUMBER_OF_THREADS);
		if (maxNumberOfThreadsString == null){
			throw new IllegalArgumentException("The provided configuration file is invalid.");
		}
		return new Integer(maxNumberOfThreadsString);
	}
	
	/**
	 * The configuration file is parsed and a new {@link IPersistenceApi} instance is created.
	 * 
	 * @return a {@link IPersistenceApi} instance as specified in the given configuration.
	 */
	private IPersistenceApi parseOrientDbConfiguration() {
		
		String dbPath = properties.getProperty(Constants.DB_Path);
		String dbUser = properties.getProperty(Constants.DB_USER);
		String dbPassword = properties.getProperty(Constants.DB_PASSWD);
		if (dbPath == null || dbUser == null || dbPassword == null) {
			throw new IllegalArgumentException("The provided configuration file is invalid.");
		}		
		return new PersistenceApiOrientDbObj(dbPath, dbUser, dbPassword);
	}
}
