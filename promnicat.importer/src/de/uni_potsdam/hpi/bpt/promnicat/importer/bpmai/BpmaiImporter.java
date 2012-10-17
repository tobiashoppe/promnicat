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
package de.uni_potsdam.hpi.bpt.promnicat.importer.bpmai;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.json.JSONException;

import de.uni_potsdam.hpi.bpt.ai.collection.BPMAIExport;
import de.uni_potsdam.hpi.bpt.ai.collection.BPMAIExportBuilder;
import de.uni_potsdam.hpi.bpt.ai.diagram.Diagram;
import de.uni_potsdam.hpi.bpt.promnicat.importer.AbstractImporter;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IModel;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPersistenceApi;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IRepresentation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IRevision;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Model;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Revision;
import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;
import de.uni_potsdam.hpi.bpt.promnicat.util.ConstantsMapper;

/**
 * This class is used to import models from the BPM Academic Initiative.
 * 
 * @author Andrina Mascher, Tobias Hoppe
 * 
 */
public class BpmaiImporter extends AbstractImporter {

	private static final String PROPERTY_AUTHOR = "author";
	private static final String PROPERTY_LANGUAGE = "language";

	private int createdRepresentationsCount = 0;
	private int createdRevisionsCount = 0;

	private IPersistenceApi persistenceApi = null;

	private final static Logger logger = Logger.getLogger(BpmaiImporter.class.getName());

	/**
	 * Creates a new {@link BpmaiImporter} with the given {@link IPersistenceApi} used for database access.
	 * @param persistenceApi persistence API used by importer
	 */
	public BpmaiImporter(IPersistenceApi persistenceApi) {
		this.persistenceApi = persistenceApi;
	}

	@Override
	public void importModelsFrom(String modelDirectory) throws IOException, JSONException {
		File rootDir = super.checkModelPath(modelDirectory, true);
		//reset counter
		this.createdRepresentationsCount = 0;
		this.createdRevisionsCount = 0;

		//import all models
		importAll(rootDir);
	}

