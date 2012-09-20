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

import org.apache.commons.lang3.StringUtils;

import com.google.testing.pogen.parser.template.HtmlTagInfo;
import com.google.testing.pogen.parser.template.TemplateInfo;

/**
 * A class to modify the template from the specified {@link TemplateInfo} instance with a "class"
 * attribute.
 * 
 * @author Kazunori Sakamoto
 */
class TemplateUpdaterWithClassAttribute extends TemplateUpdater {

  /**
   * Constructs an instance with the default prefix of a new id attribute.
   */
  TemplateUpdaterWithClassAttribute() {
    super();
  }

  /**
   * Constructs an instance with the given prefix of a new id attribute.
   * 
   * @param idPrefix the prefix of a new id attribute
   */
  TemplateUpdaterWithClassAttribute(String idPrefix) {
    super(idPrefix);
  }

  protected StringBuilder buildModifiedTag(String template, HtmlTagInfo tagInfo) {
    StringBuilder tag =
        new StringBuilder(template.substring(tagInfo.getStartIndex(), tagInfo.getEndIndex()));
    tagInfo.setId(generateUniqueId());
    if (tagInfo.hasAttributeValue()) {
      int space = StringUtils.indexOf(tag, ' ');
      int equal;
      while (space > 0 && (equal = StringUtils.indexOf(tag, '=', space + 1)) > 0) {
        String name = tag.substring(space + 1, equal).trim();
        int leftQuote = StringUtils.indexOfAny(tag, '"', '\'');
        if (name.equals("class")) {
          tag.insert(leftQuote + 1, tagInfo.getAttributeValue() + " ");
          return tag;
        }
        space = StringUtils.indexOf(tag, tag.charAt(leftQuote)) + 1;
      }
    }
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
    tag.append(" class=\"" + tagInfo.getAttributeValue() + "\"" + tail);
    return tag;
  }
}
