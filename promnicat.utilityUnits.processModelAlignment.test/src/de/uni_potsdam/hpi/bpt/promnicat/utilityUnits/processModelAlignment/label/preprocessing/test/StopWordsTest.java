package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.preprocessing.test;

import java.util.HashSet;

import org.jbpt.alignment.LabelEntity;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.preprocessing.StopWords;

public class StopWordsTest extends SinglePreProcessingStepTest<StopWords> {

	public StopWordsTest() {
		super(new StopWords());
	}

	@Override
	protected HashSet<LabelEntity> getInputCollection() {
		HashSet<LabelEntity> in = new HashSet<LabelEntity>();
		in.add(new LabelEntity("me and you"));
		in.add(new LabelEntity("to order"));
		in.add(new LabelEntity("process order"));
		return in;
	}

	@Override
	protected HashSet<LabelEntity> getExpectedCollection() {
		HashSet<LabelEntity> in = new HashSet<LabelEntity>();
		in.add(new LabelEntity(""));
		in.add(new LabelEntity("order"));
		in.add(new LabelEntity("process order"));
		return in;
	}

	@Override
	protected String getInputString() {
		return "i am legend";
	}

	@Override
	protected String getExpectedString() {
		return "legend";
	}

}
