/**
 * PromniCAT - Collection and Analysis of Business Process Models
 * Copyright (C) 2012 Cindy F�hnrich, Tobias Hoppe, Andrina Mascher
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
package de.uni_potsdam.hpi.bpt.promnicat.analysisModules.classification;

import java.util.logging.Logger;

import org.jbpt.petri.Flow;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.PetriNet;
import org.jbpt.petri.io.PNMLSerializer;

/**
 * This class provides methods to serialize/unserialize {@link PetriNet}s into/from PNML.
 * @author Tobias Hoppe
 *
 */
public class PetriNetSerializer {
	
	private static final Logger logger = Logger.getLogger(PetriNetSerializer.class.getName());

	/**
	 * @param petriNet {@link PetriNet} to serialize
	 * @return a byte array representing the PNML-Serialization of the given {@link PetriNet}.
	 */
	public static byte[] serialize(PetriNet petriNet) {
		try {
			return PNMLSerializer.serializePetriNet(transformToNetSystem(petriNet), PNMLSerializer.LOLA).getBytes("UTF-8");
		} catch (Exception e) {
			logger.warning("Petri Net could not be serialized due to the following error:\n" + e.getMessage());
		}
		return null;
	}
	
	/**
	 * Transforms the given {@link PetriNet} into a {@link NetSystem} and adds the natural
	 * markings(all source nodes are filled with one marking).
	 * @param petriNet {@link PetriNet} to transform
	 * @return The {@link NetSystem} of the given {@link PetriNet}.
	 */
	private static NetSystem transformToNetSystem(PetriNet petriNet) {
		NetSystem netSystem = new NetSystem();
		netSystem.addNodes(petriNet.getNodes());
		for(Flow edge : petriNet.getEdges()) {
			netSystem.addFreshFlow(edge.getSource(), edge.getTarget());
		}
		netSystem.loadNaturalMarking();
		return netSystem;
	}

	/**
	 * @param pnmlContent the PNML-content to unserialize
	 * @return The {@link PetriNet} unserialzed from the given PNML-content.
	 */
	public static PetriNet parsePetriNet(byte[] pnmlContent) {
		NetSystem netSystem = new PNMLSerializer().parse(pnmlContent);
		//transform net system to PetriNet
		PetriNet pn = new PetriNet();
		pn.addNodes(netSystem.getNodes());
		for(Flow edge : netSystem.getEdges()) {
			pn.addFreshFlow(edge.getSource(), edge.getTarget());
		}
		return pn;
	}
}
