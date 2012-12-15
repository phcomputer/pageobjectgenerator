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

package com.google.testing.pogen;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import com.google.common.io.Files;
import com.google.testing.pogen.parser.template.HtmlTagInfo;
import com.google.testing.pogen.parser.template.TemplateInfo;
import com.google.testing.pogen.parser.template.TemplateParseException;
import com.google.testing.pogen.parser.template.TemplateParser;
import com.google.testing.pogen.parser.template.TemplateParsers;
import com.google.testing.pogen.parser.template.VariableInfo;

/**
 * A class which represents the list command to print template variables and ids.
 * 
 * @author Kazunori Sakamoto
 */
public class ListCommand extends Command {

  /**
   * Template paths to be parsed.
   */
  private final String[] templatePaths;
  /**
   * A name of the attribute to be assigned for tags containing template variables
   */
  private final String attributeName;

  /**
   * Constructs an instance with the specified template paths and the specified attribute name.
   * 
   * @param templatePaths the template paths to be parsed
   * @param attributeName the name of the attribute to be assigned for tags containing template
   *        variables
   */
  public ListCommand(String[] templatePaths, String attributeName) {
    this.attributeName = attributeName;
    this.templatePaths = Arrays.copyOf(templatePaths, templatePaths.length);
  }

  @Override
  public void execute() throws IOException {
    for (String templatePath : templatePaths) {
      TemplateParser templateParser =
          TemplateParsers.getPreferredParser(templatePath, attributeName);
      File templateFile = createFileFromFilePath(templatePath);
      checkExistenceAndPermission(templateFile, true, false);
      String template = Files.toString(templateFile, Charset.defaultCharset());
      try {
        TemplateInfo templateInfo = templateParser.parse(template);
        for (HtmlTagInfo tagInfo : templateInfo.getHtmlTagInfos()) {
          for (@SuppressWarnings("unused")
          VariableInfo varInfo : tagInfo.getVariableInfos()) {
            String id = tagInfo.hasAttributeValue() ? tagInfo.getAttributeValue() : "";
            System.out.print(templatePath + ", " + id);
            for (VariableInfo variableInfo : tagInfo.getVariableInfos()) {
              if (!variableInfo.isManipulableTag()) {
                System.out.print(", " + variableInfo.getName());
              }
            }
            System.out.println();
          }
        }
      } catch (TemplateParseException e) {
        throw new FileProcessException("Errors occur in parsing the specified file", templateFile,
            e);
      }
    }
  }
}
