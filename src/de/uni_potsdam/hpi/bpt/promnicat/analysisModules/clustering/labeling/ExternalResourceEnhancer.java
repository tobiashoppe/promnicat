/**
 * 
 */
package de.uni_potsdam.hpi.bpt.promnicat.analysisModules.clustering.labeling;

import java.util.HashMap;

/**
 * Abstract class for modules that enhance the label candidate corpus by using external resources,
 * such as WordNet or DBpedia.
 * @author Cindy Fähnrich
 *
 */
public abstract class ExternalResourceEnhancer {

	public abstract void enhanceLabelCandidates(HashMap<String, Double> labelCandidates);
}
