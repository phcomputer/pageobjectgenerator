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

package com.google.testing.pogen.generator.test.java;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.testing.pogen.generator.template.TemplateUpdater;
import com.google.testing.pogen.generator.template.TemplateUpdaters;
import com.google.testing.pogen.parser.template.TemplateInfo;
import com.google.testing.pogen.parser.template.TemplateParseException;
import com.google.testing.pogen.parser.template.soy.SoyParser;

// @formatter:off
/**
 * Tests for {@link TestCodeGenerator}.
 *
 * @author Kazunori Sakamoto
 */
@RunWith(JUnit4.class)
public class TestCodeGeneratorWithoutClassAttributeTest {

  private static final String CLASS_HEAD =
      "public class TestPage extends AbstractPage {\n"
          + "  public TestPage(WebDriver driver) {\n"
          + "    super(driver);\n"
          + "    assertInvariant();\n"
          + "  }\n\n"
          + "  private void assertInvariant() {\n"
          + "  }\n\n"
          + "  /* ------------------- GENERATED CODE START ------------------- */\n";
  private static final String CLASS_TAIL = "\n"
      + "  /* -------------------- GENERATED CODE END -------------------- */\n}\n";

  private static final String HEAD =
      "package ;\n\n"
          + "import static org.junit.Assert.*;\n"
          + "import static org.hamcrest.Matchers.*;\n\n"
          + "import org.openqa.selenium.By;\n"
          + "import org.openqa.selenium.WebDriver;\n"
          + "import org.openqa.selenium.WebElement;\n"
          + "import org.openqa.selenium.support.FindBy;\n"
          + "import org.openqa.selenium.support.How;\n\n"
          + "import java.util.ArrayList;\n"
          + "import java.util.List;\n\n"
          + CLASS_HEAD;
  private static final String TAIL = CLASS_TAIL;

  private TemplateUpdater updater;
  private SoyParser parser;
  private TestCodeGenerator generator;

  @Before
  public void setUp() {
    String attributeName = "id";
    updater = TemplateUpdaters.getPreferredUpdater(attributeName, "_");
    parser = new SoyParser(attributeName);
    generator = TestCodeGenerators.getPreferredGenerator(attributeName, "  ", "\n");
  }

  @Test
  public void generateForAnEnclosure() throws TemplateParseException {
    TemplateInfo templateInfo =
        parser.parse("<html><head><title>{$title}</title></head><body>test</body></html>");
    updater.generate(templateInfo);
    String actual = generator.generate(templateInfo, "", "Test");
    String expected = HEAD
        + "  @FindBy(how = How.XPATH, using = \"//*[@id='_0']\")\n"
        + "  private WebElement title;\n\n"
        + "  public WebElement getElementForTitle() {\n"
        + "    return title;\n"
        + "  }\n\n"
        + "  public String getTextForTitle() {\n"
        + "    return title.getText();\n"
        + "  }" + TAIL;
    assertEquals(expected, actual);
  }

  @Test
  public void generateForAnEnclosure2() throws TemplateParseException {
    TemplateInfo templateInfo = parser.parse("<html><body>{$content}</body></html>");
    updater.generate(templateInfo);
    String actual = generator.generate(templateInfo, "", "Test");
    String expected = HEAD
        + "  @FindBy(how = How.XPATH, using = \"//*[@id='_0']\")\n"
        + "  private WebElement content;\n\n"
        + "  public WebElement getElementForContent() {\n"
        + "    return content;\n"
        + "  }\n\n"
        + "  public String getTextForContent() {\n"
        + "    return content.getText();\n"
        + "  }" + TAIL;
    assertEquals(expected, actual);
  }

  @Test
  public void generateForAnEnclosureUsingProhibittedChars() throws TemplateParseException {
    TemplateInfo templateInfo =
        parser.parse("<html><head><title>{$p.title}</title></head><body>test</body></html>");
    updater.generate(templateInfo);
    String actual = generator.generate(templateInfo, "", "Test");
    String expected = HEAD
        + "  @FindBy(how = How.XPATH, using = \"//*[@id='_0']\")\n"
        + "  private WebElement p_title;\n\n"
        + "  public WebElement getElementForP_title() {\n"
        + "    return p_title;\n"
        + "  }\n\n"
        + "  public String getTextForP_title() {\n"
        + "    return p_title.getText();\n"
        + "  }" + TAIL;
    assertEquals(expected, actual);
  }

