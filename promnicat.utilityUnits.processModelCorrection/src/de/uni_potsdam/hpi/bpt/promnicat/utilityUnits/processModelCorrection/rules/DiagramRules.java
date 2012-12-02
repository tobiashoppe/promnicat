package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.rules;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.uni_potsdam.hpi.bpt.ai.diagram.Diagram;
import de.uni_potsdam.hpi.bpt.ai.diagram.Edge;
import de.uni_potsdam.hpi.bpt.ai.diagram.Node;
import de.uni_potsdam.hpi.bpt.ai.diagram.Shape;
import de.uni_potsdam.hpi.bpt.ai.diagram.StencilSet;
import de.uni_potsdam.hpi.bpt.ai.diagram.StencilSetFactory;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.UnsupportedModelException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.wrapper.ShapeWrapper;


/**
 * Abstraction from the current used model defining the fundamental elements and the rules 
 * for the interaction of the element
 * @author Christian Kieschnick
 */
public abstract class DiagramRules  {
	private final static Logger logger = Logger.getLogger(DiagramRules.class.getName());
	/**
	 * map for rules to cache instanciated maps
	 */
	private static HashSet<DiagramRules> ruleSets;
	
	/**
	 * the underlying stencilset for the current ruleset
	 */
	private StencilSet stencilSet;
	
	/**
	 * mappings between a stencil and the roles in which it can occur
	 */
	private StencilRoleMapping stencilRoleMapping = new StencilRoleMapping();
	protected HashSet<Connection> roleToRoleConnection = new HashSet<Connection>();

	/**
	 * determine if we have a matching rule set for correcting the given model
	 * @param model
	 * @return
	 */
	private boolean canHandle(Diagram model){
		boolean canHandle = getSupportedStencilSet().contentEquals(model.getStencilset().getNamespace());
		
		if (canHandle){
			for (String neededExtension : model.getSsextensions()){
				canHandle &= getSupportedStencilSetExtensions().contains(neededExtension);
			}
		}
		return canHandle;
	}
	
	/**
	 * fetch the correct rule set for the model
	 * @param model 
	 * @return the matching rule set for further operation
	 * @throws UnsupportedModelException in case we do not have any rule set
	 */
	public static DiagramRules of(Diagram model) throws UnsupportedModelException{
		if (ruleSets == null){
			ruleSets = new HashSet<DiagramRules>();
			try {
				ruleSets.add(new BpmnDiagramRules.Bpmn1_1());
				ruleSets.add(new BpmnDiagramRules.Bpmn2_0());
				ruleSets.add(new EpcDiagramRules());
			} catch (Exception e) {
				logger.warning("Could not initialize model rules");
			}
		}
		
		for (DiagramRules rules : ruleSets){
			if (rules.canHandle(model)){
				return rules;
			}
		}
		throw new UnsupportedModelException("Model "+model.getStencilset().getNamespace()+" can not be corrected "+model.getPath());
	}
	
	/**
	 * the rule set which is implemented by the subclass
	 * @return
	 */
	public abstract String getSupportedStencilSet();
	
	/**
	 * the extensions which can be handled be the subclass
	 * @return
	 */
	public abstract HashSet<String> getSupportedStencilSetExtensions();
		
	protected DiagramRules() throws UnsupportedModelException{
		try {
			this.stencilSet = StencilSetFactory.getInstance().getStencilSet(getSupportedStencilSet());
		

		JSONArray stencils = stencilSet.getSpecification().getJSONArray("stencils");
		JSONObject rules = stencilSet.getSpecification().getJSONObject("rules");
		JSONArray connectionRules = rules.getJSONArray("connectionRules");
//		JSONArray containmentRules = rules.getJSONArray("containmentRules");

		initializeStencilRoleMappings(stencils);
		initializeRoleToRoleConnections(connectionRules);
		
		} catch (UnsupportedEncodingException e) {
			throw new UnsupportedModelException("Could not read rules for "+stencilSet.getNamespace(), e);
		} catch (FileNotFoundException e) {
			throw new UnsupportedModelException("Could not find rules for "+stencilSet.getNamespace(), e);
		} catch (JSONException e) {
			throw new UnsupportedModelException("Could not parse rules for "+stencilSet.getNamespace(), e);
		} catch (IOException e) {
			throw new UnsupportedModelException("Could not open rules for "+stencilSet.getNamespace(), e);
		}
	}
	
