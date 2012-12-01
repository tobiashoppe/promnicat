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
package de.uni_potsdam.hpi.bpt.promnicat.persistenceAPI.test.util;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IModel;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPojoFactory;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IRepresentation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IRevision;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.PojoFactory;
import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;
/**
 * @author Andrina Mascher, Tobias Hoppe
 *
 */
public class RepresentationFactory {

	private static final IPojoFactory fac = PojoFactory.INSTANCE;
	
	public static IRepresentation createLightweightRepresentation() {
		IModel model = fac.createModel("a title", Constants.ORIGIN_BPMAI);
		IRevision revision = fac.createRevision(0);
		revision.addMetadataAtKey("key1", "value1");
		revision.addMetadataAtKey("key1", "value1a");
		revision.addMetadataAtKey("key2", "value2");
		model.connectRevision(revision);
		model.connectLatestRevision(revision);
		
		IRepresentation representation = fac.createRepresentation(Constants.FORMAT_BPMAI_JSON, 
				Constants.NOTATION_EPC);
		revision.connectRepresentation(representation);
		
		return representation;
	}
	
	public static IRepresentation createRepresentationWithMultipleLinks() {
		IModel model = fac.createModel("model with multiple links", Constants.ORIGIN_BPMAI, "oneImportedId");
		IRevision revision = fac.createRevision(0);
		revision.addMetadataAtKey("k1", "v1");
		revision.addMetadataAtKey("k1", "v1a");
		revision.addMetadataAtKey("k2", "v2");
		revision.addMetadataAtKey("kX", "vY");
		model.connectRevision(revision);
		
		IRevision revision2 = fac.createRevision(1);
		model.connectRevision(revision2);
		model.connectLatestRevision(revision);
		
		IRepresentation representation = fac.createRepresentation(Constants.FORMAT_BPMAI_JSON, 
				Constants.NOTATION_BPMN2_0);
		IRepresentation representation2 = fac.createRepresentation(Constants.FORMAT_SVG, 
				Constants.NOTATION_BPMN2_0);
		IRepresentation representation3 = fac.createRepresentation(Constants.FORMAT_BPMAI_JSON, 
				Constants.NOTATION_BPMN1_1);
		revision.connectRepresentation(representation);
		revision.connectRepresentation(representation2);
		revision.connectRepresentation(representation3);
		
		return representation;
	}
	
	public static IRepresentation createUnconnectedRepresentation() {
		return fac.createRepresentation(Constants.FORMAT_SVG, Constants.NOTATION_BPMN2_0);
	}
}
