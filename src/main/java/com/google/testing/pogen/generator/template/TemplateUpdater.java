// Copyright 2011 The PageObjectGenerator Authors.
// Copyright 2011 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS-IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.testing.pogen.generator.template;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.testing.pogen.parser.template.HtmlTagInfo;
import com.google.testing.pogen.parser.template.TemplateInfo;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A class to modify the template from the specified {@link TemplateInfo}
 * instance.
 *
 * @author Kazunori Sakamoto
 */
public class TemplateUpdater {
  /**
   * A prefix of a new id attribute.
   */
  private final String idPrefix;
  /**
   * A unique number to generate new id attributes.
   */
  private int idCount = 0;

  /**
   * Constructs an instance with the default prefix of a new id attribute.
   */
  public TemplateUpdater() {
    this("__pogen_");
  }

  /**
   * Constructs an instance with the given prefix of a new id attribute.
   *
   * @param idPrefix the prefix of a new id attribute
   */
  public TemplateUpdater(String idPrefix) {
    this.idPrefix = idPrefix;
  }

  /**
   * Generates a modified template from the specified {@link TemplateInfo}
   * inserting id attributes into html tags which contain template variables.
   *
   * @param templateInfo the {@link TemplateInfo} instance of the template to be
   *        updated
   * @return the modified template with the inserted id attributes
   */
  public String generate(TemplateInfo templateInfo) {
    Preconditions.checkNotNull(templateInfo);

    StringBuilder newTemplate = new StringBuilder();
    String template = templateInfo.getTemplate();
    int lastIndex = 0;

    List<HtmlTagInfo> htmlTagInfos = Lists.newArrayList(templateInfo.getHtmlTagInfos());
    Collections.sort(htmlTagInfos, new Comparator<HtmlTagInfo>() {
      @Override
      public int compare(HtmlTagInfo o1, HtmlTagInfo o2) {
        if (o1.getEndIndex() < o2.getEndIndex()) {
          return -1;
        } else if (o1.getEndIndex() > o2.getEndIndex()) {
          return 1;
        }
        return 0;
      }
    });

    for (HtmlTagInfo tagInfo : htmlTagInfos) {
      // Skip this variable if it has no parent html tag
      // TODO(kazuu): Deal with these variable with a more proper way
      if (!tagInfo.hasParentTag()) {
        continue;
      }

      // Append rest content
      newTemplate.append(template.subSequence(lastIndex, tagInfo.getStartIndex()));
      lastIndex = tagInfo.getEndIndex();

      // Construct a modified template
      StringBuilder sb =
          new StringBuilder(template.substring(tagInfo.getStartIndex(), tagInfo.getEndIndex()));
      if (!tagInfo.hasId()) {
        tagInfo.setId(generateUniqueId());
        // Deal with closed tag such as <br />
        int insertIndex = sb.length() - 2;
        String tail = ">";
        if (sb.charAt(insertIndex) == '/') {
          --insertIndex;
          tail = " />";
        }
        // Remove redundant space
        while (sb.charAt(insertIndex) == ' ') {
          insertIndex--;
        }
        // Insert the generated id attribute
        sb.setLength(insertIndex + 1);
        sb.append(" id=\"" + tagInfo.getId() + "\"" + tail);
      }
      newTemplate.append(sb);

    }
    // Add the rest part of the template
    newTemplate.append(template.subSequence(lastIndex, template.length()));
    return newTemplate.toString();
  }

  /**
   * Generates a unique id value.
   *
   * @return the generated unique id value
   */
  @VisibleForTesting
  protected String generateUniqueId() {
    return idPrefix + (idCount++);
  }
}