	/**
	 * determines if the shape is an edge
	 * @param shape
	 * @return
	 */
	public boolean isEdge(Shape shape){
		return shape instanceof Edge;
	}
	
	/**
	 * determines if the shape is a node
	 * @param shape
	 * @return
	 */
	public boolean isNode(Shape shape){
		return shape instanceof Node;
	}
	
	public boolean isControlFlowNode(ShapeWrapper shape){
		return true;
	}
	
	/**
	 * determine if the shape needs an incoming control flow
	 * @return true if it needs an incoming control flow
	 */
	public abstract boolean needsIncomingControlFlow(ShapeWrapper shape);
	
	/**
	 * determine if the shape needs an outgoing control flow
	 * @return true if it needs an outgoing control flow
	 */
	public abstract boolean needsOutgoingControlFlow(ShapeWrapper shape);
	
	/**
	 * determine if the shape needs at least one control flow connection
	 * @return true if at least an incoming or outgoing control flow is needed
	 */
	public boolean needsAtLeastOneControlFlowConnection(ShapeWrapper shape){
		return true;
	}
	
	/**
	 * determine if the edge is an control flow element 
	 * @return true if the edge is an control flow element
	 */
	public boolean isControlFlowEdge(ShapeWrapper shape){
		return true;
	}
	
	/**
	 * determine if the edge does not differ between source and target
	 * @return true if the shape does not differ between source and target
	 */
	public boolean isUndirectedEdge(ShapeWrapper shape){
		// this informaton can possibly be extracted form the specification using the connection rules  
		return false;
	}