  @Test
  public void generateForAnEnclosureUsingOption() throws TemplateParseException {
    TemplateInfo templateInfo =
        parser
        .parse("<html><head><title>{$title|escapeUri}</title></head><body>test</body></html>");
    updater.generate(templateInfo);
    String actual = generator.generate(templateInfo, "", "Test");
    String expected = HEAD
        + "  @FindBy(how = How.XPATH, using = \"//*[@id='_0']\")\n"
        + "  private WebElement title;\n\n"
        + "  public WebElement getElementForTitle() {\n"
        + "    return title;\n"
        + "  }\n\n"
        + "  public String getTextForTitle() {\n"
        + "    return title.getText();\n"
        + "  }" + TAIL;
    assertEquals(expected, actual);
  }

  @Test
  public void generateForAnEnclosureContainingTwoDiffVars() throws TemplateParseException {
    TemplateInfo templateInfo = parser.parse("<html><body>{$content1}{$content2}</body></html>");
    updater.generate(templateInfo);
    String actual = generator.generate(templateInfo, "", "Test");
    String expected = HEAD
        + "  @FindBy(how = How.XPATH, using = \"//*[@id='_0']\")\n"
        + "  private WebElement content1;\n"
        + "  @FindBy(how = How.XPATH, using = \"//*[@id='_0']\")\n"
        + "  private WebElement content2;\n\n"
        + "  public WebElement getElementForContent1() {\n"
        + "    return content1;\n"
        + "  }\n\n"
        + "  public String getTextForContent1() {\n"
        + "    return content1.getText();\n"
        + "  }\n\n"
        + "  public WebElement getElementForContent2() {\n"
        + "    return content2;\n"
        + "  }\n\n"
        + "  public String getTextForContent2() {\n"
        + "    return content2.getText();\n"
        + "  }" + TAIL;
    assertEquals(expected, actual);
  }

  @Test
  public void generateForAnEnclosureContainingTwoSameVars() throws TemplateParseException {
    TemplateInfo templateInfo = parser.parse("<html><body>{$content}{$content}</body></html>");
    updater.generate(templateInfo);
    String actual = generator.generate(templateInfo, "", "Test");
    String expected = HEAD
        + "  @FindBy(how = How.XPATH, using = \"//*[@id='_0']\")\n"
        + "  private WebElement content;\n\n"
        + "  public WebElement getElementForContent() {\n"
        + "    return content;\n"
        + "  }\n\n"
        + "  public String getTextForContent() {\n"
        + "    return content.getText();\n  }" + TAIL;
    assertEquals(expected, actual);
  }

  @Test
  public void generateForTwoEnclosuresContainingSameVars() throws TemplateParseException {
    TemplateInfo templateInfo =
        parser.parse("<html><body><p>{$content}<p>{$content}</body></html>");
    updater.generate(templateInfo);
    String actual = generator.generate(templateInfo, "", "Test");
    String expected = HEAD
        + "  @FindBy(how = How.XPATH, using = \"//*[@id='_0']\")\n"
        + "  private WebElement content;\n"
        + "  @FindBy(how = How.XPATH, using = \"//*[@id='_1']\")\n"
        + "  private WebElement content2;\n\n"
        + "  public WebElement getElementForContent() {\n"
        + "    return content;\n"
        + "  }\n\n"
        + "  public String getTextForContent() {\n"
        + "    return content.getText();\n"
        + "  }\n\n"
        + "  public WebElement getElementForContent2() {\n"
        + "    return content2;\n"
        + "  }\n\n"
        + "  public String getTextForContent2() {\n"
        + "    return content2.getText();\n"
        + "  }" + TAIL;
    assertEquals(expected, actual);
  }

