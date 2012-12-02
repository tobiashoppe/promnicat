package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.preprocessing.test;

import java.util.HashSet;

import org.jbpt.alignment.LabelEntity;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.preprocessing.ToLowerCase;

public class ToLowerCaseTest extends SinglePreProcessingStepTest<ToLowerCase> {

	public ToLowerCaseTest() {
		super(new ToLowerCase());
	}

	@Override
	protected HashSet<LabelEntity> getInputCollection() {
		HashSet<LabelEntity> in = new HashSet<LabelEntity>();
		in.add(new LabelEntity("Foo"));
		in.add(new LabelEntity("BAR"));
		in.add(new LabelEntity("wAyNE"));
		return in;
	}

	@Override
	protected HashSet<LabelEntity> getExpectedCollection() {
		HashSet<LabelEntity> in = new HashSet<LabelEntity>();
		in.add(new LabelEntity("foo"));
		in.add(new LabelEntity("bar"));
		in.add(new LabelEntity("wayne"));
		return in;
	}

	@Override
	protected String getInputString() {
		return "FooBar";
	}

	@Override
	protected String getExpectedString() {
		return "foobar";
	}

}
