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

package com.google.testing.pogen.measurer;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.testing.pogen.parser.template.TemplateInfo;
import com.google.testing.pogen.parser.template.TemplateParseException;
import com.google.testing.pogen.parser.template.soy.SoyParser;

/**
 * Tests for {@link VariableCoverageMeasurer}.
 * 
 * @author Kazunori Sakamoto
 */
@RunWith(JUnit4.class)
public class VariableCoverageMeasurerTest {
  private SoyParser soyParser;

  @Before
  public void setUp() {
    soyParser = new SoyParser("id");
  }

  @Test
  public void testMeasure() throws TemplateParseException {
    TemplateInfo info = soyParser.parse("<html>{$v1}</html>");
    VariableCoverage coverage = VariableCoverageMeasurer.measure(info);
    assertEquals(0, coverage.getVariableWithIdCount());
    assertEquals(1, coverage.getAllVariableCount());
  }

  @Test
  public void testMeasure2() throws TemplateParseException {
    TemplateInfo info = soyParser.parse("<html>{$v1}<p id='i1'>{$v2}</p></html>");
    VariableCoverage coverage = VariableCoverageMeasurer.measure(info);
    assertEquals(1, coverage.getVariableWithIdCount());
    assertEquals(2, coverage.getAllVariableCount());
  }

  @Test
  public void testMeasure3() throws TemplateParseException {
    TemplateInfo info = soyParser.parse("<html><p id='i1'>{$v1}{$v2}</p></html>");
    VariableCoverage coverage = VariableCoverageMeasurer.measure(info);
    assertEquals(2, coverage.getVariableWithIdCount());
    assertEquals(2, coverage.getAllVariableCount());
  }

  @Test
  public void testMeasureForSameVariables() throws TemplateParseException {
    TemplateInfo info = soyParser.parse("<html>{$v1}<p id='i1'>{$v1}</p></html>");
    VariableCoverage coverage = VariableCoverageMeasurer.measure(info);
    assertEquals(1, coverage.getVariableWithIdCount());
    assertEquals(2, coverage.getAllVariableCount());
  }
}
