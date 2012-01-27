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

import static org.junit.Assert.assertEquals;

import com.google.testing.pogen.parser.template.TemplateParseException;
import com.google.testing.pogen.parser.template.soy.SoyParser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests for {@link TemplateUpdater}.
 *
 * @author Kazunori Sakamoto
 */
@RunWith(JUnit4.class)
public class TemplateUpdaterTest {
  private TemplateUpdater updater;
  private SoyParser parser;

  class TemplateUpdaterForTest extends TemplateUpdater {
    private int idCount = 0;

    @Override
    protected String generateUniqueId() {
      return "_" + (idCount++);
    }
  }

  @Before
  public void setUp() {
    updater = new TemplateUpdaterForTest();
    parser = new SoyParser();
  }

  @Test
  public void dealWithVariableInToplevel() throws TemplateParseException {
    String actual = updater.generate(parser.parse("{$title}<html>test</html>"));
    assertEquals("{$title}<html>test</html>", actual);
  }

  @Test
  public void insertIdIntoEnclosure() throws TemplateParseException {
    String actual =
        updater.generate(parser
            .parse("<html><head><title>{$title}</title></head><body>test</body></html>"));
    String expected =
        "<html><head><title id=\"_0\">{$title}</title></head><body>test</body></html>";
    assertEquals(expected, actual);
  }

  @Test
  public void insertIdIntoEnclosure2() throws TemplateParseException {
    String actual = updater.generate(parser.parse("<html><body>{$content}</body></html>"));
    String expected = "<html><body id=\"_0\">{$content}</body></html>";
    assertEquals(expected, actual);
  }

  @Test
  public void insertIdIntoEnclosureCombiningStartEnd() throws TemplateParseException {
    String actual = updater.generate(parser.parse("<html><img src='{$url}' /></html>"));
    String expected = "<html><img src='{$url}' id=\"_0\" /></html>";
    assertEquals(expected, actual);
  }

  @Test
  public void insertIdIntoEnclosureCombiningStartEndWithRedundantSpace()
      throws TemplateParseException {
    String actual = updater.generate(parser.parse("<html><img src='{$url}'        /></html>"));
    String expected = "<html><img src='{$url}' id=\"_0\" /></html>";
    assertEquals(expected, actual);
  }

  @Test
  public void insertIdIntoEnclosureUsingProhibittedChars() throws TemplateParseException {
    String actual =
        updater.generate(parser
            .parse("<html><head><title>{$p.title}</title></head><body>test</body></html>"));
    String expected =
        "<html><head><title id=\"_0\">{$p.title}</title></head><body>test</body></html>";
    assertEquals(expected, actual);
  }

  @Test
  public void insertIdIntoEnclosureUsingOption() throws TemplateParseException {
    String actual =
        updater.generate(parser
            .parse("<html><head><title>{$title|escapeUri}</title></head><body>test</body></html>"));
    String expected =
        "<html><head><title id=\"_0\">{$title|escapeUri}</title></head><body>test</body></html>";
    assertEquals(expected, actual);
  }

  @Test
  public void insertIdIntoEnclosureContainingTwoDiffVars() throws TemplateParseException {
    String actual =
        updater.generate(parser.parse("<html><body>{$content1}{$content2}</body></html>"));
    String expected = "<html><body id=\"_0\">{$content1}{$content2}</body></html>";
    assertEquals(expected, actual);
  }

  @Test
  public void insertIdIntoEnclosureContainingTwoSameVars() throws TemplateParseException {
    String actual =
        updater.generate(parser.parse("<html><body>{$content}{$content}</body></html>"));
    String expected = "<html><body id=\"_0\">{$content}{$content}</body></html>";
    assertEquals(expected, actual);
  }

  @Test
  public void insertIdIntoTwoEnclosuresContainingSameVars() throws TemplateParseException {
    String actual =
        updater.generate(parser.parse("<html><body><p>{$content}<p>{$content}</body></html>"));
    String expected = "<html><body><p id=\"_0\">{$content}<p id=\"_1\">{$content}</body></html>";
    assertEquals(expected, actual);
  }

  @Test
  public void insertIdIntoEnclosuresContainingTwoVarsWithTextAndAttr()
      throws TemplateParseException {
    String actual =
        updater.generate(parser.parse("<html><body attr='{$content1}'>{$content2}</body></html>"));
    String expected = "<html><body attr='{$content1}' id=\"_0\">{$content2}</body></html>";
    assertEquals(expected, actual);
  }

  @Test
  public void insertIdIntoDistantEnclosures() throws TemplateParseException {
    String actual = updater.generate(parser.parse("<html><body><p></p>{$content}</body></html>"));
    String expected = "<html><body id=\"_0\"><p></p>{$content}</body></html>";
    assertEquals(expected, actual);
  }

  @Test
  public void treatExistingId() throws TemplateParseException {
    String actual =
        updater.generate(parser.parse("<html><body id='content'><p></p>{$content}</body></html>"));
    String expected = "<html><body id='content'><p></p>{$content}</body></html>";
    assertEquals(expected, actual);
  }

  @Test
  public void treatVariableInAttribute() throws TemplateParseException {
    String actual =
        updater.generate(parser.parse("<html><body><a href='{$url}'></a></body></html>"));
    String expected = "<html><body><a href='{$url}' id=\"_0\"></a></body></html>";
    assertEquals(expected, actual);
  }

  @Test
  public void treatVariableInAttributeAndText() throws TemplateParseException {
    String actual =
        updater.generate(parser.parse("<html><body><a href='{$url}'>{$url}</a></body></html>"));
    String expected = "<html><body><a href='{$url}' id=\"_0\">{$url}</a></body></html>";
    assertEquals(expected, actual);
  }

  @Test
  public void treatRepeatedPart() throws TemplateParseException {
    // TODO(kazuu): Don't use the same id attribute for one more html tags
    String actual =
        updater.generate(parser
            .parse("<html>{foreach $url in $urls}<a href='{$url}'>{$url}</a>{/foreach}</html>"));
    String expected =
        "<html>{foreach $url in $urls}<a href='{$url}' id=\"_0\">{$url}</a>{/foreach}</html>";
    assertEquals(expected, actual);
  }
}
