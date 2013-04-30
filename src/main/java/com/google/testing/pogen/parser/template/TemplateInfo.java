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

package com.google.testing.pogen.parser.template;

import java.util.Collections;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.RangeSet;

/**
 * A class to contain the information of a template such as template content and
 * html tags which contain template variables.
 *
 * @author Kazunori Sakamoto
 */
public class TemplateInfo {
  /**
   * A string of template content.
   */
  private final String template;
  /**
   * A list of the information of html tags which contain template variables.
   */
  private final List<HtmlTagInfo> htmlTagInfos;
  /**
   * A set of positions of repeated parts such as "{template .t1}repeated
   * part{/template}{foreach ...}repeated part{call .t1}{/foreach}".
   */
  private final RangeSet<Integer> repeatedRanges;

  /**
   * Constructs an instance with the specified template and information of html
   * tags.
   *
   * @param template the string of template content
   * @param htmlTagInfos the list of the information of html which contain
   *        template variables
   * @param repeatedRanges the {@link RangeSet} where repeated part is located
   */
  public TemplateInfo(String template, List<HtmlTagInfo> htmlTagInfos,
      RangeSet<Integer> repeatedRanges) {
    Preconditions.checkNotNull(template);
    Preconditions.checkNotNull(htmlTagInfos);
    Preconditions.checkNotNull(repeatedRanges);

    this.template = template;
    this.htmlTagInfos = Collections.unmodifiableList(htmlTagInfos);
    this.repeatedRanges = repeatedRanges;
  }

  /**
   * Returns {@code true} if the specified html tag appears in repeated part.
   *
   * @param htmlTagInfo the {@link HtmlTagInfo} instance of the html tag to be
   *        checked
   * @return {@code true} if the specified html tag appears in repeated part
   */
  public boolean isRepeated(HtmlTagInfo htmlTagInfo) {
    return repeatedRanges.contains(htmlTagInfo.getStartIndex());
  }

  public String getTemplate() {
    return template;
  }

  public List<HtmlTagInfo> getHtmlTagInfos() {
    return htmlTagInfos;
  }
}
