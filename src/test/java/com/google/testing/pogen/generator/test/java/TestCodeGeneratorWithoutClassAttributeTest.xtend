// Copyright 2011-2013 The PageObjectGenerator Authors.
// Copyright 2011 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS-IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.google.testing.pogen.generator.test.java

import com.google.testing.pogen.generator.template.TemplateUpdater
import com.google.testing.pogen.generator.template.TemplateUpdaters
import com.google.testing.pogen.parser.template.soy.SoyParser
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

import static org.junit.Assert.*

/**
 * Tests for {@link TestCodeGenerator}.
 *
 * @author Kazunori Sakamoto
 */
@RunWith(typeof(JUnit4))
class TestCodeGeneratorWithClassAttributeTest {

	private TemplateUpdater updater
	private SoyParser parser
	private TestCodeGenerator generator

	@Before
	def void setUp() {
		val attributeName = "id"
		updater = TemplateUpdaters.getPreferredUpdater(attributeName, "_")
		parser = new SoyParser(attributeName)
		generator = new TestCodeGenerator(attributeName, "  ", "\n")
	}

	def String generateClass(String body) '''
		package test;
		
		import static org.junit.Assert.*;
		import static org.hamcrest.Matchers.*;
		
		import org.openqa.selenium.By;
		import org.openqa.selenium.WebDriver;
		import org.openqa.selenium.WebElement;
		import org.openqa.selenium.support.FindBy;
		import org.openqa.selenium.support.How;
		
		import java.util.ArrayList;
		import java.util.HashMap;
		import java.util.List;
		import java.util.regex.Matcher;
		import java.util.regex.Pattern;
		
		public class TestPage extends AbstractPage {
			public TestPage(WebDriver driver) {
				super(driver);
				assertInvariant();
			}
			
			private void assertInvariant() {
			}
			
			«TestCodeGenerator.GENERATED_CODE_START_MARK»
			private static Pattern commentPattern = Pattern.compile("<!--POGEN,([^,]*),([^,]*),(.*?)-->", Pattern.DOTALL);
			
			«body»
			«TestCodeGenerator.GENERATED_CODE_END_MARK»
		}
	'''

	def normalizeCode(String code) {
		code.replace("	", "  ").replace("\r\n", "\n")
	}

	@Test
	def generateForAnEnclosure() {
		val templateInfo = parser.parse("<html><head><title>{$title}</title></head><body>test</body></html>")
		updater.generate(templateInfo)
		val actual = generator.generate(templateInfo, "test", "Test")
		val expected = generateClass(
			'''
				@FindBy(how = How.XPATH, using = "//*[@id='_0']")
				private WebElement title;
				
				public WebElement getElementOfTitle() {
					return title;
				}
				
				public String getTextOfTitle() {
					Matcher matcher = commentPattern.matcher(driver.getPageSource());
					while (matcher.find()) {
						if (matcher.group(1).equals("_0") && matcher.group(2).equals("title")) {
							return matcher.group(3);
						}
					}
					return null;
				}
			''').normalizeCode()
		assertEquals(expected, actual)
	}

	@Test
	def generateForAnEnclosure2() {
		val templateInfo = parser.parse("<html><body>{$content}</body></html>")
		updater.generate(templateInfo)
		val actual = generator.generate(templateInfo, "test", "Test")
		val expected = generateClass(
			'''
				@FindBy(how = How.XPATH, using = "//*[@id='_0']")
				private WebElement content;
				
				public WebElement getElementOfContent() {
					return content;
				}
				
				public String getTextOfContent() {
					Matcher matcher = commentPattern.matcher(driver.getPageSource());
					while (matcher.find()) {
						if (matcher.group(1).equals("_0") && matcher.group(2).equals("content")) {
							return matcher.group(3);
						}
					}
					return null;
				}
			''').normalizeCode()

		assertEquals(expected, actual)
	}

	@Test
	def generateForAnEnclosureUsingProhibittedChars() {
		val templateInfo = parser.parse("<html><head><title>{$p.title}</title></head><body>test</body></html>")
		updater.generate(templateInfo)
		val actual = generator.generate(templateInfo, "test", "Test")
		val expected = generateClass(
			'''
				@FindBy(how = How.XPATH, using = "//*[@id='_0']")
				private WebElement p_dot_title;
				
				public WebElement getElementOfP_dot_title() {
					return p_dot_title;
				}
				
				public String getTextOfP_dot_title() {
					Matcher matcher = commentPattern.matcher(driver.getPageSource());
					while (matcher.find()) {
						if (matcher.group(1).equals("_0") && matcher.group(2).equals("p_dot_title")) {
							return matcher.group(3);
						}
					}
					return null;
				}
			''').normalizeCode()

		assertEquals(expected, actual)
	}

