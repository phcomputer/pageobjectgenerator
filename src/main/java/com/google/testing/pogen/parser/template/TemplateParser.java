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

import com.google.common.base.Preconditions;

import java.util.List;

/**
 * A class to parse a template to retrieve the {@link TemplateInfo} instance.
 *
 * @author Kazunori Sakamoto
 */
public abstract class TemplateParser {

  /**
   * Retrieves the information of the specified template by parsing it.
   *
   * @param template the template to be parsed
   * @return the information of the specified template
   * @throws TemplateParseException if the specified template is in bad format
   */
  public TemplateInfo parse(String template) throws TemplateParseException {
    Preconditions.checkNotNull(template);

    List<HtmlTagInfo> htmlTagInfos = parseTagsContainingVariables(template);
    RangeSet<Integer> repeatedParts = parseRepeatedPart(template);
    return new TemplateInfo(template, htmlTagInfos, repeatedParts);
  }

  /**
   * Retrieves an information list of html tags which contain template variables
   * by parsing the specified template.
   *
   * @param template the string of the template to be parsed
   * @return the information list of html tags which contain template variables
   * @throws TemplateParseException if the specified template is in bad format
   */
  protected abstract List<HtmlTagInfo> parseTagsContainingVariables(String template)
      throws TemplateParseException;

  /**
   * Retrieves a {@link RangeSet} of indexes where the repeated part are located
   * in the specified template. Note that repeated part is inner part of
   * for/foreach tags and of template tags called from another repeated part.
   * For example, {template .t1}repeated part{/template}{foreach ...}repeated
   * part{call .t1}{/foreach}.
   *
   * @param template the string of the template to be parsed
   * @return the {@link RangeSet} of the indexes where repeated part are located
   * @throws TemplateParseException if the specified template is in bad format
   */
  protected abstract RangeSet<Integer> parseRepeatedPart(String template)
      throws TemplateParseException;
}
