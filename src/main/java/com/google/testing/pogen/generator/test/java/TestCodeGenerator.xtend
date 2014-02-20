package com.google.testing.pogen.generator.test.java

import com.google.common.base.Preconditions
import com.google.common.collect.Sets
import com.google.testing.pogen.generator.test.PageObjectUpdateException
import com.google.testing.pogen.parser.template.TemplateInfo
import com.google.testing.pogen.parser.template.VariableInfo
import org.apache.commons.lang3.StringUtils

class TestCodeGenerator {

	/**
	 * The string to indicate the start of generated fields and getter methods.
	 */
	public static val GENERATED_CODE_END_MARK = "/* -------------------- GENERATED CODE END -------------------- */"

	/**
	 * The string to indicate the end of generated fields and getter methods.
	 */
	public static val GENERATED_CODE_START_MARK = "/* ------------------- GENERATED CODE START ------------------- */"

	/**
	 * The string to split original names with numbers to make their names unique.
	 */
	public static val NUMBER_SPLITTER = "_PO"

	/**
	 * The indent string.
	 */
	val String indent;

	/**
	 * The new-line string.
	 */
	val String newLine;

	/**
	 * The head string of the annotation @FindBy.
	 */
	val String findByAnnotationHead;

	/**
	 * The tail string of the annotation @FindBy.
	 */
	val String findByAnnotationTail;

	/**
	 * Constructs an instance with the default indent and new-line strings.
	 * 
	 * @param assignedAttributeName the attribute name which is assigned to identify elements
	 */
	new(String assignedAttributeName) {
		this(assignedAttributeName, "  ", "\n")
	}

	/**
	 * Constructs an instance with the specified indent and the specified new-line strings.
	 * 
	 * @param assignedAttributeName the attribute name which is assigned to identify elements
	 * @param indent the string of indent for generating source code
	 * @param newLine the string of new line for generating source code
	 */
	new(String assignedAttributeName, String indent, String newLine) {
		if (assignedAttributeName.equals("class")) {
			this.findByAnnotationHead = "@FindBy(how = How.CSS, using = \"."
			this.findByAnnotationTail = "\")"
		} else {
			this.findByAnnotationHead = "@FindBy(how = How.XPATH, using = \"//*[@" + assignedAttributeName + "='"
			this.findByAnnotationTail = "']\")"
		}
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
		val code = '''
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
				
				private void assertInvariant() {
				}
				
				«GENERATED_CODE_START_MARK»
				«getFieldsAndGetters(templateInfo)»
				«GENERATED_CODE_END_MARK»
			}
		'''
		code.normalize()
	}

