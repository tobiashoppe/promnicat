/**
 * PromniCAT - Collection and Analysis of Business Process Models
 * Copyright (C) 2012 Cindy FÃ¤hnrich, Tobias Hoppe, Andrina Mascher
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
package de.uni_potsdam.hpi.bpt.promnicat.configuration;

import java.io.IOException;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPersistenceApi;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.config.AbstractConfigurationParser;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.config.IConfigurationParser;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.config.ConfigurationParserOrientDb;


/**
 * This class parses the PromniCAT configuration file used for
 * Orient DB database instances.
 * @author Tobias Hoppe
 *
 */
public class ConfigurationParser extends AbstractConfigurationParser implements IConfigurationParser {
	
	/**
	 * @param configPath the path to the configuration file being used. If an empty {@link String} is given,
	 *  the default path '{@link Constants#DEFAULT_CONFIG_PATH}' is used.
	 * @throws IOException if the configuration file could not be found.
	 */
	public ConfigurationParser(String configPath) throws IOException {
		super(configPath);
	}

	@Override
	public IPersistenceApi getDbInstance() {
		String dataBaseType = properties.getProperty("db.id");
		//delegate to the specified database type
		if ("OrientDb".equals(dataBaseType)) {
			return ConfigurationParserOrientDb.getDataBaseInstance(configPath);
		}
		//add further database types here if needed.
		throw new IllegalArgumentException("The provided configuration file is invalid.");
		
	}

}
