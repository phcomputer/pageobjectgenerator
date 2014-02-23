package com.google.testing.pogen.pages;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

public aspect OracleGenerator {
	pointcut driverMethodPointcut(WebDriver driver):
		call(* WebDriver.*(..)) && target(driver) && !this(Selenium) && !this(OracleGenerator) && !this(VariableAnalyzer);

	private static String lastName;
	private static int index;
	private static Map<String, List<Map<String, String>>> testName2VariableMapList = new HashMap<String, List<Map<String, String>>>();

	private static void setTestName2VariableMapList(
			Map<String, List<Map<String, String>>> map) {
		testName2VariableMapList = map;
	}

	before(WebDriver driver): driverMethodPointcut(driver) {
		if (thisEnclosingJoinPointStaticPart.getSignature() instanceof MethodSignature) {
			MethodSignature ms = (MethodSignature) thisEnclosingJoinPointStaticPart
					.getSignature();
			Method method = ms.getMethod();
			Test test = method.getAnnotation(Test.class);
			if (test != null) {
				String name = method.getDeclaringClass().getName() + "/"
						+ method.getName();
				if (!name.equals(lastName)) {
					lastName = name;
					index = 0;
				} else {
					index++;
				}
				Map<String, String> variableTexts = VariableAnalyzer
						.getVariableTexts(driver);
				if (testName2VariableMapList.containsKey(lastName)) {
					Map<String, String> variableMap = testName2VariableMapList
							.get(lastName).get(index);
					assertThat(variableTexts.size(), is(variableMap.size()));
					for (String key : variableTexts.keySet()) {
						assertThat(variableTexts.get(key),
								is(variableMap.get(key)));
					}
				} else {
					if (index == 0) {
						System.out.println("List<Map<String, String>> maps = new List<Map<String, String>>();");
						System.out.println("Map<String, String> map = new HashMap<String, String>();");
					}
					for (Entry<String, String> kv : variableTexts.entrySet()) {
						System.out.println("map.put(\"" + kv.getKey()
								+ "\", \"" + kv.getValue() + "\")");
					}
				}
			}
		}
	}
}
