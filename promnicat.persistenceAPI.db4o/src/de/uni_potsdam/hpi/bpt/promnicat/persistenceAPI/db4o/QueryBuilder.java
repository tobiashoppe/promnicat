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
package de.uni_potsdam.hpi.bpt.promnicat.persistenceAPI.db4o;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observer;
import java.util.Set;

import com.db4o.query.Predicate;
import com.db4o.query.Query;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IModel;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IRepresentation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IRevision;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.config.DbFilterConfig;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Representation;


/**
 * This class is used by {@link PersistenceApiDb4o} to generate queries from
 * {@link DbFilterConfig}.
 * 
 * @author Tobias Hoppe
 *
 */
public class QueryBuilder {

	/**
	 * Builds a query that returns instances of {@link Representation}.
	 * @param config to define which elements should be selected
	 * @return a {@link Query} matching the parameters given by the {@link DbFilterConfig}
	 */
	public static Predicate<IRepresentation> build(final DbFilterConfig config) {
		return build(config, null);
	}
	
	/**
	 * @param config to define which elements should be selected
	 * @param resultHandler class to inform about matching {@link Representation}s.
	 * If the result should be returned at the end of the query enter
	 * <code>null</code> as result handler.
	 * @return a query that returns instances of {@link Representation}.
	 */
	public static Predicate<IRepresentation> build(final DbFilterConfig config, final Observer resultHandler) {
		return new Predicate<IRepresentation>() {
			private static final long serialVersionUID = -4547847598341430548L;

			public boolean match(IRepresentation rep) {
				boolean match = false;
				if(config.getFormats().isEmpty()) {
					match = true;
				} else {
					match = config.getFormats().contains(rep.getFormat());
				}
				if(config.latestRevisionsOnly()) {
					match = match && rep.belongsToLatestRevision();
				}
				if(!config.getNotations().isEmpty()) {
					match = match && config.getNotations().contains(rep.getNotation());
				}
				if(!config.getLanguages().isEmpty()) {
					match = match && config.getLanguages().contains(rep.getLanguage());
				}
				IRevision rev = rep.getRevision();
				//add rep's revision constraints
				if(rev != null) {
					if(!config.getTitles().isEmpty()) {
						boolean titleMatch = false;
						String revTitle = rev.getTitle();
						for(String title : config.getTitles()) {
							if(title.contains(revTitle)) {
								titleMatch = true;
								break;
							}
						}					
						match = match && titleMatch;
					}
					if(!config.getMetadataEntries().isEmpty()) {
						boolean metaDataMatch = false;
						Map<String, Collection<String>> metaData = rev.getMetadata();
						for(Entry<String, String> entry : config.getMetadataEntries().entrySet()) {
							if(metaData.containsKey(entry.getKey())) {
								Collection<String> element = metaData.get(entry.getKey());
								if(metaData.get(entry.getKey()).containsAll(element)) {
									metaDataMatch = true;
									break;
								}
							}
						}	
						match = match && metaDataMatch;
					}
					if(!config.getMetadataKeys().isEmpty()) {
						boolean metaDataMatch = false;
						Set<String> metaDataKeys = rev.getMetadata().keySet();
						if(metaDataKeys.containsAll(config.getMetadataKeys())) {
							metaDataMatch = true;
						}
						match = match && metaDataMatch;
					}
					if(!config.getMetadataValues().isEmpty()) {
						boolean metaDataMatch = false;
						Collection<String> allValues = new ArrayList<String>();
						for(Collection<String> entry : rev.getMetadata().values()) {
							allValues.addAll(entry);
						}					
						if(allValues.containsAll(config.getMetadataValues())) {
							metaDataMatch = true;
						}
						match = match && metaDataMatch;
					}
					if(!config.getAuthors().isEmpty()) {
						match = match && config.getAuthors().contains(rev.getAuthor());
					}
				}
				IModel model = rep.getModel();
				//add rep's model constraints
				if(model != null) {
					if(!config.getImportedIds().isEmpty()) {
						match = match && config.getImportedIds().contains(model.getImportedId());
					}				
					if(!config.getOrigins().isEmpty()) {
						match = match && config.getOrigins().contains(model.getOrigin());
					}
					if(!config.getTitles().isEmpty()) {
						match = match && config.getTitles().contains(model.getTitle());
					}					
				}
				if(match) {
					if(resultHandler != null) {
						resultHandler.update(null, rep);
					} else {
						return true;
					}
				}
				return false;
			}
		};
	}
}
