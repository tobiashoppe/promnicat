package de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.label.preprocessing.test;

import java.util.HashSet;

import org.jbpt.alignment.LabelEntity;

import de.uni_potsdam.hpi.bpt.promnicat.processModelAlignment.label.preprocessing.PorterStemming;

public class PorterStemmingTest extends SinglePreProcessingStepTest<PorterStemming> {

	public PorterStemmingTest() {
		super(new PorterStemming());
	}

	@Override
	protected HashSet<LabelEntity> getInputCollection() {
		HashSet<LabelEntity> in = new HashSet<LabelEntity>();
		in.add(new LabelEntity("receive order"));
		in.add(new LabelEntity("processing order"));
		in.add(new LabelEntity("maximize throughput"));
		return in;
	}

	@Override
	protected HashSet<LabelEntity> getExpectedCollection() {
		HashSet<LabelEntity> in = new HashSet<LabelEntity>();
		in.add(new LabelEntity("receiv order"));
		in.add(new LabelEntity("process order"));
		in.add(new LabelEntity("maxim throughput"));
		return in;
	}

	@Override
	protected String getInputString() {
		return "verification";
	}

	@Override
	protected String getExpectedString() {
		return "verif";
	}

}
