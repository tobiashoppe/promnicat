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
package de.uni_potsdam.hpi.bpt.promnicat.importer.npb;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import de.uni_potsdam.hpi.bpt.promnicat.importer.AbstractImporter;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IModel;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPersistenceApi;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IRepresentation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IRevision;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Model;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Revision;
import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;

/**
 * This class is used to import models from the 'National Process Library'.
 * 
 * @author Tobias Hoppe
 * 
 */
public class NPBImporter extends AbstractImporter {

	private IPersistenceApi persistenceApi = null;
	
	private Logger logger = Logger.getLogger(NPBImporter.class.getName());
	
	private int createdModelsCount = 0;
	private int createdRevisionsCount = 0;
	private int createdRepresentationsCount = 0;

	//XML keys
	private static final String KEY_REVISION = "revision";
	private static final String KEY_PROCESS_NAME = "processName";
	private static final String KEY_PROCESS_AUTHOR = "processAuthor";
	private static final String KEY_MODELING_METHOD = "modelingMethod";
	private static final String KEY_SOURCE_FILE = "sourceFile";
	private static final String KEY_IMAGE_FILE = "imageFile";
	private static final String KEY_NAME = "name";
	private static final String KEY_LISTE = "liste";
	private static final String KEY_META_INFORMATION = "metaInformation";
	private static final String KEY_BPMN = "BPMN";
	private static final String KEY_EPK_OR_eEPK = "EPK / eEPK";
	private static final String KEY_WERT = "wert";
	private static final String KEY_CODE = "code";
	private static final String KEY_SCHLUESSEL = "schluessel";
	private static final String KEY_EXPORT = "export";
	
	/**
	 * Creates a new {@link NPBImporter} with the given {@link IPersistenceApi} used for database access.
	 * @param persistenceApi persistence API used by importer
	 */
	public NPBImporter(IPersistenceApi persistenceApi) {
		this.persistenceApi = persistenceApi;
	}
	
	@Override
	public void importModelsFrom(String modelDirectory) throws IOException, JDOMException {
		File rootFile = super.checkModelPath(modelDirectory, false);
		
		//reset counter
		this.createdModelsCount = 0;
		this.createdRepresentationsCount = 0;
		this.createdRevisionsCount = 0;
		
		//walk through all files and import them
		if (rootFile.isDirectory()){
			for (File file : getFilesRecursivelyFromDir(rootFile)) {
				importFile(file);
			}
		} else {
			importFile(rootFile);
		}
		this.persistenceApi.closeDb();
		logger.info("Finished import or update of " + this.createdModelsCount + " models," +
				" and created " + this.createdRevisionsCount + " revisions and " + this.createdRepresentationsCount + " representations.");
	}

