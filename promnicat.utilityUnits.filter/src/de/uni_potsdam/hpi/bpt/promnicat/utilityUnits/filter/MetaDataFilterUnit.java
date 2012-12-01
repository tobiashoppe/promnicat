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
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.filter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Revision;
import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.AbstractUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitDataMetaData;

/**
 * This class filters out metadata from a {@link Revision}.
 * 
 * The expected input type is {@link IUnitData}<{@link Representation}>.
 * The output type is {@link IUnitData}<{@link Map}<{@link String}, {@link Collection}<{@link String}>>>.
 * 
 * @author Tobias Hoppe
 *
 */
public class MetaDataFilterUnit extends AbstractUnit implements IUnit<IUnitData<Object>, IUnitData<Object> > {
	
	private String keySearchCriterion = null;
	private String valueSearchCriterion = null;
	private Pattern keySearchPattern = null;
	private Pattern valueSearchPattern = null;
	
	/**
	 * Create a new {@link MetaDataFilterUnit} with the given metadata key and metadata value to search for.
	 * If all values for a key or a value should be considered, just enter <code>null</code> as key or value.
	 * @param key the metadata key to search for
	 * @param value the metadata value to search for
	 */
	public MetaDataFilterUnit(String key, String value) {
		this.keySearchCriterion = key;
		this.valueSearchCriterion = value;
	}
	
	/**
	 * Create a new {@link MetaDataFilterUnit} with the given metadata key and metadata value to search for.
	 * If all values for a key or a value should be considered, just enter <code>null</code> as key or value.
	 * @param keyPattern the metadata key pattern to search for
	 * @param valuePattern the metadata value pattern to search for
	 */
	public MetaDataFilterUnit(Pattern keyPattern, Pattern valuePattern) {
		this.keySearchPattern = keyPattern;
		this.valueSearchPattern = valuePattern;
	}
	
	@Override
	public IUnitData<Object> execute(IUnitData<Object> input) throws IllegalTypeException {
		if (input == null) {
			throw new IllegalArgumentException("Got an invalid null pointer input!");
		}
		if (!(input.getValue() instanceof Representation)){
			throw new IllegalTypeException(Representation.class, input.getValue().getClass(), "Got wrong input type in " + this.getName());
		}
		
		Map<String, Collection<String>> result = new HashMap<String, Collection<String>>();
		Map<String, Collection<String>> metaData = ((Representation)input.getValue()).getRevision().getMetadata();
		if (this.keySearchCriterion == null && this.valueSearchCriterion == null){
			//handle searched pattern
			if(this.keySearchPattern != null){
				//search for given key
				for (String key : metaData.keySet()){
					if (this.keySearchPattern.matcher(key).matches()){
						result.put(key, metaData.get(key));
					}
				}				
			} else if(this.valueSearchPattern != null){
				//search for given value
				for (String key : metaData.keySet()){
					Collection<String> values = metaData.get(key);
					boolean found = false;
					for(String value : values){
						if(this.valueSearchPattern.matcher(value).matches()){
							found = true;
							break;
						}
					}
					if (found){
						result.put(key, values);
					}
				}
			} else {
				for (String key : metaData.keySet()){
					result.put(key, metaData.get(key));
				}
			}
		} else {
			//handle searched string
			if(this.keySearchCriterion != null){
				//search for given key
				for (String key : metaData.keySet()){
					if (key.equalsIgnoreCase(this.keySearchCriterion)){
						result.put(key, metaData.get(key));
					}
				}				
			} else if(this.valueSearchCriterion != null){
				//search for given value
				for (String key : metaData.keySet()){
					Collection<String> values = metaData.get(key);
					boolean found = false;
					for(String value : values){
						if(value.equalsIgnoreCase(this.valueSearchCriterion)){
							found = true;
							break;
						}
					}
					if (found){
						result.put(key, values);
					}
				}
			} else {
				for (String key : metaData.keySet()){
					result.put(key, metaData.get(key));
				}
			}
		}
		//set result value(s)
		input.setValue(result);
		if(input instanceof IUnitDataMetaData<?>){
			((IUnitDataMetaData<?>) input).setMetaData(result);
		}		
		return input;
	}

	@Override
	public String getName() {
		return "MetaDataFilterUnit";
	}

	@Override
	public Class<?> getInputType() {
		return Representation.class;
	}

	@Override
	public Class<?> getOutputType() {
		return Map.class;
	}

}
