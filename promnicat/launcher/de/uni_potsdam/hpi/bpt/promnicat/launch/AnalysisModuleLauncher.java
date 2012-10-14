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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import com.jdotsoft.jarloader.JarClassLoader;

/**
 * Launcher class for analysis modules. If some errors occur, check for changes of 
 * interface for analysis modules.
 * 
 * @author Tobias Hoppe
 *
 */
public class AnalysisModuleLauncher {
	
	private final static Logger logger = Logger.getLogger(AnalysisModuleLauncher.class.getName());
	
	/**
	 * Starts the given analysis module. <br\>
	 * e.g. "example.Analysis" for de.uni_potsdam.hpi.bpt.promnicat.analysisModules.example.Analysis
	 * @param analysismoduleName The name must contain the complete path
	 * starting from analysis module package.
	 * @return the content of the result variable of the started analysis module
	 */
	public static Object execute(String analysismoduleName) {
		return execute(analysismoduleName, null);
	}

	/**
	 * Starts the given analysis module with the given parameters.<br\>
	 * e.g. "example.Analysis" for de.uni_potsdam.hpi.bpt.promnicat.analysisModules.example.Analysis
	 * @param analysisModuleName The name must contain the complete path
	 * starting from analysis module package.
	 * @param arguments parameters are passed on to the analysis module's main method
	 * @return the content of the result variable of the started analysis module
	 */
	public static Object execute(String analysisModuleName, String[] arguments) {
		JarClassLoader jcl = new JarClassLoader();
		try {
			Class<?> clazz = Class.forName("de.uni_potsdam.hpi.bpt.promnicat.analysisModules." + analysisModuleName, true, jcl);		
			//check that given class implements the correct interface
			boolean found = false;
			for(Class<?> c : clazz.getInterfaces()){
				if(c.getName().equals("de.uni_potsdam.hpi.bpt.promnicat.analysisModules.IAnalysisModule")){
					found = true;
					break;
				}
			}
			if(!found) {
				logger.severe("The given class does not implement the 'IAnalysisModule'-Interface. Execution aborted!!");
				return null;
			}
			//invoke execute-method of current analysis module
			Method m = clazz.getMethod("execute", String[].class);
			m.setAccessible(true);
			return m.invoke(clazz.newInstance(), (Object)arguments);			
		} catch (Throwable e) {
			logger.severe("Failed to run analysis module " + analysisModuleName + ". Got error message: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Starts the analysis module given as first parameter in args. The name must contain the
	 * complete path starting from analysis module package.<br\>
	 * e.g. "example.Analysis" for de.uni_potsdam.hpi.bpt.promnicat.analysisModules.example.Analysis
	 * @param args first parameter is the analysis module being started.
	 * Any further parameters are passed on to the analysis module's main method.
	 */
	public static void main(String[] args) {
		String moduleName = args[0];
		Collection<String> arguments = new ArrayList<String>();
		for (int i = 1; i < args.length; i++) {
			arguments.add(args[i]);
		}
		execute(moduleName, arguments.toArray(new String[0]));
	}
}