  @Test
  public void generateForEnclosuresContainingTwoVarsWithTextAndAttr()
      throws TemplateParseException {
    TemplateInfo templateInfo =
        parser.parse("<html><body attr='{$content1}'>{$content2}</body></html>");
    updater.generate(templateInfo);
    String actual = generator.generate(templateInfo, "", "Test");
    String expected = HEAD
        + "  @FindBy(how = How.XPATH, using = \"//*[@id='_0']\")\n"
        + "  private WebElement content1;\n"
        + "  @FindBy(how = How.XPATH, using = \"//*[@id='_0']\")\n"
        + "  private WebElement content2;\n\n"
        + "  public WebElement getElementForContent1() {\n"
        + "    return content1;\n"
        + "  }\n\n"
        + "  public String getAttrAttributeForContent1() {\n"
        + "    return content1.getAttribute(\"attr\");\n"
        + "  }\n\n"
        + "  public WebElement getElementForContent2() {\n"
        + "    return content2;\n"
        + "  }\n\n"
        + "  public String getTextForContent2() {\n"
        + "    return content2.getText();\n"
        + "  }" + TAIL;
    assertEquals(expected, actual);
  }

  @Test
  public void generateForDistantEnclosures() throws TemplateParseException {
    TemplateInfo templateInfo = parser.parse("<html><body><p></p>{$content}</body></html>");
    updater.generate(templateInfo);
    String actual = generator.generate(templateInfo, "", "Test");
    String expected = HEAD
        + "  @FindBy(how = How.XPATH, using = \"//*[@id='_0']\")\n"
        + "  private WebElement content;\n\n"
        + "  public WebElement getElementForContent() {\n"
        + "    return content;\n"
        + "  }\n\n"
        + "  public String getTextForContent() {\n"
        + "    return content.getText();\n"
        + "  }" + TAIL;
    assertEquals(expected, actual);
  }

  @Test
  public void treatExistingId() throws TemplateParseException {
    TemplateInfo templateInfo =
        parser.parse("<html><body id='content'><p></p>{$content}</body></html>");
    updater.generate(templateInfo);
    String actual = generator.generate(templateInfo, "", "Test");
    String expected = HEAD
        + "  @FindBy(how = How.XPATH, using = \"//*[@id='content']\")\n"
        + "  private WebElement content;\n\n"
        + "  public WebElement getElementForContent() {\n"
        + "    return content;\n"
        + "  }\n\n"
        + "  public String getTextForContent() {\n"
        + "    return content.getText();\n"
        + "  }" + TAIL;
    assertEquals(expected, actual);
  }

  @Test
  public void treatExistingId2() throws TemplateParseException {
    TemplateInfo templateInfo =
        parser.parse("<html><body id='content'><p></p><p>{$content}</p></body></html>");
    updater.generate(templateInfo);
    String actual = generator.generate(templateInfo, "", "Test");
    String expected = HEAD
        + "  @FindBy(how = How.XPATH, using = \"//*[@id='_0']\")\n"
        + "  private WebElement content;\n\n"
        + "  public WebElement getElementForContent() {\n"
        + "    return content;\n"
        + "  }\n\n"
        + "  public String getTextForContent() {\n"
        + "    return content.getText();\n"
        + "  }" + TAIL;
    assertEquals(expected, actual);
  }

  @Test
  public void treatConflictedVariableNames() throws TemplateParseException {
    TemplateInfo templateInfo =
        parser.parse("<html><p>{$content}</p><p>{$content}{$content2}</p></body></html>");
    updater.generate(templateInfo);
    String actual = generator.generate(templateInfo, "", "Test");
    // TODO(kazuu): Deal with conflicted fileds and methods.
    String expected = HEAD
        + "  @FindBy(how = How.XPATH, using = \"//*[@id='_0']\")\n"
        + "  private WebElement content;\n"
        + "  @FindBy(how = How.XPATH, using = \"//*[@id='_1']\")\n"
        + "  private WebElement content2;\n"
        + "  @FindBy(how = How.XPATH, using = \"//*[@id='_1']\")\n"
        + "  private WebElement content2;\n\n"
        + "  public WebElement getElementForContent() {\n"
        + "    return content;\n"
        + "  }\n\n"
        + "  public String getTextForContent() {\n"
        + "    return content.getText();\n"
        + "  }\n\n"
        + "  public WebElement getElementForContent2() {\n"
        + "    return content2;\n"
        + "  }\n\n"
        + "  public String getTextForContent2() {\n"
        + "    return content2.getText();\n"
        + "  }\n\n"
        + "  public WebElement getElementForContent2() {\n"
        + "    return content2;\n"
        + "  }\n\n"
        + "  public String getTextForContent2() {\n"
        + "    return content2.getText();\n"
        + "  }" + TAIL;
    assertEquals(expected, actual);
  }

