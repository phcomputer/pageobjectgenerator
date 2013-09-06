package com.google.testing.pogen.generator.test.java

import com.google.common.base.Preconditions
import com.google.common.collect.HashMultiset
import com.google.testing.pogen.generator.test.PageObjectUpdateException
import com.google.testing.pogen.parser.template.TemplateInfo
import org.apache.commons.lang3.StringUtils

abstract class TestCodeGenerator {

	/**
	 * The string to indicate the start of generated fields and getter methods.
	 */
	val GENERATED_CODE_END_MARK = "/* -------------------- GENERATED CODE END -------------------- */"

	/**
	 * The string to indicate the end of generated fields and getter methods.
	 */
	val GENERATED_CODE_START_MARK = "/* ------------------- GENERATED CODE START ------------------- */"

	/**
	 * The indent string.
	 */
	val String indent;

	/**
	 * The new-line string.
	 */
	val String newLine;

	/**
	 * The boolean value whether this instance uses css selectors.
	 */
	val String findByAnnotationLeft;
	val String findByAnnotationRight;

	static def createUsingCssSelector() {
		
	}

	/**
	 * Constructs an instance with the default indent and new-line strings.
	 * 
	 * @param usedCssSelector boolean value whether this instance uses css selectors
	 */
	private new(String findByAnnotationLeft, String findByAnnotationRight) {
		this(findByAnnotationLeft, findByAnnotationRight, "  ", "\n")
	}

	/**
	 * Constructs an instance with the specified indent and the specified new-line strings.
	 * 
	 * @param usedCssSelector boolean value whether this instance uses css selectors
	 * @param indent the string of indent for generating source code
	 * @param newLine the string of new line for generating source code
	 */
	private new(String findByAnnotationLeft, String findByAnnotationRight, String indent, String newLine) {
		this.findByAnnotationLeft = findByAnnotationLeft;
		this.findByAnnotationRight = findByAnnotationRight;
		this.indent = indent;
		this.newLine = newLine;
	}

	/**
	 * Generates skeleton test code with getter methods for html tags, texts and attributes to
	 * retrieve values of variables from Selenium2.
	 * 
	 * @param templateInfo the {@link TemplateInfo} of the template whose skeleton test code we want
	 *	 	   to generate
	 * @param packageName the package name to generate skeleton test code
	 * @param className the class name to generate skeleton test code
	 * @return the generated skeleton test code
	 */
	def generate(TemplateInfo templateInfo, String packageName, String className) {
		Preconditions.checkNotNull(templateInfo);
		Preconditions.checkNotNull(packageName);
		Preconditions.checkNotNull(className);
		return '''
			package «packageName»;
			
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
			
			public class «className»Page extends AbstractPage {
				public «className»Page(WebDriver driver) {
					super(driver);
					assertInvariant();
				}
				
				private def assertInvariant() {
				}
			
				«GENERATED_CODE_START_MARK»
				«getFieldsAndGetters(templateInfo)»
				«GENERATED_CODE_END_MARK»
			}
		'''
	}

	/**
	 * Updates existing test code with getter methods for html tags, texts and attributes to retrieve
	 * the values of the variables from Selenium2.
	 * 
	 * @param templateInfo the {@link TemplateInfo} of the template whose skeleton test code we want
	 *	 	   to generate
	 * @param code the existing test code
	 * @return the updated skeleton test code
	 * @throws PageObjectUpdateException if the existing test code doesn't have generated code
	 */
	def update(TemplateInfo templateInfo, String code) throws PageObjectUpdateException {
		Preconditions.checkNotNull(templateInfo);
		Preconditions.checkNotNull(code);

		val builder = new StringBuilder();
		val startIndex = code.indexOf(GENERATED_CODE_START_MARK);
		val endIndex = code.indexOf(GENERATED_CODE_END_MARK);
		if (startIndex < 0 || endIndex < 0 || endIndex < startIndex) {
			throw new PageObjectUpdateException("There are no proper start/end marks.");
		}
		builder.append(code.subSequence(0, startIndex + GENERATED_CODE_START_MARK.length()));
		builder.append(newLine);
		builder.append(getFieldsAndGetters(templateInfo));
		builder.append(code.subSequence(endIndex, code.length()));
		return builder.toString();
	}