	/**
	 * Updates existing test code with getter methods for html tags, texts and attributes to retrieve
	 * the values of the variables from Selenium2.
	 * 
	 * @param templateInfo the {@link TemplateInfo} of the template whose skeleton test code we want
	 *	 	   to generate
	 * @param existingCode the existing test code
	 * @return the updated skeleton test code
	 * @throws PageObjectUpdateException if the existing test code doesn't have generated code
	 */
	def update(TemplateInfo templateInfo, String existingCode) throws PageObjectUpdateException {
		Preconditions.checkNotNull(templateInfo);
		Preconditions.checkNotNull(existingCode);

		val builder = new StringBuilder();
		val startIndex = existingCode.indexOf(GENERATED_CODE_START_MARK);
		var endIndex = existingCode.indexOf(GENERATED_CODE_END_MARK);
		if (startIndex < 0 || endIndex < 0 || endIndex < startIndex) {
			throw new PageObjectUpdateException("There are no proper start/end marks.");
		}
		builder.append(existingCode.substring(0, startIndex + GENERATED_CODE_START_MARK.length()));
		builder.append(newLine);
		val code = '''
			
				«getFieldsAndGetters(templateInfo)»
		'''
		builder.append(code.substring(code.indexOf('\t')).normalize());
		while (existingCode.charAt(endIndex - 1) == ' ' || existingCode.charAt(endIndex - 1) == '\t') {
			endIndex = endIndex - 1
		}
		builder.append(existingCode.substring(endIndex, existingCode.length()));
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
				private static Pattern commentPattern = Pattern.compile("<!--POGEN,([^,]*),([^,]*),(.*?)-->", Pattern.DOTALL);
			''')

		val varNames = Sets.<String>newHashSet()

		templateInfo.getHtmlTagInfos().filter [
			it.hasParentTag()
		].forEach [
			// Skip this variable if it has no parent html tag
			// TODO(kazuu): Deal with these variable with a more proper way
			val attrValue = it.getAttributeValue()
			val isRepeated = it.isRepeated
			for (varInfo : it.getVariableInfos()) {
				val varName = varInfo.getName()
				val uniqueVarName = if (!varNames.contains(varName)) {
						varName
					} else {
						(2 .. Integer.MAX_VALUE).map[varName + NUMBER_SPLITTER + it].findFirst[!varNames.contains(it)]
					}
				varNames += uniqueVarName

				if (!isRepeated) {
					builder.append(getElementFieldAndMethod(attrValue, uniqueVarName))
					builder.append(getAttributeMethod(attrValue, uniqueVarName, varInfo))
				} else {
					builder.append(getElementListFieldAndMethod(attrValue, uniqueVarName))
					builder.append(getAttributeListMethod(attrValue, uniqueVarName, varInfo))
				}

				if (!varInfo.manipulableTag) {
					if (!isRepeated) {
						builder.append(getTextMethod(uniqueVarName, attrValue, varName))
					} else {
						builder.append(getTextListMethod(uniqueVarName, attrValue, varName))
					}
				}
			}
		]

		return builder.toString
	}

	private def getElementFieldAndMethod(String attrValue, String newVarName) '''
		
		«getFindByAnnotation(attrValue)»
		private WebElement «newVarName»;
		
		public WebElement getElementOf«StringUtils.capitalize(newVarName)»() {
			return «newVarName»;
		}
	'''

	private def getAttributeMethod(String attrValue, String newVarName, VariableInfo varInfo) '''
		«FOR attrName : varInfo.sortedAttributeNames»
			
			public String getAttributeOf«StringUtils.capitalize(attrName)»On«StringUtils.capitalize(newVarName)»() {
				return «newVarName».getAttribute("«attrName»");
			}
		«ENDFOR»
	'''

	private def getElementListFieldAndMethod(String attrValue, String newVarName) '''
		
		«getFindByAnnotation(attrValue)»
		private List<WebElement> «newVarName»;
		
		public List<WebElement> getElementsOf«StringUtils.capitalize(newVarName)»() {
			return «newVarName»;
		}
	'''

	private def getAttributeListMethod(String attrValue, String newVarName, VariableInfo varInfo) '''
		«FOR attrName : varInfo.sortedAttributeNames»
			
			public List<String> getAttributesOf«StringUtils.capitalize(attrName)»On«StringUtils.capitalize(newVarName)»() {
				List<String> result = new ArrayList<String>();
				for (WebElement e : «newVarName») {
					result.add(e.getAttribute("«attrName»"));
				}
				return result;
			}
		«ENDFOR»
	'''

	private def getTextMethod(String newVarName, String attrValue, String varName) '''
		
		public String getTextOf«StringUtils.capitalize(newVarName)»() {
			Matcher matcher = commentPattern.matcher(driver.getPageSource());
			while (matcher.find()) {
				if (matcher.group(1).equals("«attrValue»") && matcher.group(2).equals("«varName»")) {
					return matcher.group(3);
				}
			}
			return null;
		}
	'''

	private def getTextListMethod(String newVarName, String attrValue, String varName) '''
		
		public List<String> getTextsOf«StringUtils.capitalize(newVarName)»() {
			List<String> result = new ArrayList<String>();
			Matcher matcher = commentPattern.matcher(driver.getPageSource());
			while (matcher.find()) {
				if (matcher.group(1).equals("«attrValue»") && matcher.group(2).equals("«varName»")) {
					result.add(matcher.group(3));
				}
			}
			return result;
		}
	'''

	private def String getFindByAnnotation(String attrValue) {
		findByAnnotationHead + attrValue + findByAnnotationTail
	}

	private def normalize(String code) {
		code.replace("	", indent).replace("\r\n", "\n").replace("\n", newLine)
	}

}
