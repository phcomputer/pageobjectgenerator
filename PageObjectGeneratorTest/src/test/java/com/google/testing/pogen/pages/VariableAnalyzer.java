package com.google.testing.pogen.pages;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;

public class VariableAnalyzer {
	private static Pattern commentPattern = Pattern
			.compile("<!--POGEN,([^,]*),([^,]*),(.*?)-->");

	public static Map<String, String> getVariableTexts(WebDriver driver) {
		HashMap<String, String> result = new HashMap<String, String>();
		Matcher matcher = commentPattern.matcher(driver.getPageSource());
		while (matcher.find()) {
			result.put(matcher.group(1), matcher.group(3));
		}
		return result;
	}
}
