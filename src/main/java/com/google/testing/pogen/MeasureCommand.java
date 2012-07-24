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
import com.google.testing.pogen.measurer.VariableCoverage;
import com.google.testing.pogen.measurer.VariableCoverageMeasurer;
import com.google.testing.pogen.parser.template.TemplateParseException;
import com.google.testing.pogen.parser.template.TemplateParser;
import com.google.testing.pogen.parser.template.ejs.EjsParser;
import com.google.testing.pogen.parser.template.soy.SoyParser;

/**
 * A class which represents the measure command to print the measurement result of template-variable
 * coverage.
 * 
 * @author Kazunori Sakamoto
 */
public class MeasureCommand extends Command {

  /**
   * Template paths to be parsed.
   */
  private final String[] templatePaths;
  /**
   * A boolean whether prints processed files verbosely.
   */
  private final boolean verbose;

  /**
   * Constructs an instance with the specified template paths.
   * 
   * @param templatePaths the template paths to be parsed
   * @param verbose the boolean whether prints processed files verbosely
   */
  public MeasureCommand(String[] templatePaths, boolean verbose) {
    this.templatePaths = Arrays.copyOf(templatePaths, templatePaths.length);
    this.verbose = verbose;
  }

  @Override
  public void execute() throws IOException {
    int sumAllVariableCount = 0, sumVariableWithIdCount = 0;
    for (String templatePath : templatePaths) {
      TemplateParser templateParser;
      if (templatePath.endsWith(".ejs")) {
        templateParser = new EjsParser();
      } else {
        templateParser = new SoyParser();
      }
      File templateFile = createFileFromFilePath(templatePath, true, false);
      String template = Files.toString(templateFile, Charset.defaultCharset());
      try {
        VariableCoverage result = VariableCoverageMeasurer.measure(templateParser.parse(template));
        sumAllVariableCount += result.getAllVariableCount();
        sumVariableWithIdCount += result.getVariableWithIdCount();
        if (verbose) {
          System.out.format("%.2f%% : %s", result.getCoverage() * 100,
              templateFile.getAbsolutePath());
        }
      } catch (TemplateParseException e) {
        throw new FileProcessException("Errors occur in parsing the specified file", templateFile,
            e);
      }
    }
    if (sumAllVariableCount > 0) {
      System.out.format("Summary: %.2f%% (%d / %d)", (double) sumVariableWithIdCount
          / sumAllVariableCount * 100, sumVariableWithIdCount, sumAllVariableCount);
    } else {
      System.out.println("Summary: no template variables were found.");
    }
  }
}
