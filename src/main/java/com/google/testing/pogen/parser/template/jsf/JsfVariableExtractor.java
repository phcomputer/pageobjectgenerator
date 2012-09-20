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

package com.google.testing.pogen.parser.template.jsf;

import java.util.regex.Pattern;

import com.google.testing.pogen.parser.template.RangeSet;
import com.google.testing.pogen.parser.template.RegexVariableExtractor;
import com.google.testing.pogen.parser.template.TemplateParseException;

/**
 * A class to extract template variables for JSF template engine.
 * 
 * @author Kazunori Sakamoto
 */
public class JsfVariableExtractor extends RegexVariableExtractor {

  /**
   * Constructs an instance to extract template variables with the specified positions of excluded
   * parts and the given attribute name for memorizing the value.
   * 
   * @param excludedRanges a {@link RangeSet} with the positions of excluded parts
   * @param attributeName a string of attribute name for memorizing the attribute value in tags
   * @throws TemplateParseException if the specified template is in bad format
   */
  public JsfVariableExtractor(RangeSet<Integer> excludedRanges, String attributeName)
      throws TemplateParseException {
    super(excludedRanges, attributeName);
  }

  @Override
  protected Pattern initializeVariablePattern() {
    return Pattern.compile("#\\{([^\"'][^{|]*[^\"']||[^{]*[^{\\s\"'][^{]*)\\}");
  }
}
