// Copyright 2011 The PageObjectGenerator Authors.
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

package com.google.testing.pogen.generator.test.java;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultiset;
import com.google.testing.pogen.generator.test.PageObjectUpdateException;
import com.google.testing.pogen.parser.template.HtmlTagInfo;
import com.google.testing.pogen.parser.template.TemplateInfo;
import com.google.testing.pogen.parser.template.VariableInfo;

/**
 * A class to generate skeleton test code designed by PageObject pattern for Selenium2 (WebDriver)
 * from the specified {@link TemplateInfo} instance.
 * 
 * @author Kazunori Sakamoto
 */
public class TestCodeGenerator {
  /**
   * A string to indicate the start of generated fields and getter methods.
   */
  public static final String GENERATED_CODE_END_MARK =
      "/* -------------------- GENERATED CODE END -------------------- */";
  /**
   * A string to indicate the end of generated fields and getter methods.
   */
  public static final String GENERATED_CODE_START_MARK =
      "/* ------------------- GENERATED CODE START ------------------- */";
  /**
   * An indent string.
   */
  private final String indent;
  /**
   * A new-line string.
   */
  private final String newLine;

  /**
   * Constructs an instance with the default indent and new-line strings.
   */
  public TestCodeGenerator() {
    this("  ", "\n");
  }

  /**
   * Constructs an instance with the specified indent and the specified new-line strings.
   */
  public TestCodeGenerator(String indent, String newLine) {
    this.indent = indent;
    this.newLine = newLine;
  }

  /**
   * Generates skeleton test code with getter methods for html elements, texts and attributes to
   * retrieve values of variables from Selenium2.
   * 
   * @param templateInfo the {@link TemplateInfo} of the template whose skeleton test code we want
   *        to generate
   * @param packageName the package name to generate skeleton test code
   * @param className the class name to generate skeleton test code
   * @return the generated skeleton test code
   */
  public String generate(TemplateInfo templateInfo, String packageName, String className) {
    Preconditions.checkNotNull(templateInfo);
    Preconditions.checkNotNull(packageName);
    Preconditions.checkNotNull(className);

    StringBuilder builder = new StringBuilder();
    appendLine(builder, 0, String.format("package %s;", packageName));
    appendLine(builder);

    appendLine(builder, 0, "import static org.junit.Assert.*;");
    appendLine(builder, 0, "import static org.hamcrest.Matchers.*;");
    appendLine(builder);

    appendLine(builder, 0, "import org.openqa.selenium.By;");
    appendLine(builder, 0, "import org.openqa.selenium.WebDriver;");
    appendLine(builder, 0, "import org.openqa.selenium.WebElement;");
    appendLine(builder, 0, "import org.openqa.selenium.support.FindBy;");
    appendLine(builder, 0, "import org.openqa.selenium.support.How;");
    appendLine(builder);
    appendLine(builder, 0, "import java.util.ArrayList;");
    appendLine(builder);

    appendLine(builder, 0, String.format("public class %sPage extends AbstractPage {", className));
    appendLine(builder, 1, String.format("public %sPage(WebDriver driver) {", className));
    appendLine(builder, 2, String.format("super(driver);", className));
    appendLine(builder, 2, String.format("assertInvariant();", className));
    appendLine(builder, 1, String.format("}", className));
    appendLine(builder);

    appendLine(builder, 1, "private void assertInvariant() {");
    appendLine(builder, 1, "}");
    appendLine(builder);

    appendLine(builder, 1, GENERATED_CODE_START_MARK);
    appendFieldsAndGetters(builder, templateInfo);
    appendLine(builder, 1, GENERATED_CODE_END_MARK);
    appendLine(builder, 0, "}");
    return builder.toString();
  }

  /**
   * Updates existing test code with getter methods for html elements, texts and attributes to
   * retrieve the values of the variables from Selenium2.
   * 
   * @param templateInfo the {@link TemplateInfo} of the template whose skeleton test code we want
   *        to generate
   * @param code the existing test code
   * @return the updated skeleton test code
   * @throws PageObjectUpdateException if the existing test code doesn't have generated code
   */
  public String update(TemplateInfo templateInfo, String code) throws PageObjectUpdateException {
    Preconditions.checkNotNull(templateInfo);
    Preconditions.checkNotNull(code);

    StringBuilder builder = new StringBuilder();
    int startIndex = code.indexOf(GENERATED_CODE_START_MARK);
    int endIndex = code.indexOf(GENERATED_CODE_END_MARK);
    if (startIndex < 0 || endIndex < 0 || endIndex < startIndex) {
      throw new PageObjectUpdateException();
    }
    builder.append(code.subSequence(0, startIndex + GENERATED_CODE_START_MARK.length()));
    builder.append(newLine);
    appendFieldsAndGetters(builder, templateInfo);
    builder.append(code.subSequence(endIndex, code.length()));
    return builder.toString();
  }

