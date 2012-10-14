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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * A {@link Representation} belongs to {@link Revision}, which again belongs to a {@link Model}
 * Each {@link Representation} can have sibling {@link Representation}s in other formats (XML, JSON)
 * or notations (EPC, BPMN). Each {@link Representation} also has dataContent and teh path to the original file.
 * 
 * 
 * @author Andrina Mascher, Tobias Hoppe
 *
 */
public class Representation extends AbstractPojo{

	// the format such as XML, JSON used in the dataContent
	private String format = "";
	// the modeling notation such as EPC or BPMN
	private String notation = "";
	// the original file path that was used to import the data content
	private String originalFilePath = "";
	// the actual data content used for analysis
	private byte[] dataContent = new byte[0];
	// the connected revision
	Revision revision = null;
	//name of the used language in the model, e.g. English or German
	private String language = "";

	public Representation() {
	}
	
	public Representation(String format, String notation) {
		super();
		this.format = format;
		this.notation = notation;
	}
	
	public Representation(String format, String notation, File dataFile) {
		super();
		this.format = format;
		this.notation = notation;
		importFile(dataFile);
	}
	
	public Representation(String format, String notation, byte[] dataContent) {
		super();
		this.format = format;
		this.notation = notation;
		this.setOriginalFilePath("");
		this.dataContent = dataContent;
	}

	@Override
	public String toString() {
		return "Representation [dbId=" + dbId 
								+ ", format=" + format
								+ ", notation=" + notation
								+ ", dataLength="+ dataContent.length
								+ ", language=" + language
								+ ", model=" + getTitle() + "(Revision " + getRevisionNumber() + ")"
								+ ", belongsToLatestRevision=" + belongsToLatestRevision()
								+ "]";
	}
	
	/**
	 * @return the language used to model, e.g. English
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @param language set the language used to model, e.g. English
	 */
	public void setLanguage(String language) {
		this.language = language;
	}
	
	/**
	 * @return the connected model via the connected revision
	 */
	public Model getModel() {
		if(revision == null) {
			return null;
		} else {
			return revision.getModel();
		}
	}

	/**
	 * 
	 * @return the connected {@link Revision}
	 */
	public Revision getRevision() {
		return revision;
	}
	
	/**
	 * Connects this {@link Representation} to a {@link Revision} and vice versa
	 * 
	 * @param newRevision the revision to connect
	 */
	public void connectRevision(Revision newRevision) {
		if(newRevision != null) {
			//defer responsibility
			newRevision.connectRepresentation(this);
		}
	}
	
	/**
	 * @return the title of the connected {@link Model}
	 */
	public String getTitle() {
		if(revision == null) {
			return null;
		}
		return revision.getTitle();
	}

	/**
	 * @return the revision number from the connected {@link Revision}
	 */
	public Integer getRevisionNumber() {
		if(revision == null) {
			return null;
		}
		return revision.getRevisionNumber();
	}

	/**
	 * @return true if the connected {@link Revision} is a latest revision
	 */
	public boolean belongsToLatestRevision() {
		if(revision != null) {
			return revision.isLatestRevision();
		}
		return false;
	}
	
	/**
	 * Import a file by saving the file content and the file path used.
	 * Assumes UTF8 content. If not, prepare and save byte[] directly.
	 * @param file the file to import
	 */
	public void importFile(File file) {
		this.setOriginalFilePath(file.getAbsolutePath());
		this.importDataContent(file);
	}
	
	/**
	 * @param file the file to read the content from
	 */
	private void importDataContent(File file) {
		try {
			FileInputStream in = new FileInputStream(file);
			byte[] b = new byte[(int) file.length()];
			in.read(b);
			this.dataContent = b;
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return the data content as String instead of bytes
	 */
	public String convertDataContentToString() {
		return new String(dataContent);
	}
	
	/**
	 * @return the data content written in the specified format
	 */
	public byte[] getDataContent() {
		return dataContent;
	}

	/**
	 * @param dataContent the dataContent to set
	 */
	public void setDataContent(byte[] dataContent) {
		this.dataContent = dataContent;
	}
	
	
	/**
	 * @return the original file path of the data content
	 */
	public String getOriginalFilePath() {
		return originalFilePath;
	}

	/**
	 * @param originalFilePath the originalFilePath to set
	 */
	public void setOriginalFilePath(String originalFilePath) {
		this.originalFilePath = originalFilePath;
	}

	/**
	 * @return true if dataContent is not empty
	 */
	public boolean hasDataContent() {
		return dataContent.length > 0;
	}

	/**
	 * @return the format used for the data content, e.g. XML or JSON
	 */
	public String getFormat() {
		getModel();
		return format;
	}

	/**
	 * @param format the format of the data content, e.g. XML or JSON
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * 
	 * @return the notation language used to model, e.g. EPC or BPMN
	 */
	public String getNotation() {
		return notation;
	}

	/**
	 * 
	 * @param notation the modeling notation language to set, e.g. EPC or BPMN
	 */
	public void setNotation(String notation) {
		this.notation = notation;
	}
}
