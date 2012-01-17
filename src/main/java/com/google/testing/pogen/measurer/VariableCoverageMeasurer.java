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

package com.google.testing.pogen.measurer;

import com.google.testing.pogen.parser.template.HtmlTagInfo;
import com.google.testing.pogen.parser.template.TemplateInfo;
import com.google.testing.pogen.parser.template.VariableInfo;

/**
 * A utility class to measure template-variable coverage from the specified
 * {@link TemplateInfo} instance.
 *
 * @author Kazunori Sakamoto
 */
public class VariableCoverageMeasurer {
  private VariableCoverageMeasurer() {
  }

  /**
   * Measures template-variable coverage from the specified {@link TemplateInfo}
   * instance. Note that same template variables in other html tags count as
   * other template variables.
   *
   * @param templateInfo the {@link TemplateInfo} instance to be measured
   * @return the {@link VariableCoverage} instance which stores the measurement
   *         result
   */
  public static VariableCoverage measure(TemplateInfo templateInfo) {
    int allVarCount = 0, varWithIdCount = 0;
    for (HtmlTagInfo tagInfo : templateInfo.getHtmlTagInfos()) {
      for (VariableInfo varInfo : tagInfo.getVariableInfos()) {
        if (tagInfo.hasId()) {
          varWithIdCount++;
        }
        allVarCount++;
      }
    }
    return new VariableCoverage(allVarCount, varWithIdCount);
  }
}
