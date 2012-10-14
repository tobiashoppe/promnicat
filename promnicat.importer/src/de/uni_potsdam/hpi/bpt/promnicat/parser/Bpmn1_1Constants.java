/**
 * PromniCAT - Collection and Analysis of Business Process Models
 * Copyright (C) 2012 Cindy Fähnrich, Tobias Hoppe, Andrina Mascher
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General private   License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General private   License for more details.
 * 
 * You should have received a copy of the GNU General private   License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.uni_potsdam.hpi.bpt.promnicat.parser;


/**
 * Constants for id matching of a JSON Diagram for Bpmn 1.1
 * @author Cindy Fähnrich, Tobias Hoppe
 *
 */
public class Bpmn1_1Constants implements IBpmnConstants{

	/**
	 * Activities
	 */
	private static final String ENTITY_TASK = "Task";
	private static final String ENTITY_SUBPROCESS = "Subprocess";
	private static final String ENTITY_SUBPROCESS_EVENT = " "; //no eventbased subprocess exists in Bpmn 1.1
	private static final String ENTITY_SUBPROCESS_COLLAPSED = "Collapsed";
	
	/**
	 * Gateways
	 */
	private static final String ENTITY_GATEWAY_AND = "AND_Gateway";
	private static final String ENTITY_GATEWAY_OR = "OR_Gateway";
	private static final String ENTITY_GATEWAY_XOR = "Exclusive_Databased_Gateway";
	private static final String ENTITY_GATEWAY_EVENTBASED = "Eventbased_Gateway";
	private static final String ENTITY_GATEWAY_ALTERNATIVE = "Complex_Gateway";
	
	/**
	 * Events - Start
	 */
	private static final String ENTITY_EVENT_START = "Start";
	
	/**
	 * Events - Intermediate
	 * catching and throwing events contain this term also, so it is checked only at the end
	 */
	private static final String ENTITY_EVENT_INTERMEDIATE = "Intermediate"; 
	private static final String ENTITY_EVENT_CATCHING = "Catching";
	private static final String ENTITY_EVENT_THROWING = "Throwing";
	
	/**
	 * Events - End
	 */
	private static final String EVENT_END = "End";
	
	/**
	 * Resources and Data
	 */
	private static final String ENTITY_POOL = "Pool";
	private static final String ENTITY_LANE = "Lane";
	private static final String ENTITY_DATA = "DataObject";
	
	/**
	 * Edges
	 */
	private static final String ENTITY_SEQUENCEFLOW = "SequenceFlow";
	private static final String ENTITY_ASSOCIATION = "Association";
	private static final String ENTITY_MESSAGEFLOW = "MessageFlow";
	
	/**
	 * Json Property names
	 */
	private static final String PROPERTY_ISCOMPENSATION = "iscompensation";
	private static final String PROPERTY_LOOPTYPE = "looptype";
	private static final String PROPERTY_MI_ORDER = "mi_ordering";
	private static final String PROPERTY_ISADHOC= "isadhoc";
	private static final String PROPERTY_ADHOC_ORDER = "adhocordering";
	private static final String PROPERTY_CONDITION_TYPE = "conditiontype";
	private static final String PROPERTY_CONDITION_EXPRESSION = "conditionexpression";
	private static final String PROPERTY_NAME = "name";
	private static final String PROPERTY_DESCRIPTION = "description";
	private static final String PROPERTY_TITLE = "title";
	
	/**
	 * Json Property values
	 */
	private static final String VALUE_MULTIINSTANCE = "MultiInstance";
	private static final String VALUE_UNDIRECTED = "Undirected";
	private static final String VALUE_UNIDIRECTED = "Unidirectional";
	private static final String VALUE_BIDIRECTED = "Bidirected";
	private static final String VALUE_DEFAULT = "Default";
	private static final String VALUE_SEQUENTIAL = "Sequential";
	private static final String VALUE_TRUE = "true";
	private static final String VALUE_STANDARD = "Standard";
	private static final String VALUE_NONE = "None";
	
