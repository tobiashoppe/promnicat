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
package de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.orientdbObj.pojos;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Id;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPojo;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.AbstractPojo;

/**
 * This class is used to group many instances of {@link LabelStorage}.
 * 
 * @author Andrina Mascher
 *
 */
public class AnalysisRun extends AbstractPojo {
	
	/**
	 * the id used by OrientDb, can not use the id defined in {@link AbstractPojo}
	 * due to OrientDb's injections.
	 */
	@Id 
	private String dbId;
	
	private Collection<IPojo> storages = new ArrayList<IPojo>();
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
		for(IPojo storage : storages) {
			s += "\n\t" + storage.toString();
		}
		return s;
	}

	/**
	 * @return the storages
	 */
	public Collection<IPojo> getStorages() {
		return storages;
	}

	/**
	 * @param storages the storages to set
	 */
	public void setStorages(Collection<IPojo> storages) {
		this.storages = storages;
	}
	
	/**
	 * @param storage the storage to add
	 */
	public void addStorage(LabelStorage storage) {
		this.storages.add((IPojo) storage);
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
	
	@Override
	public String getDbId() {
		return this.dbId;
	}

	@Override
	public boolean hasDbId() {
		return this.dbId != null;
	}
}
