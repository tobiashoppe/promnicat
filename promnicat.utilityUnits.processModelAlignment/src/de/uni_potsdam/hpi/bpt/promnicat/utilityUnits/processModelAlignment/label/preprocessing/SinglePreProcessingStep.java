package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.preprocessing;

import java.util.Collection;
import java.util.List;

import org.jbpt.alignment.LabelEntity;

/**
 * The {@link SinglePreProcessingStep} processes each String isolated.
 * It provides a convenient implementation of 
 * {@link #processAll(List, List)}, which reuses the given {@link LabelEntity}s.
 *  
 * @author stefan.schaefer
 */
public abstract class SinglePreProcessingStep implements PreProcessingStep {

	public abstract String process(String in);

	@Override
	public void processAll(Collection<LabelEntity> labels1, Collection<LabelEntity> labels2) {
		for (LabelEntity each : labels1) {
			each.setLabel(process(each.getLabel()));
		}
		for (LabelEntity each : labels2) {
			each.setLabel(process(each.getLabel()));
		}
	}
}
