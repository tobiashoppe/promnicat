package de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.tests;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.refactoring.StringOperations;

public class CapitalizeStringTest {

	private static StringOperations stringOperator;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		stringOperator = new StringOperations();
	}

	@Test
	public void testStringCapitalization() {
		 assertEquals("Check availability", stringOperator.capitalize("check availability"));
	}

}
