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
package de.uni_potsdam.hpi.bpt.promnicat.parser;

/**
 * Constants for id matching of a Json Diagram for Epc
 * @author Cindy Fähnrich
 *
 */
public class EpcConstants {

	/**
	 * Notation elements
	 */
	public static final String ENTITY_EVENT = "Event";
	public static final String ENTITY_FUNCTION = "Function";
	public static final String ENTITY_XORCONNECTOR = "XorConnector";
	public static final String ENTITY_ORCONNECTOR = "OrConnector";
	public static final String ENTITY_ANDCONNECTOR = "AndConnector";
	public static final String ENTITY_CONTROLFLOW = "ControlFlow";
	public static final String ENTITY_PROCESSINTERFACE = "ProcessInterface";
	public static final String ENTITY_ORGANIZATION = "Organization";
	public static final String ENTITY_ORGANIZATIONUNIT = "OrganizationUnit";
	public static final String ENTITY_POSITION = "Position";
	public static final String ENTITY_DATA = "Data";
	public static final String ENTITY_RELATION = "Relation";
	
	/**
	 * Property names in Json
	 */
	public static final String PROPERTY_INFOFLOW = "informationflow";
	public static final String PROPERTY_TITLE = "title";
	public static final String PROPERTY_DESCRIPTION = "description";
	public static final String PROPERTY_ENTRY = "entry";
	
	/**
	 * Property values in Json
	 */
	public static final String VALUE_FALSE = "False";
	public static final String VALUE_TRUEINVERSE = "TrueInverse";
	
}