	/**
	 * implements a basic rule set for connections between nodes and edges
	 * @param from
	 * @param to
	 * @return if the combination is valid (or true in case the combination is unknown)
	 */
	public boolean canBeConnected(ShapeWrapper from, ShapeWrapper to, ShapeWrapper by){
		for (Connection connection : roleToRoleConnection){
			if (canBeConnected(from, to, by, connection)
					&& connectionCanBeAppliedTo(from, to, by.getStencilId())){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * test if a connection can be applied between to nodes for a specific environment
	 * @param from
	 * @param to
	 * @param by
	 * @param using
	 * @return
	 */
	protected boolean canBeConnected(ShapeWrapper from, ShapeWrapper to, ShapeWrapper by, Connection using){
		Collection<String> fromRoles = getRoles(from);
		Collection<String> toRoles = getRoles(to);
		Collection<String> byRoles = getRoles(by);
		return (fromRoles.contains(using.from) || fromRoles.isEmpty())
				&& (toRoles.contains(using.to) || toRoles.isEmpty())
				&& (byRoles.contains(using.by) || byRoles.isEmpty());
	}
	
	/**
	 * test if a connection can be applied for the specific source and target
	 * @param from
	 * @param to
	 * @param byStencilId
	 * @return true if the connection can be applied
	 */
	public boolean connectionCanBeAppliedTo(ShapeWrapper from, ShapeWrapper to, String byStencilId){
		return true;
	}
	
	/**
	 * delivers a list of connections which can be applied for from and to nodes
	 * TODO at this point a rating according to the distribution of the stencils would be an improvement
	 * @param from
	 * @param to
	 * @return
	 */
	public List<String> getSupportedConnectionFor(ShapeWrapper from, ShapeWrapper to) {
		ArrayList<String> possibleConnections = new ArrayList<String>();
		for (Connection connection : roleToRoleConnection){
			if (canBeConnected(from, to, null, connection)){
				Collection<String> temp = stencilRoleMapping.getStencilIds(connection.by);
				possibleConnections.addAll(temp);
			}
		}
		return possibleConnections;
	}
	
	/**
	 * Mapping between roles and the particular nodes
	 * @author Christian Kieschnick
	 */
	private static class StencilRoleMapping {
		
		private HashMap<String, HashSet<String>> stencilIdToRole = new HashMap<String, HashSet<String>>();
		private HashMap<String, HashSet<String>> roleToStencilId = new HashMap<String, HashSet<String>>();

		/**
		 * add a new association
		 * @param stencilId
		 * @param role
		 */
		public void add(String stencilId, String role){
			if (!stencilIdToRole.containsKey(stencilId)){
				stencilIdToRole.put(stencilId, new HashSet<String>());
			} 
			stencilIdToRole.get(stencilId).add(role);
			if (!roleToStencilId.containsKey(role)){
				roleToStencilId.put(role, new HashSet<String>());
			} 
			roleToStencilId.get(role).add(stencilId);
		}
		
		/**
		 * find all values of M associated with the lookup 
		 * @param lookup role
		 * @return a collection of associated StencilIds
		 */
		protected Collection<String> getStencilIds(String role){
			return roleToStencilId.get(role);
		}
		
		/**
		 * find all roles associated with the lookup stencilId
		 * @param lookup stencilId
		 * @return a collection of associated roles including the stencilId itself
		 */
		protected Collection<String> getRoles(String stencilId){
			if (stencilIdToRole.containsKey(stencilId)){
				return stencilIdToRole.get(stencilId);
			}
			ArrayList<String> fallbackRoles = new ArrayList<String>(); //TODO verify if correct
			fallbackRoles.add(stencilId);
			return fallbackRoles;
		}
	}
	
	/**
	 * Mapping for associating connections with their corresponding node roles
	 * @author Christian Kieschnick
	 *
	 */
	protected static class Connection {
		public String by;
		public String from;
		public String to;
		
		public Connection(String stencilId, String from, String to){
			this.by = stencilId;
			this.from = from;
			this.to = to;
		}
		
		@Override
		public String toString(){
			return by+": "+from+" -> "+to;
		}
	}
	
	/**
	 * extract the possible connections and store them in roleToRoleConnection
	 * @param connectionRules
	 * @throws JSONException
	 */
	private void initializeRoleToRoleConnections(JSONArray connectionRules) throws JSONException {
		for (int i = 0; i < connectionRules.length(); i++){
			JSONObject connectionRule = connectionRules.getJSONObject(i);
			String role = connectionRule.getString("role");
			JSONArray connections = connectionRule.getJSONArray("connects");
			for (int j = 0; j < connections.length(); j++){
				JSONObject connection = connections.getJSONObject(j);
				if (!(connection.get("to") instanceof JSONArray)){
					roleToRoleConnection.add(new Connection(role, connection.getString("from"), connection.getString("to")));
				} else {
					for (int k = 0; k < connection.getJSONArray("to").length(); k++){
						roleToRoleConnection.add(new Connection(role, connection.getString("from"), connection.getJSONArray("to").getString(k)));
					}
				}
			}
		}
	}

	/**
	 * extract the stencils with their roles and store them in stencilRoleMapping
	 * @param stencils
	 * @throws JSONException
	 */
	private void initializeStencilRoleMappings(JSONArray stencils) throws JSONException {
		for (int i = 0; i < stencils.length(); i++){
			JSONObject stencil = stencils.getJSONObject(i);
			String stencilId = stencil.getString("id");
			JSONArray roles = stencil.getJSONArray("roles");
			stencilRoleMapping.add(stencilId, stencilId);
			for (int j = 0; j < roles.length(); j++){
				stencilRoleMapping.add(stencilId, roles.getString(j));
			}
		}
	}
	
	/**
	 * get the roles of a shape
	 * @param shape
	 * @return
	 */
	protected Collection<String> getRoles(ShapeWrapper shape){
		if (shape != null){
			return stencilRoleMapping.getRoles(shape.getStencilId());
		}
		return new ArrayList<String>();
	}

	/**
	 * test if one node can contain another
	 * @param container
	 * @param innerShape
	 * @return
	 */
	public boolean canContain(Shape container, Shape innerShape) {
		return true;
	}
}
