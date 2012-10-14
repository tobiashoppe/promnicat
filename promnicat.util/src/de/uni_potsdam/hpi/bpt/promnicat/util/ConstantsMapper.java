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

import java.util.logging.Logger;

/**
 * Class for handling slight variations of values regarding the constants, for example in BPM AI JSON.
 * @author Andrina Mascher, Cindy Fähnrich
 */
public class ConstantsMapper implements Constants{

	private final static Logger logger = Logger.getLogger(ConstantsMapper.class.getName());
	
	/**
	 * A given string representing a notation is mapped to a generic representation of this notation.
	 * Returns the given string, if no match is possible.
	 * 
	 * @param given A string that should be mapped
	 * @return the generic representation of this notation
	 */
	public static String mapNotation(String given) {

		//TODO add mapping for several strings like PetriNet, PN, Petri-Net to one notation.
		NOTATIONS[] notations = Constants.NOTATIONS.values();
		for(int i=0; i<notations.length; i++) {
			String notation = notations[i].toString();
			if(given.equalsIgnoreCase(notation)) {
				return notation;
			}
		}
		
		logger.info("Notation could not be mapped: " + given);
		return given;
	}

}
