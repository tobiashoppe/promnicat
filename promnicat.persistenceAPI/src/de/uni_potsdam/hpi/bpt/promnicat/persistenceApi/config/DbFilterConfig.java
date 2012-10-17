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
package de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Logger;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Model;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Revision;
import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;

/**
 * This class is used to collect all filter criteria that will be used to 
 * find representations from the database.
 * 
 * See {@link NoSqlBuilder}:
 * For one attribute such as model titles, multiple criteria can be set and 
 * shall be executed in or-semantic by the database.
 * Search is done case sensitively.
 * Sometimes search allows substrings.
 * 
 * @author Andrina Mascher, Tobias Hoppe
 *
 */
public class DbFilterConfig {

	// filter for models
	private ArrayList<String> origins = new ArrayList<String>();
	private ArrayList<String> titles = new ArrayList<String>();
	private ArrayList<String> importedIds = new ArrayList<String>();
	private boolean latestRevisionsOnly = false;

	// filer for revisions
	private ArrayList<String> languages = new ArrayList<String>();
	private ArrayList<String> authors = new ArrayList<String>();
	private ArrayList<String> metadataKeys = new ArrayList<String>();
	private ArrayList<String> metadataValues = new ArrayList<String>();
	private HashMap<String, String> metadataEntries = new HashMap<String, String>();
	
	// filter for representations
	private ArrayList<String> notations = new ArrayList<String>();
	private ArrayList<String> formats = new ArrayList<String>();

	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(DbFilterConfig.class.getName());

	
	/**
	 * @param notation 
	 * 		filter {@link Representation} by the added notation, e.g BPMN,
	 * 		multiple criteria are treated with or-semantic,
	 * 		search is case-sensitive,
	 * 		substrings search is not allowed
	 */
	public void addNotation(Constants.NOTATIONS notation) {
		notations.add(notation.toString());
	}
	
	/**
	 * @param notation
	 * 	 	filter {@link Representation} by the added notation, e.g BPMN,
	 * 		multiple criteria are treated with or-semantic,
	 *		search is case-sensitive,
	 * 		substrings search is not allowed
	 */
	public void addNotation(String notation) {
		notations.add(notation);
	}

	/**
	 * @param notations
	 * 		filter {@link Representation} by the given notations, e.g BPMN,
	 * 		multiple criteria are treated with or-semantic,
	 *		search is case-sensitive,
	 * 		substrings search is not allowed
	 */
	public void addNotations(Collection<String> notations) {
		this.notations.addAll(notations);			
	}
	
	/**
	 * @param notations
	 * 		filter {@link Representation} by the given notations, e.g BPMN,
	 * 		multiple criteria are treated with or-semantic,
	 *		search is case-sensitive,
	 * 		substrings search is not allowed
	 */
	public void setNotations(Collection<String> notations) {
		this.notations.clear();
		this.notations.addAll(notations);
	}

	/**
	 * @return
	 * 		get the notations to filter {@link Representation}, e.g BPMN,
	 * 		multiple criteria are treated with or-semantic,
	 *		search is case-sensitive,
	 * 		substrings search is not allowed
	 */
	public ArrayList<String> getNotations() {
		return notations;
	}

	/**
	 * @param format
	 * 		filter {@link Representation} by the added format, e.g Json,
	 * 		multiple criteria are treated with or-semantic,
	 *		search is case-sensitive,
	 * 		substrings search is not allowed
	 */
	public void addFormat(Constants.FORMATS format) {
		formats.add(format.toString());
	}

	/**
	 * @param format
	 * 		filter {@link Representation} by the added format, e.g Json,
	 * 		multiple criteria are treated with or-semantic,
	 *		search is case-sensitive,
	 * 		substrings search is not allowed
	 */
	public void addFormat(String format) {
		formats.add(format.toString());
	}

	/**
	 * @param formats 
	 * 		filter {@link Representation} by the added formats, e.g Json,
	 * 		multiple criteria are treated with or-semantic,
	 *		search is case-sensitive,
	 * 		substrings search is not allowed
	 */
	public void addFormats(Collection<String> formats) {
		this.formats.addAll(formats);
	}

