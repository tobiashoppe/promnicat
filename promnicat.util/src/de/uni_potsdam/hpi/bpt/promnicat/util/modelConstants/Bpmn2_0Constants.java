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
package de.uni_potsdam.hpi.bpt.promnicat.util.modelConstants;

/**
 * Constants for id matching of a JSON Diagram for Bpmn 2.0
 * @author Cindy Fähnrich, Tobias Hoppe
 *
 */
public class Bpmn2_0Constants extends Bpmn1_1Constants {
	
	/*
	 * here, all constants differing from Bpmn 1.1 are reset to a new value
	 */
	private static final String ENTITY_SUBPROCESS_EVENT = "Event";
	private static final String ENTITY_GATEWAY_AND = "ParallelGateway";
	private static final String ENTITY_GATEWAY_OR = "InclusiveGateway";
	private static final String ENTITY_GATEWAY_XOR = "Exclusive_Databased_Gateway";
	private static final String ENTITY_GATEWAY_EVENTBASED = "EventbasedGateway";
	private static final String ENTITY_GATEWAY_ALTERNATIVE = "ComplexGateway";
	
	private static final String PROPERTY_ISCOMPENSATION = "isforcompensation";
	private static final String PROPERTY_MI_ORDER = "looptype";

	@Override
	public String getEntitySubprocessEvent() {
		return ENTITY_SUBPROCESS_EVENT;
	}

	@Override
	public String getEntityGatewayAnd() {
		return ENTITY_GATEWAY_AND;
	}

	@Override
	public String getPropertyMiOrder() {
		return PROPERTY_MI_ORDER;
	}

	@Override
	public String getPropertyIscompensation() {
		return PROPERTY_ISCOMPENSATION;
	}

	@Override
	public String getEntityGatewayAlternative() {
		return ENTITY_GATEWAY_ALTERNATIVE;
	}

	@Override
	public String getEntityGatewayEventbased() {
		return ENTITY_GATEWAY_EVENTBASED;
	}

	@Override
	public String getEntityGatewayXor() {
		return ENTITY_GATEWAY_XOR;
	}

	@Override
	public String getEntityGatewayOr() {
		return ENTITY_GATEWAY_OR;
	}
}

