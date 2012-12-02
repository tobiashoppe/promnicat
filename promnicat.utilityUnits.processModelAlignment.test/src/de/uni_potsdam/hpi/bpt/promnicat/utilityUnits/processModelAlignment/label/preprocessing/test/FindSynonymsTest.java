package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.preprocessing.test;

import java.util.HashSet;

import org.jbpt.alignment.LabelEntity;
import org.junit.Assert;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelAlignment.label.preprocessing.ReplaceSynonyms;

public class FindSynonymsTest extends PairwisePreProcessingStepTest<ReplaceSynonyms> {

	private static final ReplaceSynonyms synonyms = new ReplaceSynonyms();

	public FindSynonymsTest() {
		super(synonyms);
	}
	
	@Test
	public void testWordGroupInput() {
		String[] in = new String[]{"Process Order", "Work on Order"};
		String[] expected = new String[]{"Process Order", "Process on Order"};
		String[] out = synonyms.process(in[0], in[1]);
		Assert.assertArrayEquals(expected, out);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected HashSet<LabelEntity>[] getInputSets() {
		HashSet<LabelEntity> in1 = new HashSet<LabelEntity>();
		in1.add(new LabelEntity("employ"));
		in1.add(new LabelEntity("use"));
		in1.add(new LabelEntity("bar"));
		HashSet<LabelEntity> in2 = new HashSet<LabelEntity>();
		in2.add(new LabelEntity("use"));
		in2.add(new LabelEntity("employ"));
		in2.add(new LabelEntity("foo"));
		return new HashSet[]{in1,in2};
	}

	@SuppressWarnings("unchecked")
	@Override
	protected HashSet<LabelEntity>[] getExpectedSets() {
		HashSet<LabelEntity> in1 = new HashSet<LabelEntity>();
		in1.add(new LabelEntity("employ"));
		in1.add(new LabelEntity("use"));
		in1.add(new LabelEntity("bar"));
		HashSet<LabelEntity> in2 = new HashSet<LabelEntity>();
		in2.add(new LabelEntity("employ"));
		in2.add(new LabelEntity("use"));
		in2.add(new LabelEntity("foo"));
		return new HashSet[]{in1,in2};
	}

	@Override
	protected String[] getInputStrings() {
		return new String[] {"process", "work"};
	}

	@Override
	protected String[] getExpectedStrings() {
		return new String[] {"process", "process"};
	}

}
