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
package de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IModel;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IRepresentation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IRevision;



/**
 * A {@link Representation} belongs to {@link Revision}, which again belongs to a {@link AbstractModel}
 * Each {@link Representation} can have sibling {@link Representation}s in other formats (XML, JSON)
 * or notations (EPC, BPMN). Each {@link Representation} also has dataContent and teh path to the original file.
 * 
 * 
 * @author Andrina Mascher, Tobias Hoppe
 *
 */
public abstract class AbstractRepresentation implements IRepresentation {

	// the format such as XML, JSON used in the dataContent
	protected String format = "";
	// the modeling notation such as EPC or BPMN
	protected String notation = "";
	// the original file path that was used to import the data content
	protected String originalFilePath = "";
	// the actual data content used for analysis
	protected byte[] dataContent = new byte[0];
	// the connected revision
	protected IRevision revision = null;
	//name of the used language in the model, e.g. English or German
	protected String language = "";

	protected AbstractRepresentation() {
	}
	
	protected AbstractRepresentation(String format, String notation) {
		super();
		this.format = format;
		this.notation = notation;
	}
	
	protected AbstractRepresentation(String format, String notation, File dataFile) {
		this(format, notation);
		importFile(dataFile);
	}
	
	protected AbstractRepresentation(String format, String notation, byte[] dataContent) {
		this(format, notation);
		this.setOriginalFilePath("");
		this.dataContent = dataContent;
	}

	@Override
	public String toString() {
		return "Representation [dbId=" + getDbId() 
								+ ", format=" + format
								+ ", notation=" + notation
								+ ", dataLength="+ dataContent.length
								+ ", language=" + language
								+ ", model=" + getTitle() + "(Revision " + getRevisionNumber() + ")"
								+ ", belongsToLatestRevision=" + belongsToLatestRevision()
								+ "]";
	}
	
	@Override
	public String getLanguage() {
		return language;
	}

	@Override
	public void setLanguage(String language) {
		this.language = language;
	}
	
	@Override
	public IModel getModel() {
		if(revision == null) {
			return null;
		} else {
			return revision.getModel();
		}
	}

	@Override
	public IRevision getRevision() {
		return revision;
	}
	
	@Override
	public void connectRevision(Revision newRevision) {
		if(newRevision != null) {
			//defer responsibility
			newRevision.connectRepresentation(this);
		}
	}
	
	@Override
	public String getTitle() {
		if(revision == null) {
			return null;
		}
		return revision.getTitle();
	}

	@Override
	public Integer getRevisionNumber() {
		if(revision == null) {
			return null;
		}
		return revision.getRevisionNumber();
	}

	@Override
	public boolean belongsToLatestRevision() {
		if(revision != null) {
			return revision.isLatestRevision();
		}
		return false;
	}
	
	@Override
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
	
	@Override
	public String convertDataContentToString() {
		return new String(dataContent);
	}
	
	@Override
	public byte[] getDataContent() {
		return dataContent;
	}

	@Override
	public void setDataContent(byte[] dataContent) {
		this.dataContent = dataContent;
	}
	
	
	@Override
	public String getOriginalFilePath() {
		return originalFilePath;
	}

	@Override
	public void setOriginalFilePath(String originalFilePath) {
		this.originalFilePath = originalFilePath;
	}

	@Override
	public boolean hasDataContent() {
		return dataContent.length > 0;
	}

	@Override
	public String getFormat() {
		getModel();
		return format;
	}

	@Override
	public void setFormat(String format) {
		this.format = format;
	}

	@Override
	public String getNotation() {
		return notation;
	}

	@Override
	public void setNotation(String notation) {
		this.notation = notation;
	}
	
	void setRevision(IRevision revToSet) {
		revision = revToSet;
	}
}