  /**
   * Appends the body of skeleton test code, that is, only html element fields and getter methods to
   * retrieve the values of the variables into the given string builder.
   * 
   * @param builder {@link StringBuilder} the generated test code will be appended to
   * @param templateInfo the {@link TemplateInfo} of the template whose skeleton test code we want
   *        to generate
   */
  private void appendFieldsAndGetters(StringBuilder builder, TemplateInfo templateInfo) {
    // Create new StringBuilder to separate methods group from fields group such
    // as "private int field1; private int field2;
    // private void method1() {} private void method2() {}".
    StringBuilder methodBuilder = new StringBuilder();

    HashMultiset<String> varNameCounter = HashMultiset.create();

    for (HtmlTagInfo tagInfo : templateInfo.getHtmlTagInfos()) {
      // Skip this variable if it has no parent html tag
      // TODO(kazuu): Deal with these variable with a more proper way
      if (!tagInfo.hasParentTag()) {
        continue;
      }

      String id = tagInfo.getId();
      boolean isRepeated = templateInfo.isRepeated(tagInfo);

      for (VariableInfo varInfo : tagInfo.getVariableInfos()) {
        // When the same template variable appears in other html tags,
        // varIndex > 1 is satisfied
        varNameCounter.add(varInfo.getName());
        int varIndex = varNameCounter.count(varInfo.getName());
        String newVarName = varInfo.getName() + convertToString(varIndex);

        appendElementGetter(builder, methodBuilder, newVarName, id, isRepeated);
        if (varInfo.isContainedByText()) {
          appendTextGetter(methodBuilder, newVarName, id, isRepeated);
        }
        for (String attrName : varInfo.getSortedAttributeNames()) {
          appendAttributeGetter(methodBuilder, newVarName, attrName, id, isRepeated);
        }
      }
    }
    // Append method definitions after field definitions
    builder.append(methodBuilder);
  }

  /**
   * Appends a getter method and also a field if needed for the html element which contains the
   * variable specified by the name into the given string builder.
   * 
   * @param fieldBuilder {@link StringBuilder} the generated field will be appended to
   * @param methodBuilder {@link StringBuilder} the generated method will be appended to
   * @param variableName the variable name
   * @param id the id value assigned to the field
   * @param isRepeated the boolean whether the specified html tag appears in a repeated part
   */
  private void appendElementGetter(StringBuilder fieldBuilder, StringBuilder methodBuilder,
      String variableName, String id, boolean isRepeated) {
    if (!isRepeated) {
      appendField(fieldBuilder, variableName, id);
      appendGetter(methodBuilder, variableName, "", "WebElement", "ElementFor");
    } else {
      appendListGetter(methodBuilder, variableName, "", "WebElement", "ElementsFor", id);
    }
  }

  /**
   * Appends a getter method for the text of html element which contains the variable specified by
   * the name into the given string builder.
   * 
   * @param methodBuilder {@link StringBuilder} the generated method will be appended to
   * @param variableName the variable name
   * @param id the id value assigned to the field
   * @param isRepeated the boolean whether the specified html tag appears in a repeated part
   */
  private void appendTextGetter(StringBuilder methodBuilder, String variableName, String id,
      boolean isRepeated) {
    if (!isRepeated) {
      appendGetter(methodBuilder, variableName, ".getText()", "String", "TextFor");
    } else {
      appendListGetter(methodBuilder, variableName, ".getText()", "String", "TextsFor", id);
    }
  }

  /**
   * Appends a getter method for the attribute of html element which contains the variable specified
   * by the name into the given string builder.
   * 
   * @param methodBuilder {@link StringBuilder} the generated method will be appended to
   * @param variableName the variable name
   * @param attributeName the name of the attribute which contains template variables
   * @param id the id value assigned to the field
   * @param isRepeated the boolean whether the specified html tag appears in a repeated part
   */
  private void appendAttributeGetter(StringBuilder methodBuilder, String variableName,
      String attributeName, String id, boolean isRepeated) {
    if (!isRepeated) {
      appendGetter(methodBuilder, variableName, ".getAttribute(\"" + attributeName + "\")",
          "String", StringUtils.capitalize(attributeName) + "AttributeFor");
    } else {
      appendListGetter(methodBuilder, variableName, ".getAttribute(\"" + attributeName + "\")",
          "String", StringUtils.capitalize(attributeName) + "AttributesFor", id);
    }
  }

