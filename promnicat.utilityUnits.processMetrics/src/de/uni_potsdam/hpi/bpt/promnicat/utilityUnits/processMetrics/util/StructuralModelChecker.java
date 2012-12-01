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
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processMetrics.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.jbpt.algo.tree.rpst.RPST;
import org.jbpt.algo.tree.tctree.TCType;
import org.jbpt.pm.Activity;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.Gateway;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.XorGateway;

/**
 * This class uses BpStruct to check whether a {@link ProcessModel} can be structured or is structured.
 *  
 * TODO use bpstruct-lib's BPStructAPI class if it gets updated to a compatible jBPT version.
 * the current implementation only checks, whether a {@link ProcessModel} is structured using jBPT's 
 * {@link RPST} implementation.
 * @author Tobias Hoppe
 *
 */
public class StructuralModelChecker {

	private static final Logger logger = Logger.getLogger(StructuralModelChecker.class.getName());
	
	/**
	 * Check if a process is already structured.
	 * @param process to check
	 * @return <code>true</code> if process is structured. Otherwise, <code>false</code>.
	 */
	public static boolean isStructured(ProcessModel process) {
		ProcessModel copy = null;
		copy = process.clone();
		
		List<FlowNode> sources = new ArrayList<FlowNode>();
		List<FlowNode> sinks = new ArrayList<FlowNode>();
		// check if the process has multiple sources or sinks
		for (FlowNode node:copy.getFlowNodes()) {
			if (copy.getIncomingEdges(node).isEmpty())
				sources.add(node);
			if (copy.getOutgoingEdges(node).isEmpty())
				sinks.add(node);
		}
		if (sources.size() > 1) {
			// add a single source and connect it to the former sources
			Activity start = new Activity("_start_");
			Gateway gate = new XorGateway();
			copy.addEdge(start, gate);
			for (FlowNode node:sources)
				copy.addEdge(gate, node);
		}
		if (sinks.size() > 1) {
			// add a single sink and connect it to the former sinks
			Activity end = new Activity("_end_");
			Gateway gate = new XorGateway();
			copy.addEdge(gate, end);
			for (FlowNode node:sinks)
				copy.addEdge(node, gate);
		}
		try{
			RPST<ControlFlow<FlowNode>, FlowNode> rpst = new RPST<ControlFlow<FlowNode>, FlowNode>(copy);
			return rpst.getRPSTNodes(TCType.RIGID).size() == 0;
		} catch (Exception e) {
			logger.warning("Error during structural check:" + e.getClass().getName() + ": " + e.getMessage());
			return false;
		}
	}
	
	/**
	 * Check if the given {@link ProcessModel} can be structured.
	 * @param process to structure
	 * @return structured {@link ProcessModel} if the given {@link ProcessModel}
	 * can be structured. Otherwise, <code>null</code>.
	 */
	public static ProcessModel structure(ProcessModel process) {
		//TODO implement
		return null;
	}
	
}
