package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment;

import org.jbpt.alignment.IEntity;
import org.jbpt.pm.ProcessModel;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.similarity.matrix.ISimilarityMatrix;


/**
 * {@link #align(ProcessModel, ProcessModel)} two process models  
 * @author stefan.schaefer
 */
public class ProcessModelAlignment {
	private Alignment<IEntity> alignment;
	
	/** default constructor. A specific entity alignment must be set
	 *  using {@link #setAlignment(Alignment)} prior to {@link #align(ProcessModel, ProcessModel)} */
	public ProcessModelAlignment() {
	}
	/** initializes the process model alignment with an entity alignment */
	public ProcessModelAlignment(Alignment<IEntity> alignment) {
		this.alignment = alignment;
	}
	
	/**
	 * compares two {@link ProcessModel}s,
	 * using a {@link Alignment}, so that the
	 * overall similarity of the processes equals the similarity 
	 * of their entity alignment  
	 * @param process1 a jpbt ProcessModel
	 * @param process2 a jpbt ProcessModel
	 * @return an {@link ISimilarityMatrix} storing the aligned entities
	 * @see #setAlignment(Alignment)
	 */
	public ISimilarityMatrix<IEntity> align(ProcessModel process1, ProcessModel process2) {
		return getAlignment().align(process1, process2);
	}

	public Alignment<IEntity> getAlignment() {
		return alignment;
	}

	/** a specific alignment must be set prior to aligning processes */
	public void setAlignment(Alignment<IEntity> alignment) {
		this.alignment = alignment;
	}
}
