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
package de.uni_potsdam.hpi.bpt.promnicat.persistenceApi;

import java.io.File;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.AbstractModel;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Revision;


/**
 * A {@link Representation} belongs to {@link Revision}, which again belongs to a {@link AbstractModel}
 * Each {@link Representation} can have sibling {@link Representation}s in other formats (XML, JSON)
 * or notations (EPC, BPMN). Each {@link Representation} also has dataContent and teh path to the original file.
 * 
 * 
 * @author Andrina Mascher, Tobias Hoppe
 *
 */
public interface IRepresentation extends IPojo {
	
	/**
	 * @return the language used to model, e.g. English
	 */
	public String getLanguage();

	/**
	 * @param language set the language used to model, e.g. English
	 */
	public void setLanguage(String language);
	
	/**
	 * @return the connected model via the connected revision
	 */
	public IModel getModel();

	/**
	 * 
	 * @return the connected {@link Revision}
	 */
	public IRevision getRevision();
	
	/**
	 * Connects this {@link Representation} to a {@link Revision} and vice versa
	 * 
	 * @param newRevision the revision to connect
	 */
	public void connectRevision(Revision newRevision);
	
	/**
	 * @return the title of the connected {@link AbstractModel}
	 */
	public String getTitle();

	/**
	 * @return the revision number from the connected {@link Revision}
	 */
	public Integer getRevisionNumber();

	/**
	 * @return true if the connected {@link Revision} is a latest revision
	 */
	public boolean belongsToLatestRevision();
	
	/**
	 * Import a file by saving the file content and the file path used.
	 * Assumes UTF8 content. If not, prepare and save byte[] directly.
	 * @param file the file to import
	 */
	public void importFile(File file);
	
	/**
	 * @return the data content as String instead of bytes
	 */
	public String convertDataContentToString();
	
	/**
	 * @return the data content written in the specified format
	 */
	public byte[] getDataContent();

	/**
	 * @param dataContent the dataContent to set
	 */
	public void setDataContent(byte[] dataContent);
	
	
	/**
	 * @return the original file path of the data content
	 */
	public String getOriginalFilePath();

	/**
	 * @param originalFilePath the originalFilePath to set
	 */
	public void setOriginalFilePath(String originalFilePath);
	
	/**
	 * @return true if dataContent is not empty
	 */
	public boolean hasDataContent();

	/**
	 * @return the format used for the data content, e.g. XML or JSON
	 */
	public String getFormat();

	/**
	 * @param format the format of the data content, e.g. XML or JSON
	 */
	public void setFormat(String format);
	
	/**
	 * 
	 * @return the notation language used to model, e.g. EPC or BPMN
	 */
	public String getNotation();

	/**
	 * 
	 * @param notation the modeling notation language to set, e.g. EPC or BPMN
	 */
	public void setNotation(String notation);
}
