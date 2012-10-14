package de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.tests;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.refactoring.StringOperations;

public class StringOperationsTest {

	private static StringOperations stringOperator;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		stringOperator = new StringOperations();
	}

	@Test
	public void testStringCapitalization() {
		assertEquals("Check availability", stringOperator.capitalize("check availability"));
	}

	@Test
	public void testStringSplitting() {
		String string = "\"depository ,  financial-institution\"";
		String[] expectedStrings = {"depository", "financial", "institution"};
		String[] actualStrings = stringOperator.tokenize(string);
		
		assertEquals(expectedStrings.length, actualStrings.length);
		for (int i = 0; i < expectedStrings.length; ++i )
			assertEquals(expectedStrings[i], actualStrings[i]);
	}
	
}
