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

package com.google.testing.pogen.parser.template;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.RangeSet;

/**
 * A utility class for parser tests.
 * 
 * @author Kazunori Sakamoto
 */
public class ParserTestUtil {
  private ParserTestUtil() {}

  public static List<String> getCommands(TemplateParser parser, String template)
      throws TemplateParseException {
    return getCommandsInRanges(parser, template, null);
  }

  public static List<String> getCommandsInRanges(TemplateParser parser, String template,
      @Nullable RangeSet<Integer> ranges) throws TemplateParseException {
    List<HtmlTagInfo> tags = parser.parseTagsContainingVariables(template);
    List<String> commands = Lists.newArrayList();
    for (HtmlTagInfo tag : tags) {
      for (VariableInfo var : tag.getVariableInfos()) {
        if (ranges == null || ranges.contains(var.getStartIndex())) {
          commands.add(var.getName());
        }
      }
    }
    Collections.sort(commands);
    return commands;
  }
}