  /**
   * Appends a private field for accessing the html element which has the specified id value and
   * contains the variable specified by the name with {@literal @FindBy(how = How.ID, ...)} into the
   * given string builder.
   * 
   * @param builder {@link StringBuilder} the generated test code will be appended to
   * @param variableName the variable name
   * @param id the id value assigned to the field
   */
  private void appendField(StringBuilder builder, String variableName, String id) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(id));

    appendLine(builder, 1, String.format("@FindBy(how = How.ID, using = \"%s\")", id));
    appendLine(builder, 1, String.format("private WebElement %s;", variableName));
  }

  /**
   * Appends a getter method for the variable specified by the name or the result of invoking the
   * method described by the given prefix on the variable into the given string builder.
   * 
   * @param builder {@link StringBuilder} the generated test code will be appended to
   * @param variableName the variable name
   * @param elementSuffixForInvoking the suffix of the {@code WebElement} variable that specifies a
   *        method name with a dot to invoke it, e.g. {@literal".getText()"}, or an empty string to
   *        access the variable directly.
   * @param returnType the return type of the generated getter method
   * @param methodNamePrefix the name prefix of the generated method
   */
  private void appendGetter(StringBuilder builder, String variableName,
      String elementSuffixForInvoking, String returnType, String methodNamePrefix) {
    appendLine(builder);
    // TODO(kazuu): Help to select proper one from getFoo, getFoo2, getFoo3 ...
    appendLine(
        builder,
        1,
        String.format("public %s get%s%s() {", returnType, methodNamePrefix,
            StringUtils.capitalize(variableName)));
    appendLine(builder, 2, String.format("return %s%s;", variableName, elementSuffixForInvoking));
    appendLine(builder, 1, "}");
  }

  /**
   * Appends a getter method for the list of the variables specified by the name or the result of
   * invoking the method described by the given prefix on the variable. The generated getter method
   * is used for repeated part in templates such as "{foreach $x in $xs}{@literal <div>$x</div>}
   * {/foreach}"
   * 
   * @param builder {@link StringBuilder} the generated test code will be appended to
   * @param variableName the variable name
   * @param elementSuffixForInvoking the suffix of the {@code WebElement} variable that specifies a
   *        method name with a dot to invoke it, e.g. {@literal".getText()"}, or an empty string to
   *        access the variable directly.
   * @param returnType the return type of the generated getter method
   * @param methodNamePrefix the name prefix of the generated method
   * @param id the id value assigned to the field
   */
  private void appendListGetter(StringBuilder builder, String variableName,
      String elementSuffixForInvoking, String returnType, String methodNamePrefix, String id) {
    appendLine(builder);
    // TODO(kazuu): Help to select proper one from getFoo, getFoo2, getFoo3 ...
    appendLine(
        builder,
        1,
        String.format("public List<%s> get%s%s() {", returnType, methodNamePrefix,
            StringUtils.capitalize(variableName)));
    appendLine(builder, 2,
        String.format("List<%s> result = new ArrayList<%s>();", returnType, returnType));
    appendLine(builder, 2,
        String.format("for (WebElement e : driver.findElements(By.id(\"%s\"))) {", id));
    appendLine(builder, 3, String.format("result.add(e%s);", elementSuffixForInvoking));
    appendLine(builder, 2, "}");
    appendLine(builder, 2, "return result;");
    appendLine(builder, 1, "}");
  }

  /**
   * Converts the specified number to a string. Results an empty string if the number is 1.
   * 
   * @param number the number to be converted
   * 
   * @return an empty string if the specified number is 1, otherwise prefix + number
   */
  private String convertToString(int number) {
    return number == 1 ? "" : String.valueOf(number);
  }

  /**
   * Appends a new-line character into the specified builder.
   * 
   * @param builder the string builder to be appended a new-line character
   */
  private void appendLine(StringBuilder builder) {
    builder.append(newLine);
  }

  /**
   * Appends the specified line with the specified number of indent string and a new-line character
   * into the specified builder.
   * 
   * @param builder the string builder to be appended strings
   * @param indentCount the number of indent string to be appended at the beginning of the line
   * @param line the line string to be be appended into the builder
   */
  private void appendLine(StringBuilder builder, int indentCount, String line) {
    for (int i = 0; i < indentCount; i++) {
      builder.append(indent);
    }
    builder.append(line);
    builder.append(newLine);
  }
}
