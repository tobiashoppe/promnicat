/**
 * PromniCAT - Collection and Analysis of Business Process Models
 * Copyright (C) 2012 Cindy Fähnrich, Tobias Hoppe, Andrina Mascher
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General private License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General private License for more details.
 * 
 * You should have received a copy of the GNU General private License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.uni_potsdam.hpi.bpt.promnicat.util.modelConstants;

import org.jbpt.pm.bpmn.Bpmn;

/**
 * Methods for accessing all ids of a JSON Diagram for a {@link Bpmn} model
 * 
 * @author Tobias Hoppe
 */
public interface IBpmnConstants {
	
	/**
	 * @return the id for entitySubprocessEvent
	 */
	public String getEntitySubprocessEvent();

	/**
	 * @return the id for valueMultiinstance
	 */
	public String getValueMultiinstance();

	/**
	 * @return the id for entityTask
	 */
	public String getEntityTask();

	/**
	 * @return the id for valueNone
	 */
	public String getValueNone();

	/**
	 * @return the id for valueStandard
	 */
	public String getValueStandard();

	/**
	 * @return the id for valueTrue
	 */
	public String getValueTrue();

	/**
	 * @return the id for valueSequential
	 */
	public String getValueSequential();

	/**
	 * @return the id for valueDefault
	 */
	public String getValueDefault();

	/**
	 * @return the id for valueBidirected
	 */
	public String getValueBidirected();

	/**
	 * @return the id for valueUnidirected
	 */
	public String getValueUnidirected();

	/**
	 * @return the id for valueUndirected
	 */
	public String getValueUndirected();
	
	/**
	 * @return the id for eventEnd
	 */
	public String getEventEnd();

	/**
	 * @return the id for entityEventStart
	 */
	public String getEntityEventStart();

	/**
	 * @return the id for entityGatewayAlternative
	 */
	public String getEntityGatewayAlternative();

	/**
	 * @return the id for entityGatewayEventbased
	 */
	public String getEntityGatewayEventbased();

	/**
	 * @return the id for entityGatewayOr
	 */
	public String getEntityGatewayOr();

	/**
	 * @return the id for entityGatewayAnd
	 */
	public String getEntityGatewayAnd();

	/**
	 * @return the id for entityGatewayXor
	 */
	public String getEntityGatewayXor();

	/**
	 * @return the id for entitySubprocessCollapsed
	 */
	public String getEntitySubprocessCollapsed();

	/**
	 * @return the id for entitySubprocess
	 */
	public String getEntitySubprocess();

	/**
	 * @return the id for entityEventIntermediate
	 */
	public String getEntityEventIntermediate();

	/**
	 * @return the id for entityEventCatching
	 */
	public String getEntityEventCatching();

	/**
	 * @return the id for entityEventThrowing
	 */
	public String getEntityEventThrowing();

	/**
	 * @return the id for entityPool
	 */
	public String getEntityPool();

	/**
	 * @return the id for entityLane
	 */
	public String getEntityLane();

	/**
	 * @return the id for entityData
	 */
	public String getEntityData();

	/**
	 * @return the id for entitySequenceflow
	 */
	public String getEntitySequenceflow();

	/**
	 * @return the id for entityMessageflow
	 */
	public String getEntityMessageflow();

	/**
	 * @return the id for entityAssociation
	 */
	public String getEntityAssociation();

	/**
	 * @return the id for propertyIscompensation
	 */
	public String getPropertyIscompensation();

	/**
	 * @return the id for propertyLooptype
	 */
	public String getPropertyLooptype();

	/**
	 * @return the id for propertyMiOrder
	 */
	public String getPropertyMiOrder();

	/**
	 * @return the id for propertyAdhocOrder
	 */
	public String getPropertyAdhocOrder();

	/**
	 * @return the id for propertyTitle
	 */
	public String getPropertyTitle();

	/**
	 * @return the id for propertyDescription
	 */
	public String getPropertyDescription();

	/**
	 * @return the id for propertyName
	 */
	public String getPropertyName();

	/**
	 * @return the id for propertyConditionExpression
	 */
	public String getPropertyConditionExpression();

	/**
	 * @return the id for propertyConditionType
	 */
	public String getPropertyConditionType();

	/**
	 * @return the id for propertyIsadhoc
	 */
	public String getPropertyIsadhoc();
}