	/**
	 * Reads the given content and writes it into a  file with the given path.
	 * @param in stream to read
	 * @param targetPath path to write the read content to
	 * @throws IOException if the specified path does not exists.
	 */
	private void copyInputStream(InputStream in, String targetPath)	throws IOException {
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(targetPath));
		byte[] buffer = new byte[1024];
		int len;
		while((len = in.read(buffer)) >= 0) {
			bufferedOutputStream.write(buffer, 0, len);
		}
		in.close();
		bufferedOutputStream.close();
	}
	
	/**
	 * Scans the given root directory for sgx-archives and extracts them into the dummy folder.
	 * The extracted models can be parsed like any other process models from the BPM AI.
	 * @param rootDir container of archives to extract
	 * @param dummyFolder folder to extract the models to
	 * @throws ZipException if archive extraction went wrong
	 * @throws IOException if one of the given paths can not be read or written
	 */
	private void extractAvailableSgxArchives(File rootDir, File dummyFolder) throws ZipException, IOException {
		for(File file : rootDir.listFiles()) {
			if((!file.isDirectory()) && (file.getName().endsWith(".sgx"))) {
				ZipFile zipFile = new ZipFile(file);
				Enumeration<? extends ZipEntry> entries = zipFile.entries();
				//iterate through files of an zip archive
				while(entries.hasMoreElements()) {
					ZipEntry entry = (ZipEntry)entries.nextElement();
					String entryName = entry.getName();
					if(entryName.contains("/")) {
						//ignore meta data files
						if(entryName.endsWith("_meta.json")){
							continue;
						}
						//remove directory folder to fit into expected structure
						String[] pathParts = entryName.split("/");
						if(entryName.contains("directory_")) {
							entryName = "";
							for (int i = 0; i < pathParts.length; i++) {
								if (!(pathParts[i].startsWith("directory_"))) {
									entryName = entryName.concat(pathParts[i] + "/");
								}
							}
							entryName = entryName.substring(0, entryName.length() - 1);
						}
						//rename process model files
						String oldModelName = pathParts[pathParts.length - 1];
						String[] nameParts = oldModelName.split("_");
						if (nameParts.length > 2) {
							String modelName = pathParts[pathParts.length - 2].split("_")[1] + "_rev" + nameParts[1] + nameParts[2];
							entryName = entryName.replace(oldModelName, modelName);
						}
						//create directories
						(new File(dummyFolder.getPath() + File.separatorChar + entryName.substring(0, entryName.lastIndexOf("/")))).mkdirs();
					}
					//extract process model
					copyInputStream(zipFile.getInputStream(entry), dummyFolder.getPath() + File.separatorChar + entryName);
				}
				zipFile.close();
			}
		}
	}

	/**
	 * Get the notation of the given {@link Diagram}.
	 * 
	 * @param diagram to get the notation of
	 * @return simple name of this stencil set, e.g. bpmn2.0
	 */
	private String getNotation(Diagram diagram) {
		String namespace = diagram.getStencilset().getNamespace();
		// e.g. http://b3mn.org/stencilset/bpmn2.0#
	
		String[] array = namespace.split("/");
		String format = array[array.length - 1].replace("#", "");
		return ConstantsMapper.mapNotation(format);
	}

	/**
	 * Import all process models from the given directory tree into the database.
	 * Already existing {@link Model}s and {@link Revision}s are skipped.
	 * 
	 * @param rootDir the root directory of the models to import
	 * 
	 * @throws JSONException if JSON parsing is erroneous 
	 * @throws IOException if the given path could not be found or read
	 */
	private void importAll(File rootDir) throws JSONException, IOException {
		int modelCounter = 0;
		this.persistenceApi.openDb();
		
		//temp folder being used for extraction of sgx archives
		File container = new File(rootDir + File.separator + "dummy");
		container.mkdir();
		//search for sgx-archives and unzip them
		extractAvailableSgxArchives(rootDir, container);
		
		// parse directory
		BPMAIExport directoryWalker = BPMAIExportBuilder.parseDirectory(rootDir);
		for (de.uni_potsdam.hpi.bpt.ai.collection.Model bpmAiModel : directoryWalker.getModels()) {
	
			IModel model = this.persistenceApi.loadCompleteModelWithImportedId(bpmAiModel.getId().toString());
			if (model == null){
				//create and save new Model
				model = parseModel(bpmAiModel);
				this.persistenceApi.savePojo(model);
			} else {
				//check for new revision
				boolean found = false;
				for (de.uni_potsdam.hpi.bpt.ai.collection.Revision bpmAiRev : bpmAiModel.getRevisions()){
					found = false;
					for (IRevision revision : model.getRevisions()){
						if (revision.getRevisionNumber().equals(bpmAiRev.getNumber())){
							//search for new representations
							boolean containsJsonRepresentation = false;
							boolean containsSvgRepresentation = false;
							for(IRepresentation representation : revision.getRepresentations()){
								if (representation.getFormat().equals(Constants.FORMAT_BPMAI_JSON)) {
									containsJsonRepresentation = true;
									continue;
								}
								if (representation.getFormat().equals(Constants.FORMAT_SVG)) {
									containsSvgRepresentation = true;
									continue;
								}
							}
							//if the representations for Json or Svg do not exist, they are parsed
							if (!containsJsonRepresentation) {
								IRepresentation representation = parseRepresentation(bpmAiRev, Constants.FORMAT_BPMAI_JSON);
								if (representation != null) {
									revision.connectRepresentation(representation);								
								}
							}
							if (!containsSvgRepresentation) {
								IRepresentation representation = parseRepresentation(bpmAiRev, Constants.FORMAT_SVG);
								if (representation != null) {
									revision.connectRepresentation(representation);								
								}
							}
							if (!containsJsonRepresentation || !containsSvgRepresentation) {
								this.persistenceApi.savePojo(model);
							}
							found = true;
							break;
						}
					}
					if (!found){
						//create new revision
						IRevision revision = parseRevision(bpmAiRev);
						if (model.getLatestRevision().getRevisionNumber() < bpmAiRev.getNumber()) {
							model.connectLatestRevision(revision);
						}
						revision.connectModel(model);
						this.persistenceApi.savePojo(model);
					}
				}
	
			}
	
			modelCounter++;
			if(modelCounter % 100 == 0) {
				this.persistenceApi.clearCache();
				logger.info("imported or updated " + modelCounter + " models");
			}
		}
		this.persistenceApi.closeDb();
		//delete dummy folder containing extracted sgx archives
		deleteDirectory(container);
	
		logger.info("Finished import or update of " + modelCounter + " models," +
				" and created " + this.createdRevisionsCount + " revisions and " + this.createdRepresentationsCount + " representations.");
	}

	/**
	 * Transform given meta data into an other format
	 * @param properties current meta data
	 * @return original given meta data, but the value is represented as an array 
	 */
	private HashMap<String, String[]> parseMetadata(HashMap<String, String> properties) {
		HashMap<String,String[]> newMap = new HashMap<String, String[]>();
		for(Entry<String, String> e : properties.entrySet()) {
			String[] newValue = {e.getValue()};
			newMap.put(e.getKey(), newValue);
		}
		return newMap;
	}

	/**
	 * Creates a new {@link Model} with all it's {@link Revision}s and their corresponding {@link Representation}s.
	 * 
	 * @param bpmAiModel the BPM AI process model to parse
	 * 
	 * @return the parsed {@link IModel} referencing it's {@link Revision}s and {@link Representation}s.
	 * 
	 * @throws JSONException if JSON parsing is erroneous 
	 * @throws IOException if the given source path could not be found or read
	 */
	private IModel parseModel(de.uni_potsdam.hpi.bpt.ai.collection.Model bpmAiModel)	throws JSONException, IOException {

		IModel model = this.persistenceApi.getPojoFactory().createModel(bpmAiModel.getName(), Constants.ORIGIN_BPMAI, bpmAiModel.getId().toString());
		IRevision revision = null;

		// one model has several revisions
		for (de.uni_potsdam.hpi.bpt.ai.collection.Revision oldRev : bpmAiModel.getRevisions()) {
			revision = parseRevision(oldRev);
			revision.connectModel(model);
		}
		model.connectLatestRevision(revision); 
		return model;
	}

	/**
	 * Looks up the value of a given property in the given diagram.
	 * 
	 * @param diagram to be used for look up
	 * @param propertyName name of property to look up
	 * @return the value of the property or <code>null</code> if it could not be found.
	 */
	private String parseProperty(Diagram diagram, String propertyName) {
		if(!diagram.getProperties().containsKey(propertyName)) {
			return null;
		}
		return diagram.getProperties().get(propertyName);
	}

	/**
	 * Creates a new {@link Revision} with the meta data parsed from given {@link de.uni_potsdam.hpi.bpt.ai.collection.Revision}
	 * and creates all available {@link Representation}s.
	 * 
	 * @param bpmAiRev revision to parse the meta data from
	 * @return a new {@link IRevision} with the given index and the meta data parsed from the given {@link Diagram}.
	 * @throws IOException if JSON parsing is erroneous
	 * @throws JSONException if the given source path could not be found or read
	 */
	private IRevision parseRevision(de.uni_potsdam.hpi.bpt.ai.collection.Revision bpmAiRev) throws JSONException, IOException {
		IRevision revision = this.persistenceApi.getPojoFactory().createRevision(bpmAiRev.getNumber());
		Diagram diagram = bpmAiRev.getDiagram();
		revision.setMetadata(parseMetadata(diagram.getProperties()));
		revision.setAuthor(parseProperty(diagram, PROPERTY_AUTHOR));
		this.createdRevisionsCount++;
		
		//create available representations
		IRepresentation JsonRep = parseRepresentation(bpmAiRev, Constants.FORMAT_BPMAI_JSON);
		if (JsonRep != null) {
			revision.connectRepresentation(JsonRep);
		}
		IRepresentation SvgRep = parseRepresentation(bpmAiRev, Constants.FORMAT_SVG);
		if (SvgRep != null) {
			revision.connectRepresentation(SvgRep);
		}
		return revision;
	}
	
	/**
	 * Parses a {@link Representation} with the given format from the data given by the {@link de.uni_potsdam.hpi.bpt.ai.collection.Revision}.
	 * 
	 * @param bpmAiRev {@link de.uni_potsdam.hpi.bpt.ai.collection.Revision} containing the data to {@link Representation} is parsed from
	 * @param format the format of the new {@link Representation}
	 * @return the parsed {@link Representation}
	 */
	private IRepresentation parseRepresentation(de.uni_potsdam.hpi.bpt.ai.collection.Revision bpmAiRev, String format) {	
		IRepresentation representation = null;
		Diagram diagram = null;
		try{
			diagram = bpmAiRev.getDiagram();
		} catch (Exception e) {
			logger.info("This revision has no Json file: " + bpmAiRev.toString());
			return null;
		}
		
		// one revision can have two representations: JSON and SVG
		if (format.equals(Constants.FORMAT_BPMAI_JSON) && bpmAiRev.getJson() != null) {
			representation = this.persistenceApi.getPojoFactory().createRepresentation(Constants.FORMAT_BPMAI_JSON, getNotation(diagram), bpmAiRev.getJson());
			representation.setLanguage(parseProperty(diagram, PROPERTY_LANGUAGE));
			this.createdRepresentationsCount++;
		} else if (format.equals(Constants.FORMAT_SVG) && bpmAiRev.getSvg() != null) {
			representation = this.persistenceApi.getPojoFactory().createRepresentation(Constants.FORMAT_SVG, getNotation(diagram), bpmAiRev.getSvg());
			representation.setLanguage(parseProperty(diagram, PROPERTY_LANGUAGE));
			this.createdRepresentationsCount++;
		}
		return representation;
	}
}