package de.uni_potsdam.hpi.bpt.promnicat.correctionModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class(es) for collecting information about flaws in models 
 * 
 * @author Christian Kieschnick
 *
 */
public class Statistic
{
	/**
	 * represents the errors for a given model identified by its 
	 * identifier and its stencilset
	 * @author Christian Kieschnick
	 */
	private static class ModelResult {
		public String identifier;
		public String stencilSet;
		public GroupedValueResult result;	
	
		public ModelResult(String identifier, String stencilSet, GroupedValueResult result){
			this.identifier = identifier;
			this.result = result;
			this.stencilSet = stencilSet;
		}
	}
	
	/**
	 * HashMap<String, Integer> with convenience methods for increasing the values 
	 * @author Christian Kieschnick
	 */
	public static class SingleValueResult extends HashMap<String, Integer> {
		private static final long serialVersionUID = -2056973533602739519L;

		/**
		 * increase the value for a given key by increase
		 * or create the key value pair if its not existing
		 * @param key the key
		 * @param increase the value to increase by
		 */
		public void increase(String key, int increase){
			if (containsKey(key)){
				increase += get(key);
			}
			put(key, increase);
		}
		
		/**
		 * increase the value of the given key by 1
		 * @param key the key whose value should be increased
		 */
		public void increase(String key){
			this.increase(key, 1);
		}
	}
	
	/**
	 * HashMap<String, SingleValueResult> for grouping SingleValueResults
	 * and a readable output
	 * @author Christian Kieschnick
	 */
	public static class GroupedValueResult extends HashMap<String, SingleValueResult> {
		private static final long serialVersionUID = 2803525268673058731L;

		/**
		 * get the SingleValueResult
		 * initializes a new one if it is not existing
		 * @param key the key for the SingleValueResult
		 * @return the SingleValueResult
		 */
		public SingleValueResult initializingGet(String key){
			SingleValueResult result = null;
			if (!this.containsKey(key)){
				result = new SingleValueResult();
				this.put(key, result);
			} 
			else 
			{
				result = this.get(key);
			}
			return result;
		}
		
		/**
		 * print the content in a readable way
		 * @return a string representation
		 */
		public String prettyPrint(){
			String output = "";
			for (String key : keySet()){
				output += key +"\n";
				for (Map.Entry<String, Integer> pair : get(key).entrySet()){
					output += "\t"+pair.getKey()+": "+pair.getValue()+"\n";
				}
			}
			return output;
		}
	}
	
	
	/**
	 * correct models grouped by stencil set
	 */
	private SingleValueResult correctModelsByStencilSet = new SingleValueResult();
	
	/**
	 * flawed models grouped by stencil set
	 */
	private SingleValueResult flawedModelsByStencilSet = new SingleValueResult();

	/**
	 * unique flawed models grouped by stencil set
	 */
	private SingleValueResult uniqueFlawedModelsByStencilSet = new SingleValueResult();
	
	/**
	 * unique correct models grouped by stencil set
	 */
	private SingleValueResult uniqueCorrectModelsByStencilSet = new SingleValueResult();
	
	/**
	 * all resource errors collected by an detector grouped by stencil set
	 * (HashMap<Detector, HashMap<StencilSet, HashMap<Resource, Count>>>)
	 */
	private HashMap<String, GroupedValueResult> resourceErrorsByStencilSet = new HashMap<String, GroupedValueResult>();
	
	/**
	 * a list of all collected data for the models
	 */
	private List<ModelResult> results = new ArrayList<ModelResult>();
	
	/**
	 * add a model result to the list
	 * @param identifier identifies the model
	 * @param stencilSet the stencilset of the model
	 * @param result the collected data for the model
	 */
	public void addResult(String identifier, String stencilSet, GroupedValueResult result){
		results.add(new ModelResult(identifier, stencilSet, result));
	}
	
	/**
	 * evaluate the model data and interpret the collected information
	 * @param modelResult the collected information about the model
	 */
	private void evaluateModelResult(ModelResult modelResult){
		for (String detector : modelResult.result.keySet()){
			if (!resourceErrorsByStencilSet.containsKey(detector)){
				resourceErrorsByStencilSet.put(detector, new GroupedValueResult());
			}
			GroupedValueResult resourceByStencilSet = resourceErrorsByStencilSet.get(detector);
			SingleValueResult stencilErrors = modelResult.result.initializingGet(detector);
			SingleValueResult resourceErrors = resourceByStencilSet.initializingGet(modelResult.stencilSet);
			for (String stencil : stencilErrors.keySet()){
				resourceErrors.increase(stencil, stencilErrors.get(stencil));
			}
		}
	}
	
	/**
	 * collect and interpret all collected model information
	 */
	private void calculateStatistics(){
		for (ModelResult model : results){
			if (model.result.isEmpty()){
				uniqueCorrectModelsByStencilSet.increase(model.identifier);
				correctModelsByStencilSet.increase(model.stencilSet);
			} else {
				uniqueFlawedModelsByStencilSet.increase(model.identifier);
				flawedModelsByStencilSet.increase(model.stencilSet);
				evaluateModelResult(model);
			}
		}
	}
	
	/**
	 * print the content in a readable way
	 * @return a string representation
	 */
	private String prettyPrint(){
		String output = "Flawed models"+"\t" + (uniqueFlawedModelsByStencilSet.size()) +"\t"+"\t"+ "\n";
		for (String stencilSet : flawedModelsByStencilSet.keySet()){
			output += 					"\t"+ 	stencilSet+	"\t"+ flawedModelsByStencilSet.get(stencilSet)+"\t"+"\n";
		}
		output += 		"Correct models"+"\t" + (uniqueCorrectModelsByStencilSet.size()) +"\t"+"\t'"+ "\n";
		for (String stencilSet : correctModelsByStencilSet.keySet()){
			output += 					 "\t"+stencilSet+	"\t"+ correctModelsByStencilSet.get(stencilSet)+ "\t"+"\n";
		}
		output += "\t"+"\t"+"\t"+"\n";
		for (String detector : resourceErrorsByStencilSet.keySet()){
			output += detector +"\t"+"\t"+"\t"+"\n";
			GroupedValueResult errorsByStencilSet  = resourceErrorsByStencilSet.get(detector);
			for (String stencilSet : errorsByStencilSet.keySet()){
				output += "\t"+stencilSet+"\t"+"\t"+"\n";
				SingleValueResult resourceErrors = errorsByStencilSet.get(stencilSet);
				for (String resource : resourceErrors.keySet()){
					output += "\t"+"\t"+resource+"\t"+resourceErrors.get(resource)+"\n";
				}
			}
		}
		return output;
	}

	/**
	 * interpret collected information and return them in a readable way
	 */
	@Override
	public String toString(){
		calculateStatistics();
		return prettyPrint();
	}
}
