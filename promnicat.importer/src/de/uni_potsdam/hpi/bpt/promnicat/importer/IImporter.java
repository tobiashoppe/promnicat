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
import java.util.Collection;

import org.jdom.JDOMException;
import org.json.JSONException;

/**
 * The importer is used to fetch business process models from a given path or even a set of paths.
 * </br></br>
 * Each importer is used once to import the business process model collection it is associated with. Afterwards, the
 * importers can be used to update the framework's database with the latest changes of the external model collection.
 * Therefore, the method called for the initial import is used again. The framework itself decides whether an update
 * should be performed or an initial import.
 * 
 * @author Tobias Hoppe
 */
public interface IImporter {

	/**
	 * Import a model from the given location. In case a path to a directory is given, all models within this
	 * directory are imported.
	 * 
	 * @param modelDirectory
	 *            the path to the file or directory that contains the model(s), that should be imported
	 * @throws IOException
	 *             if the given path could not be found.
	 * @throws JSONException
	 *             if the given path does not point to a valid model file.
	 * @throws JDOMException
	 *             if the given path does not point to a valid model file.
	 */
	public void importModelsFrom(String modelDirectory) throws IOException, JSONException, JDOMException;

	/**
	 * Import a model from each of the given locations. In case one of the given paths points to a directory, all
	 * models within this directory are imported.<br/>
	 * Note: If all models of a directory should be imported use {@link IImporter#importModelsFrom(String)} instead.
	 * 
	 * @param modelDirectories
	 *            the paths to the files or directories that contains the models, that should be imported
	 * @throws IOException
	 *             if one of the given paths is invalid.
	 * @throws JSONException
	 *             if one of the given paths does not point to a valid model file.
	 * @throws JDOMException
	 *             if one of the given paths does not point to a valid model file.
	 */
	public void importModelsFrom(Collection<String> modelDirectories) throws IOException, JSONException, JDOMException;
}
