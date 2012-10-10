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

package com.google.testing.pogen.generator.template;

import com.google.testing.pogen.parser.template.HtmlTagInfo;
import com.google.testing.pogen.parser.template.TemplateInfo;

/**
 * A class to modify the template from the specified {@link TemplateInfo} instance with an attribute
 * but "class" attribute.
 * 
 * @author Kazunori Sakamoto
 */
class TemplateUpdaterWithoutClassAttribute extends TemplateUpdater {
  /**
   * A name of the attribute to be assigned for tags containing template variables.
   */
  private final String attributeName;

  /**
   * Constructs an instance with the specified attribute name and the default prefix of a generating
   * attribute value.
   * 
   * @param attributeName the name of the attribute to be assigned for tags containing template
   *        variables
   */
  TemplateUpdaterWithoutClassAttribute(String attributeName) {
    super();
    this.attributeName = attributeName;
  }

  /**
   * Constructs an instance with the specified attribute name and the given prefix of a generating
   * attribute value.
   * 
   * @param attributeName the name of the attribute to be assigned for tags containing template
   *        variables
   * @param idPrefix the prefix of a new id attribute
   */
  TemplateUpdaterWithoutClassAttribute(String attributeName, String idPrefix) {
    super(idPrefix);
    this.attributeName = attributeName;
  }

  /**
   * Builds the modified tag containing template variables.
   * 
   * @param template the string of the html template
   * @param tagInfo the information of the html tag containing template variables
   * @return the string of the modified tag
   */
  protected StringBuilder buildModifiedTag(String template, HtmlTagInfo tagInfo) {
    StringBuilder tag =
        new StringBuilder(template.substring(tagInfo.getStartIndex(), tagInfo.getEndIndex()));
    if (tagInfo.hasAttributeValue()) {
      return tag;
    }
    tagInfo.setAttributeValue(generateUniqueValue());
    // Deal with closed tag such as <br />
    int insertIndex = tag.length() - 2;
    String tail = ">";
    if (tag.charAt(insertIndex) == '/') {
      --insertIndex;
      tail = " />";
    }
    // Remove redundant space
    while (tag.charAt(insertIndex) == ' ') {
      insertIndex--;
    }
    // Insert the generated id attribute
    tag.setLength(insertIndex + 1);
    tag.append(" " + attributeName + "=\"" + tagInfo.getAttributeValue() + "\"" + tail);
    return tag;
  }
}