	@Test
	def generateForAnEnclosureUsingOption() {
		val templateInfo = parser.parse("<html><head><title>{$title|escapeUri}</title></head><body>test</body></html>")
		updater.generate(templateInfo)
		val actual = generator.generate(templateInfo, "test", "Test")
		val expected = generateClass(
			'''
				@FindBy(how = How.XPATH, using = "//*[@id='_0']")
				private WebElement title;
				
				public WebElement getElementOfTitle() {
					return title;
				}
				
				public String getTextOfTitle() {
					Matcher matcher = commentPattern.matcher(driver.getPageSource());
					while (matcher.find()) {
						if (matcher.group(1).equals("_0") && matcher.group(2).equals("title")) {
							return matcher.group(3);
						}
					}
					return null;
				}
			''').normalizeCode()

		assertEquals(expected, actual)
	}

	@Test
	def generateForAnEnclosureContainingTwoDiffVars() {
		val templateInfo = parser.parse("<html><body>{$content1}{$content2}</body></html>")
		updater.generate(templateInfo)
		val actual = generator.generate(templateInfo, "test", "Test")
		val expected = generateClass(
			'''
				@FindBy(how = How.XPATH, using = "//*[@id='_0']")
				private WebElement content1;
				
				public WebElement getElementOfContent1() {
					return content1;
				}
				
				public String getTextOfContent1() {
					Matcher matcher = commentPattern.matcher(driver.getPageSource());
					while (matcher.find()) {
						if (matcher.group(1).equals("_0") && matcher.group(2).equals("content1")) {
							return matcher.group(3);
						}
					}
					return null;
				}
				
				@FindBy(how = How.XPATH, using = "//*[@id='_0']")
				private WebElement content2;
				
				public WebElement getElementOfContent2() {
					return content2;
				}
				
				public String getTextOfContent2() {
					Matcher matcher = commentPattern.matcher(driver.getPageSource());
					while (matcher.find()) {
						if (matcher.group(1).equals("_0") && matcher.group(2).equals("content2")) {
							return matcher.group(3);
						}
					}
					return null;
				}
			''').normalizeCode()

		assertEquals(expected, actual)
	}

	@Test
	def generateForAnEnclosureContainingTwoSameVars() {
		val templateInfo = parser.parse("<html><body>{$content}{$content}</body></html>")
		updater.generate(templateInfo)
		val actual = generator.generate(templateInfo, "test", "Test")
		val expected = generateClass(
			'''
				@FindBy(how = How.XPATH, using = "//*[@id='_0']")
				private WebElement content;
				
				public WebElement getElementOfContent() {
					return content;
				}
				
				public String getTextOfContent() {
					Matcher matcher = commentPattern.matcher(driver.getPageSource());
					while (matcher.find()) {
						if (matcher.group(1).equals("_0") && matcher.group(2).equals("content")) {
							return matcher.group(3);
						}
					}
					return null;
				}
			''').normalizeCode()

		assertEquals(expected, actual)
	}

	@Test
	def generateForEnclosuresContainingTwoVarsWithTextAndAttr() {
		val templateInfo = parser.parse("<html><body attr='{$content1}'>{$content2}</body></html>")
		updater.generate(templateInfo)
		val actual = generator.generate(templateInfo, "test", "Test")
		val expected = generateClass(
			'''
				@FindBy(how = How.XPATH, using = "//*[@id='_0']")
				private WebElement content1;
				
				public WebElement getElementOfContent1() {
					return content1;
				}
				
				public String getAttributeOfAttrOnContent1() {
					return content1.getAttribute("attr");
				}
				
				public String getTextOfContent1() {
					Matcher matcher = commentPattern.matcher(driver.getPageSource());
					while (matcher.find()) {
						if (matcher.group(1).equals("_0") && matcher.group(2).equals("content1")) {
							return matcher.group(3);
						}
					}
					return null;
				}
				
				@FindBy(how = How.XPATH, using = "//*[@id='_0']")
				private WebElement content2;
				
				public WebElement getElementOfContent2() {
					return content2;
				}
				
				public String getTextOfContent2() {
					Matcher matcher = commentPattern.matcher(driver.getPageSource());
					while (matcher.find()) {
						if (matcher.group(1).equals("_0") && matcher.group(2).equals("content2")) {
							return matcher.group(3);
						}
					}
					return null;
				}
			''').normalizeCode()

		assertEquals(expected, actual)
	}