	/**
	 * @param formats 
	 * 		filter {@link Representation} by the given formats, e.g Json,
	 * 		multiple criteria are treated with or-semantic,
	 *		search is case-sensitive,
	 * 		substrings search is not allowed
	 */
	public void setFormats(Collection<String> formats) {
		this.formats.clear();
		this.formats.addAll(formats);
	}

	/**
	 * @return
	 * 		get the formats to filter {@link Representation}, e.g Json,
	 * 		multiple criteria are treated with or-semantic,
	 *		search is case-sensitive,
	 * 		substrings search is not allowed
	 */
	public ArrayList<String> getFormats() {
		return formats;
	}

	/**
	 * @param importedId 
	 * 		filter {@link Model} by the added importedId,
	 * 		multiple criteria are treated with or-semantic,
	 * 		the importedId is different to the databaseId,
	 *		search is case-sensitive,
	 * 		substrings search is not allowed
	 */
	public void addImportedId(String importedId) {
		this.importedIds.add(importedId);
	}
	
	/**
	 * @param importedId 
	 * 		filter {@link Model} by the added importedIds,
	 * 		multiple criteria are treated with or-semantic,
	 * 		the importedId is different to the databaseId,
	 *		search is case-sensitive,
	 * 		substrings search is not allowed
	 */
	public void addImportedIds(Collection<String> importedId) {
		this.importedIds.addAll(importedId);
	}
	
	/**
	 * @param importedId 
	 * 		filter {@link Model} by the given importedIds,
	 * 		multiple criteria are treated with or-semantic,
	 * 		the importedId is different to the databaseId,
	 *		search is case-sensitive,
	 * 		substrings search is not allowed
	 */
	public void setImportedIds(Collection<String> importedId) {
		this.importedIds.clear();
		this.importedIds.addAll(importedId);
	}

	/**
	 * @return
	 * 		get the importedIds to filter {@link Model}
	 * 		multiple criteria are treated with or-semantic,
	 * 		the importedId is different to the databaseId,
	 *		search is case-sensitive,
	 * 		substrings search is not allowed
	 */
	public ArrayList<String> getImportedIds() {
		return importedIds;
	}

	/**
	 * @param origin
	 * 		filter {@link Model} by the added origin,
	 * 		multiple criteria are treated with or-semantic,
	 *		search is case-sensitive,
	 * 		substrings search is not allowed
	 */
	public void addOrigin(Constants.ORIGINS origin) {
		origins.add(origin.toString());
	}

	/**
	 * @param origin
	 * 		filter {@link Model} by the added origin,
	 * 		multiple criteria are treated with or-semantic,
	 *		search is case-sensitive,
	 * 		substrings search is not allowed
	 */
	public void addOrigin(String origin) {
		origins.add(origin);
	}

	/**
	 * @param origins
	 * 		filter {@link Model} by the added origins,
	 * 		multiple criteria are treated with or-semantic,
	 *		search is case-sensitive,
	 * 		substrings search is not allowed
	 */
	public void addOrigins(Collection<String> origins) {
		this.origins.addAll(origins);
	}
	
	/**
	 * @param origins
	 * 		filter {@link Model} by the given origins,
	 * 		multiple criteria are treated with or-semantic,
	 *		search is case-sensitive,
	 * 		substrings search is not allowed
	 */
	public void setOrigins(Collection<String> origins) {
		this.origins.clear();
		this.origins.addAll(origins);
	}

	/**
	 * @return
	 * 		get the origins to filter {@link Model},
	 * 		multiple criteria are treated with or-semantic,
	 *		search is case-sensitive,
	 * 		substrings search is not allowed
	 */
	public ArrayList<String> getOrigins() {
		return origins;
	}

	/**
	 * @param title
	 * 		filter {@link Model} by the added title,
	 * 		multiple criteria are treated with or-semantic,
	 *		search is case-insensitive,
	 * 		substrings search is allowed
	 */
	public void addTitle(String title) {
		titles.add(title);
	}

	/**
	 * @param titles
	 * 		filter {@link Model} by the added titles,
	 * 		multiple criteria are treated with or-semantic,
	 *		search is case-insensitive,
	 * 		substrings search is allowed
	 */
	public void addTitles(Collection<String> titles) {
		this.titles.addAll(titles);
	}
	