	@Override
	public String getEntitySubprocessEvent() {
		return ENTITY_SUBPROCESS_EVENT;
	}

	@Override
	public String getValueMultiinstance() {
		return VALUE_MULTIINSTANCE;
	}

	@Override
	public String getEntityTask() {
		return ENTITY_TASK;
	}

	@Override
	public String getValueNone() {
		return VALUE_NONE;
	}

	@Override
	public String getValueStandard() {
		return VALUE_STANDARD;
	}

	@Override
	public String getValueTrue() {
		return VALUE_TRUE;
	}

	@Override
	public String getValueSequential() {
		return VALUE_SEQUENTIAL;
	}

	@Override
	public String getValueDefault() {
		return VALUE_DEFAULT;
	}

	@Override
	public String getValueBidirected() {
		return VALUE_BIDIRECTED;
	}

	@Override
	public String getValueUnidirected() {
		return VALUE_UNIDIRECTED;
	}

	@Override
	public String getValueUndirected() {
		return VALUE_UNDIRECTED;
	}

	@Override
	public String getEventEnd() {
		return EVENT_END;
	}

	@Override
	public String getEntityEventStart() {
		return ENTITY_EVENT_START;
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
	public String getEntityGatewayOr() {
		return ENTITY_GATEWAY_OR;
	}

	@Override
	public String getEntityGatewayAnd() {
		return ENTITY_GATEWAY_AND;
	}

	@Override
	public String getEntityGatewayXor() {
		return ENTITY_GATEWAY_XOR;
	}

	@Override
	public String getEntitySubprocessCollapsed() {
		return ENTITY_SUBPROCESS_COLLAPSED;
	}

	@Override
	public String getEntitySubprocess() {
		return ENTITY_SUBPROCESS;
	}

	@Override
	public String getEntityEventIntermediate() {
		return ENTITY_EVENT_INTERMEDIATE;
	}

	@Override
	public String getEntityEventCatching() {
		return ENTITY_EVENT_CATCHING;
	}

	@Override
	public String getEntityEventThrowing() {
		return ENTITY_EVENT_THROWING;
	}

	@Override
	public String getEntityPool() {
		return ENTITY_POOL;
	}

	@Override
	public String getEntityLane() {
		return ENTITY_LANE;
	}

	@Override
	public String getEntityData() {
		return ENTITY_DATA;
	}

	@Override
	public String getEntitySequenceflow() {
		return ENTITY_SEQUENCEFLOW;
	}

	@Override
	public String getEntityMessageflow() {
		return ENTITY_MESSAGEFLOW;
	}

	@Override
	public String getEntityAssociation() {
		return ENTITY_ASSOCIATION;
	}

	@Override
	public String getPropertyIscompensation() {
		return PROPERTY_ISCOMPENSATION;
	}

	@Override
	public String getPropertyLooptype() {
		return PROPERTY_LOOPTYPE;
	}

	@Override
	public String getPropertyMiOrder() {
		return PROPERTY_MI_ORDER;
	}

	@Override
	public String getPropertyAdhocOrder() {
		return PROPERTY_ADHOC_ORDER;
	}

	@Override
	public String getPropertyTitle() {
		return PROPERTY_TITLE;
	}

	@Override
	public String getPropertyDescription() {
		return PROPERTY_DESCRIPTION;
	}

	@Override
	public String getPropertyName() {
		return PROPERTY_NAME;
	}

	@Override
	public String getPropertyConditionExpression() {
		return PROPERTY_CONDITION_EXPRESSION;
	}

	@Override
	public String getPropertyConditionType() {
		return PROPERTY_CONDITION_TYPE;
	}

	@Override
	public String getPropertyIsadhoc() {
		return PROPERTY_ISADHOC;
	}
}
