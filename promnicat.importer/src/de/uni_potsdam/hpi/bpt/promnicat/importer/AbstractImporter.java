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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.jdom.JDOMException;
import org.json.JSONException;

/**
 * This is an abstract base class for all {@link IImporter} implementations.
 * 
 * @author Tobias Hoppe
 * 
 */
public abstract class AbstractImporter implements IImporter {

	@Override
	public void importModelsFrom(Collection<String> modelDirectories) throws IOException, JSONException, JDOMException {
		for (String uri : modelDirectories) {
			importModelsFrom(uri);
		}
	}

	/**
	 * Checks whether the given path exists in the file system.
	 * If checkIsDir is set to <code>true</code> the given path have to represent a directory.
	 * 
	 * @param modelPath the path to the file to check
	 * @param checkIsDir check, that the given path have to be a directory. If the check should not be performed,
	 *  set to <code>false</code>.
	 * @return the {@link File} of the given path.
	 * @throws FileNotFoundException if the given path does not exists and if enabled the given path is not a directory.
	 */
	protected File checkModelPath(String modelPath, boolean checkIsDir) throws FileNotFoundException {
		File rootDir = new File(modelPath);

		if (!rootDir.exists()) {
			throw new FileNotFoundException("given path " + modelPath + " does not exist on file system.");
		}
		
		if (checkIsDir && !rootDir.isDirectory()) {
			throw new FileNotFoundException("given path " + modelPath + " is not a directory.");
		}

		return rootDir;
	}
	
	/**
	 * Collects recursively all files in the given directory and all of it's sub-directories.
	 * 
	 * @param rootDir the directory to start with
	 * @return a {@link Collection} of all found files.
	 */
	protected Collection<File> getFilesRecursivelyFromDir(File rootDir) {
		Collection<File> files = new ArrayList<File>();
		for (File file : rootDir.listFiles()) {
			if (file.isDirectory()) {
				files.addAll(getFilesRecursivelyFromDir(file));
			} else {
				files.add(file);
			}
		}
		return files;
	}
	
	/**
	 * Deletes the given file or directory and all sub-directories.
	 * @param path to delete
	 * @return <code>true</code> if deletion was successful, <code>false</code> otherwise
	 */
	protected boolean deleteDirectory(File path) {
		if( path.exists() ) {
			File[] files = path.listFiles();
			for(int i=0; i<files.length; i++) {
				if(files[i].isDirectory()) {
					deleteDirectory(files[i]);
				}
				else {
					files[i].delete();
				}
			}
		}
		return( path.delete() );
	}
}
