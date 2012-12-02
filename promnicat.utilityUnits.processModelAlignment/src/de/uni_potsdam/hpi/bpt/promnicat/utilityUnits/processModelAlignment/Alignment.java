package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment;

import org.jbpt.alignment.IEntity;
import org.jbpt.pm.ProcessModel;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.similarity.matrix.ISimilarityMatrix;

public interface Alignment<Entity extends IEntity> {
	/** The given processes are aligned to one another, i.e. their similarity is calculated */
	public ISimilarityMatrix<Entity> align(ProcessModel process1, ProcessModel process2);
}
