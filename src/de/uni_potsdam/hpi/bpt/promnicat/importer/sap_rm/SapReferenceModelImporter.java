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
package de.uni_potsdam.hpi.bpt.promnicat.importer.sap_rm;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.uni_potsdam.hpi.bpt.ai.diagram.Diagram;
import de.uni_potsdam.hpi.bpt.ai.diagram.DiagramBuilder;
import de.uni_potsdam.hpi.bpt.promnicat.importer.AbstractImporter;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPersistenceApi;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.Model;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.Revision;
import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;

/**
 * This class is used to import the SAP reference model.
 * 
 * @author Tobias Hoppe
 * 
 */
public class SapReferenceModelImporter extends AbstractImporter {
    private IPersistenceApi persistenceApi = null;

    /**
     * Creates a new {@link SapReferenceModelImporter} with the given {@link IPersistenceApi} used for database access.
     * @param persistenceApi persistence API used by importer
     */
    public SapReferenceModelImporter(IPersistenceApi persistenceApi) {
	this.persistenceApi = persistenceApi;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_potsdam.hpi.bpt.promnicat.importer.IImporter#importModelsFrom(java.net.URI)
     */
    @Override
    public void importModelsFrom(String modelDirectory) throws IOException, JSONException {
	persistenceApi.openDb();
	File rootDir = super.checkModelPath(modelDirectory, true);
	Collection<File> files = super.getFilesRecursivelyFromDir(rootDir);
	for(File file:files){
	    try {
		splitEPML(file);
	    } catch (ParserConfigurationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (SAXException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (TransformerException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}

	persistenceApi.closeDb();
    }
    public void splitEPML(File file) throws ParserConfigurationException, SAXException, IOException, TransformerException, JSONException{
	DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
	DocumentBuilder builder = fac.newDocumentBuilder();
	Document doc = builder.parse(file);
	NodeList epcNodes = doc.getElementsByTagName("epc");
	for(int i = 0; i < epcNodes.getLength(); i ++ ){
	    String epcName = epcNodes.item(i).getAttributes().getNamedItem("name").getNodeValue();

	    Model model = this.persistenceApi.loadCompleteModelWithImportedId(epcName);
	    if (model != null){
		continue;
	    }
	    
	    Document newEpcDoc = builder.newDocument();
	    Element epml = newEpcDoc.createElement("epml:epml");
	    Element directory = newEpcDoc.createElement("directory");
	    newEpcDoc.appendChild(epml);
	    epml.appendChild(directory);
	    directory.setAttribute("name", "ROOT");
	    Node epcNode = newEpcDoc.importNode(epcNodes.item(i), true);
	    directory.appendChild(epcNode);

	    DOMSource epmlSource = new DOMSource(newEpcDoc);
	    final File epml2eRDFxsltFile = new File("resources/xslt/EPML2eRDF.xslt");
	    final Source epml2eRDFxsltSource = new StreamSource(epml2eRDFxsltFile);	

	    // Transformer Factory
	    final TransformerFactory transformerFactory = TransformerFactory.newInstance();

	    // Get the epml source
	    Transformer transformer = transformerFactory.newTransformer(epml2eRDFxsltSource);
	    StringWriter writer = new StringWriter();
	    transformer.transform(epmlSource, new StreamResult(writer));
	    String erdf = writer.toString();
	    String rdf = erdfToRdf(erdf);
	    rdf = rdf.replaceAll("ns[0-9]+:", "");
	    rdf = rdf.replace("#resource", "#");
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    factory.setNamespaceAware(true);
	    Document rdfDoc = builder.parse(new ByteArrayInputStream(rdf.getBytes("UTF-8")));
	    String json =  RdfJsonTransformation.toJson(rdfDoc, "").toString();

	    //Diagram diagram = DiagramBuilder.parseJson(json);
	    Representation representation = new Representation(Constants.FORMAT_BPMAI_JSON, Constants.NOTATION_EPC);
	    Diagram parseJson = DiagramBuilder.parseJson(json);
	    representation.setDataContent(json.getBytes());
	    model = new Model(epcName, Constants.ORIGIN_SAP_RM);
	    model.setImportedId(epcName);
	    Revision revision = new Revision(0);
	    revision.connectRepresentation(representation);
	    model.connectLatestRevision(revision); 
	    revision.connectModel(model);
	    model.connectLatestRevision(revision);
	    persistenceApi.savePojo(model);


    }

}
/**
 * Helper method which writes a XML document to the given filename
 * @param outProfileXMLdocument
 * @param xmlFileName
 * @throws FileNotFoundException
 */
private static void writeXMLToFile(Document outProfileXMLdocument,
	String xmlFileName) throws FileNotFoundException {
    // ---- Use a XSLT transformer for writing the new XML file ----
    try {
	Transformer transformer = TransformerFactory.newInstance()
		.newTransformer();
	DOMSource source = new DOMSource(outProfileXMLdocument);
	FileOutputStream os = new FileOutputStream(new File(xmlFileName));
	StreamResult result = new StreamResult(os);
	transformer.transform(source, result);
    } catch (TransformerConfigurationException e) {
	e.printStackTrace();
    } catch (TransformerFactoryConfigurationError e) {
	e.printStackTrace();
    } catch (TransformerException e) {
	e.printStackTrace();
    }
}
protected String erdfToRdf(String erdf) throws TransformerException, FileNotFoundException{
    erdf = erdf.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
    String epcCanvasNode = "<div id=\"oryx-canvas123\" class=\"-oryx-canvas\"><span class=\"oryx-type\">http://b3mn.org/stencilset/epc#Diagram</span><span class=\"oryx-title\"></span><span class=\"oryx-version\"></span><span class=\"oryx-author\"></span><span class=\"oryx-description\"></span><span class=\"oryx-mode\">writable</span><span class=\"oryx-mode\">fullscreen</span><a rel=\"oryx-stencilset\" href=\"/oryx//stencilsets/epc/epc.json\"/><a rel=\"oryx-ssnamespace\" href=\"http://b3mn.org/stencilset/epc#\"/><a rel=\"oryx-render\" href=\"#oryx_64B5C11B-B687-478F-9484-2754D57936DE\"/><a rel=\"oryx-render\" href=\"#oryx_6C9B2F26-39BE-4A80-BF8D-F6BF48427F12\"/><a rel=\"oryx-render\" href=\"#oryx_B0D82B5D-BF33-4A64-A5EF-BC4DD5E006C1\"/><a rel=\"oryx-render\" href=\"#oryx_821F2F7C-D2A4-4D92-B6BF-B37ECA795FAA\"/><a rel=\"oryx-render\" href=\"#oryx_696EB3FE-4F56-4A44-9920-7361648A2A8C\"/></div>";
    String serializedDOM = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
	    "<html xmlns=\"http://www.w3.org/1999/xhtml\" " +
	    "xmlns:b3mn=\"http://b3mn.org/2007/b3mn\" " +
	    "xmlns:ext=\"http://b3mn.org/2007/ext\" " +
	    "xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" "  +
	    "xmlns:atom=\"http://b3mn.org/2007/atom+xhtml\">" +
	    "<head profile=\"http://purl.org/NET/erdf/profile\">" +
	    "<link rel=\"schema.dc\" href=\"http://purl.org/dc/elements/1.1/\" />" +
	    "<link rel=\"schema.dcTerms\" href=\"http://purl.org/dc/terms/ \" />" +
	    "<link rel=\"schema.b3mn\" href=\"http://b3mn.org\" />" +
	    "<link rel=\"schema.oryx\" href=\"http://oryx-editor.org/\" />" +
	    "<link rel=\"schema.raziel\" href=\"http://raziel.org/\" />" +
	    "</head><body>"+ 
	    epcCanvasNode + erdf + "</body></html>" ;
    InputStream xsltStream = new FileInputStream("resources/xslt/extract-rdf.xsl");
    Source xsltSource = new StreamSource(xsltStream);
    Source erdfSource = new StreamSource(new StringReader(serializedDOM));

    TransformerFactory transFact =
	    TransformerFactory.newInstance();
    Transformer trans = transFact.newTransformer(xsltSource);
    StringWriter output = new StringWriter();
    trans.transform(erdfSource, new StreamResult(output));
    return output.toString();
}
}