	/**
	 * @param titles
	 * 		filter {@link Model} by the given titles,
	 * 		multiple criteria are treated with or-semantic,
	 *		search is case-insensitive,
	 * 		substrings search is allowed
	 */
	public void setTitles(Collection<String> titles) {
		this.titles.clear();
		this.titles.addAll(titles);
	}

	/**
	 * @return
	 * 		get the titles to filter {@link Model},
	 * 		multiple criteria are treated with or-semantic,
	 *		search is case-insensitive,
	 * 		substrings search is allowed
	 */
	public ArrayList<String> getTitles() {
		return titles;
	}

	/**
	 * If set to true, then only the latest {@link Revision}s or connected {@link Representation}s
	 * are retrieved from the database.
	 * 
	 * @return the latestRevisionsOnly
	 */
	public boolean latestRevisionsOnly() {
		return latestRevisionsOnly;
	}

	/**
	 * If set to true, then only the latest {@link Revision}s or connected {@link Representation}s
	 * are retrieved from the database.
	 * 
	 * @param latestRevisionsOnly the latestRevisionsOnly to set
	 */
	public void setLatestRevisionsOnly(boolean latestRevisionsOnly) {
		this.latestRevisionsOnly = latestRevisionsOnly;
	}

	/**
	 * @param language
	 * 		filter {@link Revision} by the added language,
	 * 		multiple criteria are treated with or-semantic,
	 *		search is case-insensitive,
	 * 		substrings search is allowed
	 */
	public void addLanguage(String language) {
		languages.add(language);
	}

	/**
	 * @param languages
	 * 		filter {@link Revision} by the added languages,
	 * 		multiple criteria are treated with or-semantic,
	 *		search is case-insensitive,
	 * 		substrings search is allowed
	 */
	public void addLanguages(Collection<String> languages) {
		this.languages.addAll(languages);
	}

	/**
	 * @param languages
	 * 		filter {@link Revision} by the given languages,
	 * 		multiple criteria are treated with or-semantic,
	 *		search is case-insensitive,
	 * 		substrings search is allowed
	 */
	public void setLanguages(Collection<String> languages) {
		this.languages.clear();
		this.languages.addAll(languages);
	}

	/**
	 * @return
	 * 		get the languages to filter {@link Revision},
	 * 		multiple criteria are treated with or-semantic,
	 *		search is case-insensitive,
	 * 		substrings search is allowed
	 */
	public ArrayList<String> getLanguages() {
		return languages;
	}

	/**
	 * @param authors
	 * 		filter {@link Revision} by the added author,
	 * 		multiple criteria are treated with or-semantic,
	 *		search is case-insensitive,
	 * 		substrings search is allowed
	 */
	public void addAuthors(Collection<String> authors) {
		this.authors.addAll(authors);
	}
	
	/**
	 * @param author
	 * 		filter {@link Revision} by the added author,
	 * 		multiple criteria are treated with or-semantic,
	 *		search is case-insensitive,
	 * 		substrings search is allowed
	 */
	public void addAuthor(String author) {
		this.authors.add(author);
	}

	/**
	 * @param authors
	 * 		filter {@link Revision} by the given authors,
	 * 		multiple criteria are treated with or-semantic,
	 *		search is case-insensitive,
	 * 		substrings search is allowed
	 */
	public void setAuthors(Collection<String> authors) {
		this.authors.clear();
		this.authors.addAll(authors);
	}

	/**
	 * @return
	 * 		get the authors to filter {@link Revision},
	 * 		multiple criteria are treated with or-semantic,
	 *		search is case-insensitive,
	 * 		substrings search is allowed
	 */
	public ArrayList<String> getAuthors() {
		return authors;
	}

	/**
	 * @param metadataKeys
	 * 		filter Metadata in {@link Revision} by the added attribute,
	 * 		multiple criteria are treated with or-semantic,
	 *		search is case-insensitive,
	 * 		substrings search is not allowed
	 */
	public void addMetadataKeys(Collection<String> metadataKeys) {
		this.metadataKeys.addAll(metadataKeys);
	}
	
