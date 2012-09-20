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

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.testing.pogen.parser.template.TemplateInfo;

/**
 * A class to generate skeleton test code designed by PageObject pattern for Selenium2 (WebDriver)
 * from the specified {@link TemplateInfo} instance with assigned attributes but 'class' attributes.
 * 
 * @author Kazunori Sakamoto
 */
public class TestCodeGeneratorWithoutClassAttribute extends TestCodeGenerator {
  /**
   * An assigned attribute name to find html tags containing template variables.
   */
  private final String assignedAttributeName;

  /**
   * Constructs an instance with the given assigned attribute name and the default indent and
   * new-line strings.
   * 
   * @param assignedAttributeName the assigned attribute name to find html elements containing
   *        template variables
   */
  public TestCodeGeneratorWithoutClassAttribute(String assignedAttributeName) {
    this(assignedAttributeName, "  ", "\n");
  }

  /**
   * Constructs an instance with the given assigned attribute name and the specified indent and the
   * specified new-line strings.
   * 
   * @param assignedAttributeName the assigned attribute name to find html elements containing
   *        template variables
   * @param indent the string of indent for generating source code
   * @param newLine the string of new line for generating source code
   */
  public TestCodeGeneratorWithoutClassAttribute(String assignedAttributeName, String indent,
      String newLine) {
    super(indent, newLine);
    this.assignedAttributeName = assignedAttributeName;
  }

  protected void appendField(StringBuilder builder, String variableName,
      String assignedAttributeValue) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(assignedAttributeValue));

    appendLine(builder, 1, String.format("@FindBy(how = How.XPATH, using = \"//*[@%s='%s']\")",
        assignedAttributeName, assignedAttributeValue));
    appendLine(builder, 1, String.format("private WebElement %s;", variableName));
  }

  protected void appendListGetter(StringBuilder builder, String variableName,
      String elementSuffixForInvoking, String returnType, String methodNamePrefix,
      String assignedAttributeValue) {
    appendLine(builder);
    // TODO(kazuu): Help to select proper one from getFoo, getFoo2, getFoo3 ...
    appendLine(
        builder,
        1,
        String.format("public List<%s> get%s%s() {", returnType, methodNamePrefix,
            StringUtils.capitalize(variableName)));
    appendLine(builder, 2,
        String.format("List<%s> result = new ArrayList<%s>();", returnType, returnType));
    appendLine(builder, 2, String.format(
        "for (WebElement e : driver.findElements(By.xpath(\"//*[@%s='%s']\"))) {",
        assignedAttributeName, assignedAttributeValue));
    appendLine(builder, 3, String.format("result.add(e%s);", elementSuffixForInvoking));
    appendLine(builder, 2, "}");
    appendLine(builder, 2, "return result;");
    appendLine(builder, 1, "}");
  }
}
