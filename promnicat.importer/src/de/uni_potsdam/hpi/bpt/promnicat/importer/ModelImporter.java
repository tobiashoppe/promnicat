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
package de.uni_potsdam.hpi.bpt.promnicat.importer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import org.jdom.JDOMException;
import org.json.JSONException;

import de.uni_potsdam.hpi.bpt.promnicat.configuration.ConfigurationParser;
import de.uni_potsdam.hpi.bpt.promnicat.importer.aok.AokModelImporter;
import de.uni_potsdam.hpi.bpt.promnicat.importer.bpmai.BpmaiImporter;
import de.uni_potsdam.hpi.bpt.promnicat.importer.npb.NPBImporter;
import de.uni_potsdam.hpi.bpt.promnicat.importer.sap_rm.SapReferenceModelImporter;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPersistenceApi;
import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;

/**
 * This class is used to fetch business process models from a given path
 * or even a set of paths. This paths have to be both local file
 * system paths.
 * <br><br>
 * Info: Each importer is used once to import the business process model collection it
 * is associated with. Afterwards, the importers can be used to update the
 * framework's database with the latest changes of the external model
 * collection. Therefore, the method called for the initial import is used
 * again. The framework itself decides whether an update should be performed or
 * an initial import.
 * 
 * @author Tobias Hoppe
 * 
 */
public class ModelImporter {

	public static final String WRONG_USAGE_MESSAGE = "Wrong usage, see help message for correct usage.";
	private final static Logger logger = Logger.getLogger(ModelImporter.class.getName());

	/**
	 * Usage: <code>'Path to config' Collection PATH [PATH2 [PATH3] ...]]</code>
	 * </br></br>
	 * <code>Path to config</code> is a path in file system to the configuration file being used
	 * for import. It can be given relative to the PromniCAT folder.</br>
	 * If an empty string is provided, the default file 'PromniCAT/configuration.properties' is used.</br></br>
	 * <code>Collection</code> is the process model collection and can be one of
	 * 'BPMN', 'NPB', 'SAP_RM' or 'AOK' </br></br>
	 * <code>PATH</code> is a path in file system to a directory containing the models that should be imported.
	 */
	public static void main(String[] args) {

		// wrong number of parameter?
		if (args.length < 3) {
			printHelpMessage();
			return;
		}

		try {
			//read configuration file
			IPersistenceApi persistenceApi = new ConfigurationParser(args[0]).getDbInstance();
			
			//import models			
			// BPMAI model?
			if (args[1].toUpperCase().equals(Constants.ORIGIN_BPMAI)) {
				startImport(new BpmaiImporter(persistenceApi), args);
				return;
			}
			// NPB model?
			if (args[1].toUpperCase().equals(Constants.ORIGIN_NPB)) {
				startImport(new NPBImporter(persistenceApi), args);
				return;
			}
			// SAP_RM model?
			if (args[1].toUpperCase().equals(Constants.ORIGIN_SAP_RM)) {
				startImport(new SapReferenceModelImporter(persistenceApi), args);
				return;
			}
			// AOK model?
			if (args[1].toUpperCase().equals(Constants.ORIGIN_AOK)) {
				startImport(new AokModelImporter(persistenceApi), args);
				return;
			}
			// wrong argument value
			printHelpMessage();
			throw new IllegalArgumentException(WRONG_USAGE_MESSAGE);
		} catch (Exception e) {
			logger.severe(e.getMessage());
			String stackTraceString = "";
			for (StackTraceElement ste : e.getStackTrace()) {
				stackTraceString = stackTraceString.concat(ste.toString()
						+ "\n");
			}
			logger.severe(stackTraceString);
		}
	}

	/**
	 * Start import of the given path(s) using the given {@link IImporter}. Furthermore, the time
	 * needed for the import is logged.
	 * 
	 * @param importer the {@link IImporter} to be used for the process model import
	 * @param args the parameter the main class has been executed with
	 * @throws IOException if one of the given file paths could not be resolved.
	 * @throws JSONException if a JSON file must be parsed and the JSON file format is invalid.
	 * @throws JDOMException if a XML file must be parsed and the XML file format is invalid.
	 */
	private static void startImport(IImporter importer, String[] args) throws IOException, JSONException, JDOMException {

		long startTime, endTime;
		startTime = System.currentTimeMillis();

		if (args.length > 2) {
			importer.importModelsFrom(getModelPaths(args));
		} else {
			importer.importModelsFrom(args[2]);
		}

		endTime = System.currentTimeMillis();
		logger.info("Time used for import: "
				+ ((endTime - startTime) / 1000 / 60) + " min "
				+ ((endTime - startTime) / 1000 % 60) + " sec");
	}

	/**
	 * Collects all given process model paths and returns them.
	 * @param args the parameter the main class has been executed with
	 * @return a {@link Collection} of all model paths
	 */
	private static Collection<String> getModelPaths(String[] args) {
		Collection<String> modelDirectories = new ArrayList<String>();
		for (int i = 2; i < args.length; i++) {
			modelDirectories.add(args[i]);
		}
		return modelDirectories;
	}

	/**
	 * Log the usage message
	 */
	private static void printHelpMessage() {
		String infoMsg = "Usage: <Path to config> <Collection> <URI> [URI2 [URI3] ...]]\n" +
			"<Path to config> is a path in file system to the configuration file being used for import(relative to PromniCAT folder)." +
			"If an empty string is provided, the default file 'PromniCAT/configuration.properties' is used.\n" +
			"<Collection> can be 'BPMAI', 'NPB', 'SAP_RM' or 'AOK'\n" +
			"<URI> is a path in file system to a directory containing the models that should be imported.";
		logger.info(infoMsg);
	}
	
}