	/**
	 * @param metadataKey
	 * 		filter Metadata in {@link Revision} by the added attribute,
	 * 		multiple criteria are treated with or-semantic,
	 *		search is case-insensitive,
	 * 		substrings search is not allowed
	 */
	public void addMetadataKey(String metadataKey) {
		this.metadataKeys.add(metadataKey);
	}

	/**
	 * @param metadataKeys
	 * 		filter Metadata in {@link Revision} by the given attribute,
	 * 		multiple criteria are treated with or-semantic,
	 *		search is case-insensitive,
	 * 		substrings search is not allowed
	 */
	public void setMetadataKeys(Collection<String> metadataKeys) {
		this.metadataKeys.clear();
		this.metadataKeys.addAll(metadataKeys);
	}

	/**
	 * @return
	 * 		get the metadataKeys to filter {@link Revision},
	 * 		multiple criteria are treated with or-semantic,
	 *		search is case-insensitive,
	 * 		substrings search is not allowed
	 */
	public ArrayList<String> getMetadataKeys() {
		return metadataKeys;
	}

	/**
	 * @param metadataValues
	 * 		filter Metadata in {@link Revision} by the added attribute,
	 * 		multiple criteria are treated with or-semantic,
	 *		search is case-insensitive,
	 * 		substrings search is allowed
	 */
	public void addMetadataValues(Collection<String> metadataValues) {
		this.metadataValues.addAll(metadataValues);
	}
	
	/**
	 * @param metadataValue
	 * 		filter Metadata in {@link Revision} by the added attribute,
	 * 		multiple criteria are treated with or-semantic,
	 *		search is case-insensitive,
	 * 		substrings search is allowed
	 */
	public void addMetadataValue(String metadataValue) {
		this.metadataValues.add(metadataValue);
	}

	/**
	 * @param metadataValues
	 * 		filter Metadata in {@link Revision} by the given attribute,
	 * 		multiple criteria are treated with or-semantic,
	 *		search is case-insensitive,
	 * 		substrings search is allowed
	 */
	public void setMetadataValues(Collection<String> metadataValues) {
		this.metadataValues.clear();
		this.metadataValues.addAll(metadataValues);
	}

	/**
	 * @return
	 * 		get the metadataValues to filter {@link Revision},
	 * 		multiple criteria are treated with or-semantic,
	 *		search is case-insensitive,
	 * 		substrings search is allowed
	 */
	public ArrayList<String> getMetadataValues() {
		return metadataValues;
	}

	/**
	 * @param metadataEntries
	 * 		filter Metadata in {@link Revision} by the given attribute,
	 * 		multiple criteria are treated with or-semantic,
	 *		search is case-insensitive,
	 * 		substrings search on values is allowed, but keys need to be exact
	 */
	public void addMetadataEntries(HashMap<String, String> metadataEntries) {
		this.metadataEntries = metadataEntries;
	}

	/**
	 * 		filter Metadata in {@link Revision} by the given attribute,
	 * 		multiple criteria are treated with or-semantic,
	 *		search is case-insensitive,
	 * 		substrings search on values is allowed, but keys need to be exact
	 * 
	 * @param key the metadata key to add
	 * @param value the metadata value to add
	 */
	public void addMetadataEntry(String key, String value) {
		this.metadataEntries.put(key, value);
	}

	/**
	 * @param metadataEntries
	 * 		filter Metadata in {@link Revision} by the given attribute,
	 * 		multiple criteria are treated with or-semantic,
	 *		search is case-insensitive,
	 * 		substrings search on values is allowed, but keys need to be exact
	 */
	public void setMetadataEntries(HashMap<String, String> metadataEntries) {
		this.metadataEntries = metadataEntries;
	}

	/**
	 * @return
	 * 		get the metadataEntries to filter {@link Revision},
	 * 		multiple criteria are treated with or-semantic,
	 *		search is case-insensitive,
	 * 		substrings search on values is allowed, but keys need to be exact
	 */
	public HashMap<String, String> getMetadataEntries() {
		return metadataEntries;
	}
	
	
}
