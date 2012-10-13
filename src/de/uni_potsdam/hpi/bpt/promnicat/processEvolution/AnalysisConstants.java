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
package de.uni_potsdam.hpi.bpt.promnicat.processEvolution;

/**
 * A set of string constants used throughout the analyses.
 * 
 * @author Tobias Metzke
 *
 */
public enum AnalysisConstants {

	/**
	 * the key for the number of models that are analyzed
	 */
	NUM_MODELS ("number of models"),

	/**
	 * the key for the number of models that grow continuously
	 */
	NUM_GROWING ("continuously growing"),

	/**
	 * the key for the number of models that do not grow continuously
	 */
	NUM_NOT_GROWING ("not always growing"),
	
	UNALTERING_REVISIONS ("unaltering Revisions"),

	ALTERING_REVISIONS ("altering revisions"),

	NUM_REVISIONS ("number of revisions"),

	LOWER ("lower"),

	SAME ("same"),

	HIGHER ("higher"), 
	
	ADD_DELETE ("add_delete"), 
	
	ACTIVITIES ("activities"),
	
	EDGES ("edges"),
	
	GATEWAYS ("gateways"),
	
	ROLES ("roles"), 
	
	DOCUMENTS ("documents"),
	
	ADDITIONS ("additions"),
	
	DELETIONS ("deletions"), 
	
	EVENTS ("events"), 
	
	CONTROL_FLOW ("control flow"),
	
	ORGANISATION ("organisation"),
	
	DATA_FLOW ("data"),
	
	CONTROL_ORGA ("control flow > organisation"),
	
	CONTROL_DATA ("control flow > data"),
	
	DATA_CONTROL ("data > control flow"),
	
	DATA_ORGA ("data > organisation"),
	
	ORGA_CONTROL ("organisation > control flow"),
	
	ORGA_DATA ("organisation > data"),
	
	CONTROL_ORGA_DATA ("control flow > organisation > data"),
	
	CONTROL_DATA_ORGA ("control flow > data > organisation"),
	
	DATA_CONTROL_ORGA ("data > control flow > organisation"),
	
	DATA_ORGA_CONTROL ("data > organisation > control flow"),
	
	ORGA_CONTROL_DATA ("organisation > control flow > data"),
	
	ORGA_DATA_CONTROL ("organisation > data > control flow"),
	
	NONE ("none"),
	
	ITEMSEPARATOR (";"), 
	
	NEW_LAYOUT ("newLayout"), 
	
	MODELING ("modeling"),
	
	RECONCILIATION ("reconciliation");
	
	private String stringRepresentation;
	
	private AnalysisConstants(String name) {
		this.stringRepresentation = name;
	}
	
	public String getDescription() {
		return this.stringRepresentation;
	}
}