	@Test
	def generateForDistantEnclosures() {
		val templateInfo = parser.parse("<html><body><p></p>{$content}</body></html>")
		updater.generate(templateInfo)
		val actual = generator.generate(templateInfo, "test", "Test")
		val expected = generateClass(
			'''
				@FindBy(how = How.XPATH, using = "//*[@id='_0']")
				private WebElement content;
				
				public WebElement getElementOfContent() {
					return content;
				}
				
				public String getTextOfContent() {
					Matcher matcher = commentPattern.matcher(driver.getPageSource());
					while (matcher.find()) {
						if (matcher.group(1).equals("_0") && matcher.group(2).equals("content")) {
							return matcher.group(3);
						}
					}
					return null;
				}
			''').normalizeCode()

		assertEquals(expected, actual)
	}

	@Test
	def treatExistingId() {
		val templateInfo = parser.parse("<html><body id='content'><p></p>{$content}</body></html>")
		updater.generate(templateInfo)
		val actual = generator.generate(templateInfo, "test", "Test")
		val expected = generateClass(
			'''
				@FindBy(how = How.XPATH, using = "//*[@id='content']")
				private WebElement content;
				
				public WebElement getElementOfContent() {
					return content;
				}
				
				public String getTextOfContent() {
					Matcher matcher = commentPattern.matcher(driver.getPageSource());
					while (matcher.find()) {
						if (matcher.group(1).equals("content") && matcher.group(2).equals("content")) {
							return matcher.group(3);
						}
					}
					return null;
				}
			''').normalizeCode()

		assertEquals(expected, actual)
	}

	@Test
	def treatExistingId2() {
		val templateInfo = parser.parse("<html><body id='content'><p></p><p>{$content}</p></body></html>")
		updater.generate(templateInfo)
		val actual = generator.generate(templateInfo, "test", "Test")
		val expected = generateClass(
			'''
				@FindBy(how = How.XPATH, using = "//*[@id='_0']")
				private WebElement content;
				
				public WebElement getElementOfContent() {
					return content;
				}
				
				public String getTextOfContent() {
					Matcher matcher = commentPattern.matcher(driver.getPageSource());
					while (matcher.find()) {
						if (matcher.group(1).equals("_0") && matcher.group(2).equals("content")) {
							return matcher.group(3);
						}
					}
					return null;
				}
			''').normalizeCode()

		assertEquals(expected, actual)
	}

	@Test
	def treatConflictedVariableNames() {
		val templateInfo = parser.parse("<html><p>{$content1}</p><p>{$content1}{$content2}</p></body></html>")
		updater.generate(templateInfo)
		val actual = generator.generate(templateInfo, "test", "Test")
		val expected = generateClass(
			'''
				@FindBy(how = How.XPATH, using = "//*[@id='_0']")
				private WebElement content1;
				
				public WebElement getElementOfContent1() {
					return content1;
				}
				
				public String getTextOfContent1() {
					Matcher matcher = commentPattern.matcher(driver.getPageSource());
					while (matcher.find()) {
						if (matcher.group(1).equals("_0") && matcher.group(2).equals("content1")) {
							return matcher.group(3);
						}
					}
					return null;
				}
				
				@FindBy(how = How.XPATH, using = "//*[@id='_1']")
				private WebElement content12;
				
				public WebElement getElementOfContent12() {
					return content12;
				}
				
				public String getTextOfContent12() {
					Matcher matcher = commentPattern.matcher(driver.getPageSource());
					while (matcher.find()) {
						if (matcher.group(1).equals("_1") && matcher.group(2).equals("content1")) {
							return matcher.group(3);
						}
					}
					return null;
				}
				
				@FindBy(how = How.XPATH, using = "//*[@id='_1']")
				private WebElement content2;
				
				public WebElement getElementOfContent2() {
					return content2;
				}
				
				public String getTextOfContent2() {
					Matcher matcher = commentPattern.matcher(driver.getPageSource());
					while (matcher.find()) {
						if (matcher.group(1).equals("_1") && matcher.group(2).equals("content2")) {
							return matcher.group(3);
						}
					}
					return null;
				}
			''').normalizeCode()

		assertEquals(expected, actual)
	}