  @Test
  public void treatVariableInAttribute() throws TemplateParseException {
    TemplateInfo templateInfo = parser.parse("<html><body><a href='{$url}'></a></body></html>");
    updater.generate(templateInfo);
    String actual = generator.generate(templateInfo, "", "Test");
    String expected = HEAD
        + "  @FindBy(how = How.XPATH, using = \"//*[@id='_0']\")\n"
        + "  private WebElement url;\n\n"
        + "  public WebElement getElementForUrl() {\n"
        + "    return url;\n"
        + "  }\n\n"
        + "  public String getHrefAttributeForUrl() {\n"
        + "    return url.getAttribute(\"href\");\n"
        + "  }" + TAIL;
    assertEquals(expected, actual);
  }

  @Test
  public void treatVariableInAttributeAndText() throws TemplateParseException {
    TemplateInfo templateInfo =
        parser.parse("<html><body><a href='{$url}'>{$url}</a></body></html>");
    updater.generate(templateInfo);
    String actual = generator.generate(templateInfo, "", "Test");
    String expected = HEAD
        + "  @FindBy(how = How.XPATH, using = \"//*[@id='_0']\")\n"
        + "  private WebElement url;\n\n"
        + "  public WebElement getElementForUrl() {\n"
        + "    return url;\n"
        + "  }\n\n"
        + "  public String getTextForUrl() {\n"
        + "    return url.getText();\n"
        + "  }\n\n"
        + "  public String getHrefAttributeForUrl() {\n"
        + "    return url.getAttribute(\"href\");\n"
        + "  }" + TAIL;
    assertEquals(expected, actual);
  }

  @Test
  public void treatRepeatedPart() throws TemplateParseException {
    TemplateInfo templateInfo =
        parser.parse("<html>{foreach $url in $urls}<a href='{$url}'>{$url}</a>{/foreach}</html>");
    updater.generate(templateInfo);
    String actual = generator.generate(templateInfo, "", "Test");
    // TODO(kazuu): Don't use the same id attribute for one more html tags
    String expected = HEAD + "\n"
        + "  public List<WebElement> getElementsForUrl() {\n"
        + "    List<WebElement> result = new ArrayList<WebElement>();\n"
        + "    for (WebElement e : driver.findElements(By.xpath(\"//*[@id='_0']\"))) {\n"
        + "      result.add(e);\n"
        + "    }\n"
        + "    return result;\n"
        + "  }\n\n"
        + "  public List<String> getTextsForUrl() {\n"
        + "    List<String> result = new ArrayList<String>();\n"
        + "    for (WebElement e : driver.findElements(By.xpath(\"//*[@id='_0']\"))) {\n"
        + "      result.add(e.getText());\n"
        + "    }\n"
        + "    return result;\n"
        + "  }\n\n"
        + "  public List<String> getHrefAttributesForUrl() {\n"
        + "    List<String> result = new ArrayList<String>();\n"
        + "    for (WebElement e : driver.findElements(By.xpath(\"//*[@id='_0']\"))) {\n"
        + "      result.add(e.getAttribute(\"href\"));\n"
        + "    }\n"
        + "    return result;\n"
        + "  }" + TAIL;
    assertEquals(expected, actual);
  }

  @Test
  public void treatExcludedPart() throws TemplateParseException {
    TemplateInfo templateInfo =
        parser.parse("<html>{call .t1}{$v1}{/call}<a>{$v2}</a></html>");
    updater.generate(templateInfo);
    String actual = generator.generate(templateInfo, "", "Test");
    String expected = HEAD
        + "  @FindBy(how = How.XPATH, using = \"//*[@id='_0']\")\n"
        + "  private WebElement v2;\n\n"
        + "  public WebElement getElementForV2() {\n"
        + "    return v2;\n"
        + "  }\n\n"
        + "  public String getTextForV2() {\n"
        + "    return v2.getText();\n"
        + "  }" + TAIL;
    assertEquals(expected, actual);
  }
}
