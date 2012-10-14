package de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.label.preprocessing;

import java.util.Collection;

import org.jbpt.alignment.LabelEntity;


/**
 * A PreProcessingStep is an operation on a given set of Strings
 * that transforms each String into a similar one, which
 * is better to handle for further processing.     
 * @author stefan.schaefer
 */
public interface PreProcessingStep {
	/** Performs the PreProcessingStep on each of the {@link LabelEntity}s, reusing the given entity object */
	public void processAll(Collection<LabelEntity> labels1, Collection<LabelEntity> labels2);
}
