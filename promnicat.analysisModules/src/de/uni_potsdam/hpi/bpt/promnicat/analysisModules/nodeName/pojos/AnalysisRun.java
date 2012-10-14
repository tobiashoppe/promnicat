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
package de.uni_potsdam.hpi.bpt.promnicat.analysisModules.nodeName.pojos;

import java.util.ArrayList;
import java.util.Collection;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.AbstractPojo;


/**
 * This class is used to group many instances of {@link LabelStorage}.
 * 
 * @author Andrina Mascher
 *
 */
public class AnalysisRun extends AbstractPojo{
	
	private Collection<AbstractPojo> storages = new ArrayList<AbstractPojo>();
	private String comment = null;
	
	public AnalysisRun() {
	}
	
	public AnalysisRun(String comment) {
		this.comment = comment;
	}
	
	public String toString() {
		return "AnalysisRun [dbId: " + getDbId() + ", comment: " + comment
				+ " #storages: " + storages.size()
				+ "]";
	}
	
	public String toStringExtended() {
		String s = "AnalysisRun [dbId: " + getDbId() + ", comment: " + comment
				+ " #storages: " + storages.size()
				+ "]";
		for(AbstractPojo storage : storages) {
			s += "\n\t" + storage.toString();
		}
		return s;
	}

	/**
	 * @return the storages
	 */
	public Collection<AbstractPojo> getStorages() {
		return storages;
	}

	/**
	 * @param storages the storages to set
	 */
	public void setStorages(Collection<AbstractPojo> storages) {
		this.storages = storages;
	}
	
	/**
	 * @param storage the storage to add
	 */
	public void addStorage(LabelStorage storage) {
		this.storages.add(storage);
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}	
}
