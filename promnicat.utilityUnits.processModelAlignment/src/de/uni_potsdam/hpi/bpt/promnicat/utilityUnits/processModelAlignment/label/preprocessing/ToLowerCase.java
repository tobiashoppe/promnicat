package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.preprocessing;

public class ToLowerCase extends SinglePreProcessingStep {

	/** @return in.toLowerCase() */
	@Override
	public String process(String in) {
		return in.toLowerCase();
	}

}
