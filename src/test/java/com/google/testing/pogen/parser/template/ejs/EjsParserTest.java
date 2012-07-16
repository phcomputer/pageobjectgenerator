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

package com.google.testing.pogen.parser.template.ejs;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.testing.pogen.parser.template.ParserTestUtil;
import com.google.testing.pogen.parser.template.RangeSet;
import com.google.testing.pogen.parser.template.TemplateParseException;
import com.google.testing.pogen.parser.template.soy.SoyParser;

/**
 * Tests for {@link SoyParser}.
 * 
 * @author Kazunori Sakamoto
 */
@RunWith(JUnit4.class)
public class EjsParserTest {
  private EjsParser parser;

  @Before
  public void setUp() {
    parser = new EjsParser();
  }

  @Test
  public void testParseVariables() throws TemplateParseException {
    String template = "<%=v1%>abc<%=v2%><%=v3%>";
    List<String> actual = ParserTestUtil.getCommands(parser, template);
    List<String> expected = Arrays.asList("v1", "v2", "v3");
    assertEquals(expected, actual);
  }

  @Test
  public void testParseRepeatedPart() throws TemplateParseException {
    String template = "<%=v1%><%for(var i=0; i<10; i++){%><%=v2%><%}%><%=v3%>";
    RangeSet<Integer> ranges = parser.parseRepeatedPart(template);
    List<String> actual = ParserTestUtil.getCommandsInRanges(parser, template, ranges);
    List<String> expected = Arrays.asList("v2");
    assertEquals(expected, actual);
  }
}
