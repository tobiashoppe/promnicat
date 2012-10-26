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

import java.util.HashSet;
import java.util.Set;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IModel;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPersistenceApi;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IRepresentation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IRevision;

/**
 * A model represents a collection of {@link Revision} that again store a collection of {@link Representation}.
 * All of them share the same title and origin. 
 * For performance reasons, sometimes not all revisions are loaded from the database. For this case 
 * the nrOfRevisionsInDb tell the total number of revisions found in the database.
 * 
 * @author Andrina Mascher, Tobias Hoppe
 *
 */
public class Model extends AbstractPojo implements IModel {
	
	/**
	 * id used for identification of {@link Model} 
	 * is independent of database id, needs to be managed by user
	 */
	protected String importedId = "";
	
	/**
	 * the title of the process model as given by the user
	 */
	protected String title = "";
	
	/**
	 *  the original process model collection's name
	 */
	protected String origin = "";
	
	/**
	 *  group of connected revisions
	 */
	protected Set<IRevision> revisions = new HashSet<IRevision>();
	
	/**
	 *  indicates whether all revisions and all their representations are loaded from the database, or just 1 of them.
	 */
	protected boolean completelyLoaded; 

	protected Model() {
		super();
		completelyLoaded = true;
	}
	
	protected Model(String title, String origin) {
		this();
		this.title = title;
		this.origin = origin;
	}

	protected Model(String title, String origin, String id) {
		this(title, origin);
		this.importedId = id;
	}

	@Override
	public String toString() {
		return "Model [dbId=" + getDbId() 
						+ ", id=" + getImportedId()
						+ ", title=" + getTitle()
						+ ", origin=" + getOrigin()
						+ ", #revisions=" + getNrOfRevisions()
						+ ", #representations=" + getNrOfRepresentations()
						+ ", completelyLoaded=" + getCompletelyLoaded()
						+ "]";
	}
	
	@Override
	public String toStringExtended() {
		String s = toString();
		try {
			for(IRevision rev : getRevisions()) {
				s +="\n\t" + rev.toString();
				for(IRepresentation rep : rev.getRepresentations()) {
					s +="\n\t\t" + rep.toString();
				}
			}
		} catch(ClassCastException e) {
			s += "\nModel is not fully loaded from database, call loadCompleteModel()";
		}
		return s;
	}
	
	@Override
	public Model loadCompleteModel(IPersistenceApi papi) {
		if(!hasDbId()) {
			return this;
		}
		IModel newM = papi.loadCompleteModelWithDbId(getDbId());
		this.title = newM.getTitle();
		this.origin = newM.getOrigin();
		setRevisions(newM.getRevisions());
		setCompletelyLoaded(true);
		return this;
	}

	@Override
	public int getNrOfRepresentations() {
		int i = 0;
		for(IRevision r : revisions) {
			i += r.getNrOfRepresentations();
		}
		return i;
	}
	
	@Override
	public int getNrOfRevisions() {
		return revisions.size();
	}

	@Override
	public Set<IRevision> getRevisions() {
		return revisions;
	}

	@Override
	public void setRevisions(Set<IRevision> revisions) {
		this.revisions.clear();
		if(revisions == null) {
			return;
		}
		for(IRevision rev : revisions) {
			connectRevision(rev);
		}
	}
	
	@Override
	public void connectRevision(IRevision revision) {
		if(revision != null) {
			((Revision) revision).setModel(this);
			this.revisions.add(revision);
		}
	}
	
	@Override
	public void connectLatestRevision(IRevision revision) {
		if(revision != null) {
			connectRevision(revision);
			setLatestRevision(revision);
		}
	}
	
	/**
	 * Sets this revision as latest revision. The previous one is unset.
	 * 
	 * @param revision
	 */
	protected void setLatestRevision(IRevision revision) {
		if(getLatestRevision() != null) {
			getLatestRevision().setLatestRevision(false);
		}
		revision.setLatestRevision(true);
	}
	
	@Override
	public IRevision getLatestRevision() {
		for(IRevision r : getRevisions()) {
			if(r.isLatestRevision()) {
				return r;
			}
		}
		return null;
	}

	@Override
	public String getImportedId() {
		return importedId;
	}

	@Override
	public void setImportedId(String id) {
		this.importedId = id;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String getOrigin() {
		return origin;
	}

	@Override
	public void setOrigin(String origin) {
		this.origin = origin;
	}

	@Override
	public boolean getCompletelyLoaded() {
		return completelyLoaded;
	}	
	
	@Override
	public void setCompletelyLoaded(boolean completelyLoaded) {
		this.completelyLoaded = completelyLoaded;
	}
}