	/**
	 * Appends the body of skeleton test code, that is, only html tag fields and getter methods to
	 * retrieve the values of the variables into the given string builder.
	 * 
	 * @param builder {@link StringBuilder} the generated test code will be appended to
	 * @param templateInfo the {@link TemplateInfo} of the template whose skeleton test code we want
	 *	 	   to generate
	 */
	private def getFieldsAndGetters(TemplateInfo templateInfo) {

		// Create new StringBuilder to separate methods group from fields group such
		// as "private int field1; private int field2;
		// private def method1() {} private def method2() {}".
		val builder = new StringBuilder();

		// Append method definitions after field definitions
		builder.append(
			'''
				private static Pattern commentPattern = Pattern.compile(\"<!--POGEN,([^,]*),([^,]*),(.*?)-->\", Pattern.DOTALL);
			''')

		val varNameCounter = HashMultiset.<String>create()

		templateInfo.getHtmlTagInfos().forEach [ tagInfo |
			// Skip this variable if it has no parent html tag
			// TODO(kazuu): Deal with these variable with a more proper way
			if (tagInfo.hasParentTag()) {
				return
			}
			val attrValue = tagInfo.getAttributeValue()
			val isRepeated = tagInfo.isRepeated
			for (varInfo : tagInfo.getVariableInfos()) {

				// When a template variable appears in multiple html tags, varIndex > 1 is satisfied
				varNameCounter.add(varInfo.getName())
				val varIndex = varNameCounter.count(varInfo.getName())
				val varName = varInfo.getName()
				val newVarName = varName + convertToString(varIndex)

				if (!isRepeated) {
					builder.append(
						'''
							«findByAnnotationLeft»
							private WebElement elementOf«newVarName»;
							
							public WebElement getElementOf«StringUtils.capitalize(newVarName)»() {
								return elementOf«newVarName»;
							}
							«FOR attrName : varInfo.sortedAttributeNames»
								
								public String getAttributeOf«attrName»On«StringUtils.capitalize(newVarName)»() {
									return elementOf«newVarName».getAttribute("«attrName»");
								}
							«ENDFOR»
						''')
				} else {
					builder.append(
						'''
							«findByAnnotation»
							private List<WebElement> elementsOf«newVarName»;
							
							public List<WebElement> getElementsOf«StringUtils.capitalize(newVarName)»() {
								return elementsOf«newVarName»;
							}
							«FOR attrName : varInfo.sortedAttributeNames»
								
								public List<String> getAttributesOf«attrName»On«StringUtils.capitalize(newVarName)»() {
									List<String> result = new ArrayList<String>();
									for (WebElement e : elementsOf«newVarName») {
										result.add(e.getAttribute("«attrName»"));
									}
									return result;
								}
							«ENDFOR»
						''')
				}

				if (varIndex == 1) {
					builder.append(
						'''
							public String getTextOf«StringUtils.capitalize(varName)» {
								Matcher matcher = commentPattern.matcher(driver.getPageSource());
								while (matcher.find()) {
									if (matcher.group(1).equals(\"«attrValue»\") && matcher.group(2).equals(\"«varName»\")) {
										return matcher.group(3);
									}
								}
								return null;
							}
						''')
				}
			}
		]

		return builder.toString
	}

	protected abstract def String getFindByAnnotation()

	/**
	 * Converts the specified number to a string. Results an empty string if the number is 1.
	 * 
	 * @param number the number to be converted
	 * 
	 * @return an empty string if the specified number is 1, otherwise prefix + number
	 */
	private def convertToString(int number) {
		return if (number == 1) "" else String.valueOf(number);
	}
}
