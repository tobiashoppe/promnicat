package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.test.util;

import java.io.BufferedReader;
import java.io.FileReader;

import org.json.JSONException;
import org.json.JSONObject;

import de.uni_potsdam.hpi.bpt.ai.diagram.Diagram;
import de.uni_potsdam.hpi.bpt.ai.diagram.DiagramBuilder;
import de.uni_potsdam.hpi.bpt.ai.diagram.JSONBuilder;
import de.uni_potsdam.hpi.bpt.ai.diagram.StencilSet;
import de.uni_potsdam.hpi.bpt.ai.diagram.StencilType;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.correctors.DiagramCorrector;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.wrapper.DiagramWrapper;
/**
 * Creates the models for testing the {@link DiagramCorrector}
 * @author Christian Kieschnick
 *
 */
public class TestModelBuilder {
	
	public static String readFromFile(String filepath) throws Exception {
		StringBuilder result = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filepath));
			String currentLine;
			while ((currentLine = br.readLine()) != null) {
				result.append(currentLine);
			}
		} catch (Exception e) {
			throw new Exception("Could not initialize modeldata for "+filepath, e);
		}
		return result.toString();
	}

	public static Diagram getModelFromFile(String filename) throws Exception {
		String stringModel = readFromFile("resources/correction_test/"+filename+".json");
		Diagram model = DiagramBuilder.parseJson(stringModel);
		// this fix is necessary because the DiagramBuilder does not set the StencilSet Url in case a specification is attachted, but needs it for exports
		JSONObject jsonModel = new JSONObject(stringModel);
		model.getStencilset().setUrl(jsonModel.getJSONObject("stencilset").getString("url"));
		return model;
	}
	
	public static DiagramWrapper getWrappedModelFromFile(String filename) throws Exception {
		return new DiagramWrapper(getModelFromFile(filename));
	}
		
	public static Diagram createDiagramWith(String namespace){
		StencilSet stencilSet = new StencilSet(namespace);
		StencilType type = new StencilType("canvas", stencilSet);
		return new Diagram("", type, stencilSet);
	}
	
	public static String getStringFromModel(Diagram model) throws JSONException{
		return JSONBuilder.parseModel(model).toString();
	}
	
}
