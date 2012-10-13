package de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.label.preprocessing;

import java.util.Collection;
import java.util.List;

import org.jbpt.alignment.LabelEntity;

/**
 * The {@link PairwisePreProcessingStep} processes all combinations of pairs from
 * labels1 and labels2.
 * It provides a convenient implementation of 
 * {@link #processAll(List, List)}, which reuses the given {@link LabelEntity}s.
 *  
 * @author stefan.schaefer
 */
public abstract class PairwisePreProcessingStep implements PreProcessingStep {
	/** process a pair of labels and return their new values as a String[2] array */
	public abstract String[] process(String label1, String label2);

	@Override
	public void processAll(Collection<LabelEntity> labels1, Collection<LabelEntity> labels2) {
		for (LabelEntity each1 : labels1) {
			for (LabelEntity each2 : labels2) {
				String[] processed = process(each1.getLabel(), each2.getLabel());
				each1.setLabel(processed[0]);
				each2.setLabel(processed[1]);
			}
		}
	}
}
