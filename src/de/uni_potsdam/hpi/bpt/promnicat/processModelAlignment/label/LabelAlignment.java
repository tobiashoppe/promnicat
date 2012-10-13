package de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.label;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.jbpt.alignment.LabelEntity;
import org.jbpt.pm.Activity;
import org.jbpt.pm.ProcessModel;

import de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.Alignment;
import de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.assignment.Assignment;
import de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.label.preprocessing.PreProcessingStep;
import de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.label.similarity.LabelSimilarity;
import de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.label.similarity.matrix.ISimilarityMatrix;

/**
 * The LabelAlignment implements a label-based alignment of process models.
 * Prior to executing an alignment you need to explicitly set a similarity and assignment algorithm.
 *
 * @author stefan.schaefer
 * @see #align(ProcessModel, ProcessModel)
 * @see #align(Collection, Collection)
 * @see #setSimilarity(LabelSimilarity)
 * @see #setAssignment(Assignment)
 */
public class LabelAlignment implements Alignment<LabelEntity> { 
	private List<PreProcessingStep> preProcessingSteps = new ArrayList<PreProcessingStep>();
	private LabelSimilarity similarity;
	private Assignment assignment;
	private final Logger log = Logger.getLogger(LabelAlignment.class);

	/**
	 * Aligns two sets of labels, computing their overall similarity.
	 * @param labels1 the labels to align
	 * @param labels2 the labels to align
	 * @return a float between 0 (least similar) and 1 (most similar)
	 */
	@SuppressWarnings("unchecked")
	public ISimilarityMatrix<LabelEntity> align(Collection<LabelEntity> labels1, Collection<LabelEntity> labels2) {
		if (preProcessingSteps != null) {
			for (PreProcessingStep step : preProcessingSteps) {
				step.processAll(labels1,labels2);
			}
		}
		
		if (log.isDebugEnabled()) {
			log.debug("Task labels of Model 1:");
			for (LabelEntity label : labels1) {
				log.debug(label);
			}
			log.debug("Task labels of Model 2:");
			for (LabelEntity label : labels2) {
				log.debug(label);
			}
		}
		
		ISimilarityMatrix<LabelEntity> similarities = similarity.getSimilarityMatrix(labels1, labels2);
		@SuppressWarnings({ "rawtypes" })
		ISimilarityMatrix result = assignment.assign((ISimilarityMatrix) similarities);
		return result;
	}
	
	/**
	 * @see #align(Collection, Collection)
	 * @see #setSimilarity(LabelSimilarity)
	 * @see #setAssignment(Assignment)
	 */
	@Override
	public ISimilarityMatrix<LabelEntity> align(ProcessModel process1, ProcessModel process2) {
		if (similarity == null) {
			throw new IllegalArgumentException("No Similarity algorithm specified!");
		}
		if (assignment == null) {
			throw new IllegalArgumentException("No Assignment algorithm specified!");
		}
		log.info("Aligning ProcessModel '"+process1.getLabel()+"' with '"+process2.getLabel()+"'");
		Collection<LabelEntity> labels1 = getLabels(process1);
		Collection<LabelEntity> labels2 = getLabels(process2);
		return align(labels1,labels2);
	}

	/** @return the labels of the activities of the given model */
	private HashSet<LabelEntity> getLabels(ProcessModel model) {
		Collection<Activity> tasks = model.getActivities();
		HashSet<LabelEntity> labels = new HashSet<LabelEntity>(tasks.size());
		for (Activity activity : tasks) {
			labels.add(new LabelEntity(activity.getLabel()));
		}
		return labels;
	}

	public List<PreProcessingStep> getPreProcessingSteps() {
		return preProcessingSteps;
	}

	public void setPreProcessingSteps(List<PreProcessingStep> preProcessingSteps) {
		this.preProcessingSteps = preProcessingSteps;
	}

	public void addPreProcessingStep(PreProcessingStep preProcessingStep) {
		this.preProcessingSteps.add(preProcessingStep);
	}

	public LabelSimilarity getSimilarity() {
		return similarity;
	}

	public void setSimilarity(LabelSimilarity similarity) {
		this.similarity = similarity;
	}

	public Assignment getAssignment() {
		return assignment;
	}

	public void setAssignment(Assignment assignment) {
		this.assignment = assignment;
	}

}