	/**
	 * Parses the given XML-file and creates a new {@link Model} including {@link Revision}s and {@link Representation}s
	 * if no {@link Model} exists for the process model. Otherwise, a new {@link Revision} is added, if the revision attribute
	 * has been changed since last import. In case the revision number is still the same nothing is done.
	 * 
	 * @param modelFile file containing the XML to parse and to import into database
	 * @throws JDOMException if XML parsing is erroneous
	 * @throws IOException if the given path could not be found or read
	 */
	private void importFile(File modelFile) throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(modelFile);
		Element rootElement = doc.getRootElement();
		//only parse expected file format schema
		if (Constants.NPB_XML_NAMESPACE.equals(rootElement.getNamespace().getURI())) {
			//if file with more than one model is given, parse them one after the other
			if (KEY_EXPORT.equals(rootElement.getName())){
				for (Object modelElement: rootElement.getChildren()){
					parseModel((Element) modelElement, modelFile);
				}
			} else {
				parseModel(rootElement, modelFile);
			}
		} else {
			logger.info("Tried to import model with unknown file format. This file has been skipped: " + modelFile.getPath());			
		}
	}

	/**
	 * Traverses the XML-file beginning from the given root element and extracts all meta data information.
	 * Afterwards, a model with the corresponding revision and it's representations is created from the parsed data,
	 * if it is not already present in the database.
	 * 
	 * @param rootElement the root element of the model in the XML-file
	 * @param file the current {@link File} to handle
	 */
	private void parseModel(Element rootElement, File file) {
		String modelId = rootElement.getAttributeValue(KEY_SCHLUESSEL);
		if (modelId == null){
			//model id could not be found, just skip the model
			logger.info("Tried to import model without identifier. This file has been skipped: " + file.getPath());
			return;
		}
		IModel model = this.persistenceApi.loadCompleteModelWithImportedId(modelId);
		if (model == null){
			//create new model
			Map<String, Collection<String>> metaData = parseMetaData(rootElement);
			model = this.persistenceApi.getPojoFactory().createModel(metaData.get(KEY_PROCESS_NAME).iterator().next(), Constants.ORIGIN_NPB, modelId);
			this.createdModelsCount++;
			IRevision revision = createRevisionAndRepresentations(metaData, getRevisionNumber(rootElement));
			revision.connectModel(model);			
		} else {
			int revisionNumber = getRevisionNumber(rootElement);
			//check for new revision
			for (IRevision revision : model.getRevisions()){
				if (revision.getRevisionNumber() == revisionNumber){
					//revision already exists, nothing to do
					return;
				}
			}			
			//create new revision and it's representation. Finally, connect model and revision
			Map<String, Collection<String>> metaData = parseMetaData(rootElement);
			IRevision revision = createRevisionAndRepresentations(metaData, revisionNumber);
			revision.connectModel(model);			
		}
		this.persistenceApi.savePojo(model);
		
		if(this.createdModelsCount % 100 == 0) {
			logger.info("imported or updated " + this.createdModelsCount + " models");
		}
		if(this.createdModelsCount % 20 == 0) {
			this.persistenceApi.clearCache();
		}
	}

	/**
	 * Creates a new {@link Revision} and all {@link Representation}s from the given meta data mapping.
	 * 
	 * @param metaData the data to use for {@link Revision} and {@link Representation} creation.
	 * @param revisionNumber number to use for new {@link Revision}
	 */
	private IRevision createRevisionAndRepresentations(Map<String, Collection<String>> metaData, int revisionNumber) {
		IRevision revision = this.persistenceApi.getPojoFactory().createRevision(revisionNumber);
		revision.setLatestRevision(true);
		
		//set meta data
		for (String metaDataKey : metaData.keySet()){
			Collection<String> values = metaData.get(metaDataKey);
			for (String value : values){
				revision.addMetadataAtKey(metaDataKey, value);
			}
		}		
		if (metaData.containsKey(KEY_PROCESS_AUTHOR)) {
			revision.setAuthor(metaData.get(KEY_PROCESS_AUTHOR).iterator().next());
		}
		
		//create Representations if data is available
		if (metaData.containsKey(KEY_IMAGE_FILE)) {
			revision.connectRepresentation(createRepresentation(metaData.get(KEY_IMAGE_FILE), getNotation(metaData)));
		}
		if (metaData.containsKey(KEY_SOURCE_FILE)) {
			revision.connectRepresentation(createRepresentation(metaData.get(KEY_SOURCE_FILE), getNotation(metaData)));
		}
		this.createdRevisionsCount++;
		return revision;
	}

	/**
	 * Parses the notation from the given meta data.
	 * @param metaData the meta data of the current process
	 * @return the notation used for the process model or an empty string if none was given.
	 */
	private String getNotation(Map<String, Collection<String>> metaData) {
		String notation = "";
		if (metaData.containsKey(KEY_MODELING_METHOD)){
			String modelingMethod = metaData.get(KEY_MODELING_METHOD).iterator().next();
			if (KEY_EPK_OR_eEPK.equals(modelingMethod)){
				notation = Constants.NOTATION_EPC;
			} else if (KEY_BPMN.equals(modelingMethod)){
				notation = Constants.NOTATION_BPMN2_0;
			}
		}
		return notation;
	}

	/**
	 * Parses all meta data from the given {@link Element}'s 'metaInformation' tags.
	 * 
	 * @param rootElement the root element of the XML to consider for parsing
	 * @return all parsed meta data
	 */
	private Map<String, Collection<String>> parseMetaData(Element rootElement) {
		Map<String, Collection<String>> metaData = new HashMap<String, Collection<String>>();
		List<?> metaDataElements = rootElement.getChildren(KEY_META_INFORMATION, rootElement.getNamespace());
		for (Object metaDataElement : metaDataElements) {
			if (metaDataElement instanceof Element && ((Element) metaDataElement).getName().equals(KEY_META_INFORMATION)){
				List<?> metaInformation = ((Element) metaDataElement).getChildren(KEY_SCHLUESSEL, rootElement.getNamespace());
				if (!metaInformation.isEmpty()) {
					String key = ((Element) metaInformation.get(0)).getChild(KEY_CODE).getText();
					metaInformation = ((Element) metaDataElement).getChildren(KEY_WERT, rootElement.getNamespace());
					Collection<String> values = new ArrayList<String>();
					if(!metaInformation.isEmpty()){
						Element valueElement = (Element)metaInformation.get(0);
						if(valueElement.getChild(KEY_LISTE, rootElement.getNamespace()) == null){
							//single value
							Element value = ((Element) valueElement.getChildren().get(0));
							values.add(StringEscapeUtils.unescapeHtml4(value.getText()));
							String name = value.getAttributeValue(KEY_NAME);
							if (name != null) {
								values.add(StringEscapeUtils.unescapeHtml4(name));
							}
						} else {
							//parse all values
							List<?> valueElements = valueElement.getChild(KEY_LISTE, rootElement.getNamespace()).getChildren();
							for(Object value : valueElements){
								values.add(StringEscapeUtils.unescapeHtml4(((Element) value).getText()));
							}
						}
					}
					metaData.put(key, values);
				}
			}
		}
		return metaData;
	}
	
	/**
	 * Parses the revision number from the given element's revision attribute, which 
	 * is the first part of the complete revision attribute value.
	 * @param element the element of the XML containing the revision attribute
	 * @return the revision parsed from the given element's revision attribute
	 */
	private Integer getRevisionNumber(Element element) {
		String completeModelId = element.getAttributeValue(KEY_REVISION, "");
		return new Integer(completeModelId.split("-")[0]);
	}

	/**
	 * Creates a new {@link Representation} with the given notation and data file content.
	 * 
	 * @param dataContent the data content as first and the type as second element
	 * @param notation the notation that was used
	 * 
	 * @return a new {@link Representation} with the given notation, data content and data format
	 */
	private IRepresentation createRepresentation(Collection<String> dataContent, String notation) {
		Iterator<String> it = dataContent.iterator();
		byte[] data = DatatypeConverter.parseBase64Binary(it.next());
		String format[] = it.next().split("\\.");
		this.createdRepresentationsCount++;
		return this.persistenceApi.getPojoFactory().createRepresentation(format[format.length - 1], notation, data);
	}

}