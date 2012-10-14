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
package de.uni_potsdam.hpi.bpt.promnicat.launch;

import java.util.logging.Logger;

import com.jdotsoft.jarloader.JarClassLoader;

/**
 * Launcher class for import of process models.
 * @author Tobias Hoppe
 *
 */
public class ModelImporterLauncher {

	/**
	 * Start the model importer with the given arguments.
	 * @param args arguments passed on to the model importer of PromniCAT
	 */
	public static void main(String[] args) {
		JarClassLoader jcl = new JarClassLoader();
		try {
			jcl.invokeMain("de.uni_potsdam.hpi.bpt.promnicat.importer.ModelImporter", args);
		} catch (Throwable e) {
			Logger.getLogger(ModelImporterLauncher.class.getName()).
				severe("Failed to start PromniCAT's model importer. Got message: " + e.getMessage());
		}
	}
}
