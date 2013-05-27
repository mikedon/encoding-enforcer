package com.miked.maven.enforcer.rule;

import static org.junit.Assert.assertTrue;

import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.plugins.enforcer.EnforcerTestUtils;
import org.junit.Test;

public class EncodingRuleTest {

	@Test
	public void testValidFile() {
		testFile(true, "UTF-8", "utf-8.txt");
	}

	@Test
	public void testInvalidFile() {
		testFile(false, "UTF-8", "iso.txt");
	}

	@Test
	public void testAsciiFile() {
		testFile(true, "UTF-8", "ascii.txt");
	}

	protected void testFile(boolean expected, String encoding, String file) {
		boolean isValid;
		EncodingRule rule = new EncodingRule();
		rule.setEncoding(encoding);
		rule.setDirectory("/src/test/resources/");
		rule.setIncludes(file);
		try {
			rule.execute(EnforcerTestUtils.getHelper());
			isValid = true;
		} catch (EnforcerRuleException e) {
			isValid = false;
		}
		assertTrue(isValid == expected);
	}
}
