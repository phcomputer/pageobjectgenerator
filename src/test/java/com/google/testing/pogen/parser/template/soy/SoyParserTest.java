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

package com.google.testing.pogen.parser.template.soy;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.Lists;
import com.google.testing.pogen.parser.template.HtmlTagInfo;
import com.google.testing.pogen.parser.template.RangeSet;
import com.google.testing.pogen.parser.template.TemplateParseException;
import com.google.testing.pogen.parser.template.VariableInfo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

/**
 * Tests for {@link SoyParser}.
 *
 * @author Kazunori Sakamoto
 */
@RunWith(JUnit4.class)
public class SoyParserTest {
  private SoyParser parser;

  @Before
  public void setUp() {
    parser = new SoyParser();
  }

  @Test
  public void testParseRepeatedPartWithoutCall() throws TemplateParseException {
    String template = "{$v1}{template .t1}{$v2}{/template}{$v3}{foreach}{$v4}{/foreach}{$v5}";
    RangeSet<Integer> ranges = parser.parseRepeatedPart(template);
    List<String> actual = getCommandsInRanges(template, ranges);
    List<String> expected = Arrays.asList("v4");
    assertEquals(expected, actual);
  }

  @Test
  public void testParseRepeatedPartWithCall() throws TemplateParseException {
    String template =
        "{$v1}{template .t1}{$v2}{/template}{$v3}{foreach}{$v4}{call .t1/}{/foreach}{$v5}";
    RangeSet<Integer> ranges = parser.parseRepeatedPart(template);
    List<String> actual = getCommandsInRanges(template, ranges);
    List<String> expected = Arrays.asList("v2", "v4");
    assertEquals(expected, actual);
  }

  @Test
  public void testParseRepeatedPartWithCall2() throws TemplateParseException {
    String template =
        "{$v1}{template .t1}{$v2}{/template}{$v3}{template .t2}{$v4}{/template}"
            + "{$v5}{foreach}{$v6}{call .t2/}{/foreach}{$v7}";
    RangeSet<Integer> ranges = parser.parseRepeatedPart(template);
    List<String> actual = getCommandsInRanges(template, ranges);
    List<String> expected = Arrays.asList("v4", "v6");
    assertEquals(expected, actual);
  }

  @Test
  public void testParseRepeatedPartWithCallChained() throws TemplateParseException {
    String template =
        "{$v1}{template .t1}{$v2}{/template}{$v3}{template .t2}{$v4}{call .t1/}{/template}"
            + "{$v5}{foreach}{$v6}{call .t2/}{/foreach}{$v7}";
    RangeSet<Integer> ranges = parser.parseRepeatedPart(template);
    List<String> actual = getCommandsInRanges(template, ranges);
    List<String> expected = Arrays.asList("v2", "v4", "v6");
    assertEquals(expected, actual);
  }

  @Test
  public void testParseExcludedPart() throws TemplateParseException {
    String template =
        "{$v1}{template .t1}{call .t1}{param a}{$v2}{/param}"
            + "{param b}{$v3}{/param}{/call}{/template}{$v4}";
    List<String> actual = getCommandsInRanges(template, null);
    List<String> expected = Arrays.asList("v1", "v4");
    assertEquals(expected, actual);
  }

  private static List<String> getCommandsInRanges(String template,
      @Nullable RangeSet<Integer> ranges) throws TemplateParseException {
    List<HtmlTagInfo> tags = new SoyParser().parseTagsContainingVariables(template);
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