	@Test
	def treatVariableInAttribute() {
		val templateInfo = parser.parse("<html><body><a href='{$url}'></a></body></html>")
		updater.generate(templateInfo)
		val actual = generator.generate(templateInfo, "test", "Test")
		val expected = generateClass(
			'''
				@FindBy(how = How.XPATH, using = "//*[@id='_0']")
				private WebElement A;
				
				public WebElement getElementOfA() {
					return A;
				}
				
				@FindBy(how = How.XPATH, using = "//*[@id='_0']")
				private WebElement url;
				
				public WebElement getElementOfUrl() {
					return url;
				}
				
				public String getAttributeOfHrefOnUrl() {
					return url.getAttribute("href");
				}
				
				public String getTextOfUrl() {
					Matcher matcher = commentPattern.matcher(driver.getPageSource());
					while (matcher.find()) {
						if (matcher.group(1).equals("_0") && matcher.group(2).equals("url")) {
							return matcher.group(3);
						}
					}
					return null;
				}
			''').normalizeCode()

		assertEquals(expected, actual)
	}

	@Test
	def treatVariableInAttributeAndText() {
		val templateInfo = parser.parse("<html><body><a href='{$url}'>{$url}</a></body></html>")
		updater.generate(templateInfo)
		val actual = generator.generate(templateInfo, "test", "Test")
		val expected = generateClass(
			'''
				@FindBy(how = How.XPATH, using = "//*[@id='_0']")
				private WebElement A__$url_;
				
				public WebElement getElementOfA__$url_() {
					return A__$url_;
				}
				
				@FindBy(how = How.XPATH, using = "//*[@id='_0']")
				private WebElement url;
				
				public WebElement getElementOfUrl() {
					return url;
				}
				
				public String getAttributeOfHrefOnUrl() {
					return url.getAttribute("href");
				}
				
				public String getTextOfUrl() {
					Matcher matcher = commentPattern.matcher(driver.getPageSource());
					while (matcher.find()) {
						if (matcher.group(1).equals("_0") && matcher.group(2).equals("url")) {
							return matcher.group(3);
						}
					}
					return null;
				}
			''').normalizeCode()

		assertEquals(expected, actual)
	}

	@Test
	def treatRepeatedPart() {
		val templateInfo = parser.parse("<html>{foreach $url in $urls}<a href='{$url}'>{$url}</a>{/foreach}</html>")
		updater.generate(templateInfo)
		val actual = generator.generate(templateInfo, "test", "Test")
		val expected = generateClass(
			'''
				@FindBy(how = How.XPATH, using = "//*[@id='_0']")
				private List<WebElement> A__$url_;
				
				public List<WebElement> getElementsOfA__$url_() {
					return A__$url_;
				}
				
				@FindBy(how = How.XPATH, using = "//*[@id='_0']")
				private List<WebElement> url;
				
				public List<WebElement> getElementsOfUrl() {
					return url;
				}
				
				public List<String> getAttributesOfHrefOnUrl() {
					List<String> result = new ArrayList<String>();
					for (WebElement e : url) {
						result.add(e.getAttribute("href"));
					}
					return result;
				}
				
				public String getTextOfUrl() {
					Matcher matcher = commentPattern.matcher(driver.getPageSource());
					while (matcher.find()) {
						if (matcher.group(1).equals("_0") && matcher.group(2).equals("url")) {
							return matcher.group(3);
						}
					}
					return null;
				}
			''').normalizeCode()

		assertEquals(expected, actual)
	}

	@Test
	def treatExcludedPart() {
		val templateInfo = parser.parse("<html>{call .t1}{$v1}{/call}<a>{$v2}</a></html>")
		updater.generate(templateInfo)
		val actual = generator.generate(templateInfo, "test", "Test")
		val expected = generateClass(
			'''
				@FindBy(how = How.XPATH, using = "//*[@id='_0']")
				private WebElement A__$v2_;
				
				public WebElement getElementOfA__$v2_() {
					return A__$v2_;
				}
				
				@FindBy(how = How.XPATH, using = "//*[@id='_0']")
				private WebElement v2;
				
				public WebElement getElementOfV2() {
					return v2;
				}
				
				public String getTextOfV2() {
					Matcher matcher = commentPattern.matcher(driver.getPageSource());
					while (matcher.find()) {
						if (matcher.group(1).equals("_0") && matcher.group(2).equals("v2")) {
							return matcher.group(3);
						}
					}
					return null;
				}
			''').normalizeCode()

		assertEquals(expected, actual)
	}
}
