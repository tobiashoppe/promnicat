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

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.PojoFactory;

/**
 * Factory to create new {@link IRepresentation}, {@link IRevision}, and {@link IModel}
 * instances.
 * @author Tobias Hoppe
 *
 */
public interface IPojoFactory {
	
	IPojoFactory INSTANCE = PojoFactory.init();
	
	/**
	 * @return a new {@link IModel} instance.
	 */
	public IModel createModel();

	/**
	 * @param title
	 * @param origin
	 * @return a new {@link IModel} instance with the given arguments.
	 */
	public IModel createModel(String title, String origin);
	
	/**
	 * @param title
	 * @param origin
	 * @param id identifier used for model, not the internal used database id!
	 * @return a new {@link IModel} instance with the given arguments.
	 */
	public IModel createModel(String title, String origin, String id);
	
	/**
	 * @return a new {@link IRevision} instance.
	 */
	public IRevision createRevision();

	/**
	 * @param revNumber
	 * @return a new {@link IRevision} instance with the given revision number.
	 */
	public IRevision createRevision(Integer revNumber);
	
	/**
	 * @return a new {@link IRepresentation} instance.
	 */
	public IRepresentation createRepresentation();
	
	/**
	 * @param format
	 * @param notation
	 * @return a new {@link IRepresentation} instance with the given
	 * format and notation.
	 */
	public IRepresentation createRepresentation(String format, String notation);
	
	/**
	 * @param format
	 * @param notation
	 * @param dataFile
	 * @return a new {@link IRepresentation} instance with the given
	 * format and notation and saves the provided file content and file path
	 */
	public IRepresentation createRepresentation(String format, String notation, File dataFile);
	
	/**
	 * @param format
	 * @param notation
	 * @param dataContent
	 * @return a new {@link IRepresentation} instance with the given
	 * format and notation and saves the provided data content
	 */
	public IRepresentation createRepresentation(String format, String notation, byte[] dataContent);
}
