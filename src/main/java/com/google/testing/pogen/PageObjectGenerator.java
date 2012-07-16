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

import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * A main class of PageObjectGenerator. The PageObjectGenerator is a tool to generate modified
 * template files inserting id attributes and skeleton test code designed by PageObject pattern. The
 * skeleton test code has getter methods for html elements which contain template variables by using
 * Selenium2 (WebDriver). So it reduces testers task such as inserting id attributes and writing
 * test code.
 * 
 * Currently only soy templates are supported.
 * 
 * @author Kazunori Sakamoto
 */
public class PageObjectGenerator {
  /**
   * A name of generate command.
   */
  private static final String GENERATE_COMMAND = "generate";

  /**
   * A name of measure command.
   */
  private static final String MEASURE_COMMAND = "measure";

  private PageObjectGenerator() {}

  // Apache's OptionBuilder has static Builder pattern
  @SuppressWarnings("static-access")
  public static void main(String[] args) {
    if (args.length == 0) {
      printUsage(System.out);
      return;
    }

    String commandName = args[0];
    // @formatter:off
    Options options = new Options()
        .addOption(OptionBuilder
            .withDescription("Print help for this command.")
            .create('h'))
        .addOption(OptionBuilder
            .withDescription("Print processed files verbosely.")
            .create('v'));
    // @formatter:on

    if (commandName.equals(GENERATE_COMMAND)) {
      // @formatter:off
      options
          .addOption(OptionBuilder
              .withDescription("Package name of generating skeleton test code.")
              .hasArg()
              .isRequired()
              .create('p'))
          .addOption(OptionBuilder
              .withDescription("Output directory of generating skeleton test code.")
              .hasArg()
              .isRequired()
              .create('o'));
      // @formatter:on
    } else if (commandName.equals(MEASURE_COMMAND)) {
      // No special options are required
    } else {
      System.err.format("'%s' is not a PageObjectGenerator command.", commandName);
      printUsage(System.err);
      System.exit(-1);
    }

    BasicParser cmdParser = new BasicParser();
    try {
      CommandLine cl = cmdParser.parse(options, Arrays.copyOfRange(args, 1, args.length));
      Command command = null;
      String[] templatePaths = cl.getArgs();
      if (commandName.equals(GENERATE_COMMAND)) {
        command =
            new GenerateCommand(templatePaths, cl.getOptionValue('o'), cl.getOptionValue('p'),
                cl.hasOption('v'));
      } else if (commandName.equals(MEASURE_COMMAND)) {
        command = new MeasureCommand(templatePaths, cl.hasOption('v'));
      }
      if (cl.hasOption('h') || templatePaths.length == 0) {
        HelpFormatter f = new HelpFormatter();
        f.printHelp(command.getHelpMessage(), options);
        return;
      }
      try {
        command.execute();
        return;
      } catch (FileProcessException e) {
        System.err.println(e.getMessage());
      } catch (IOException e) {
        System.err.println("Errors occur in processing files.");
        System.err.println(e.getMessage());
      }
    } catch (ParseException e) {
      System.err.println("Errors occur in parsing the command arguments.");
      System.err.println(e.getMessage());
    }
    System.exit(-1);
  }

  /**
   * Prints the usage of the PageObjectGenerator with the specified {@link PrintStream} instance.
   * 
   * @param printStream the {@link PrintStream} to print
   */
  private static void printUsage(PrintStream printStream) {
    printStream.println("usage: java PageObjectGenerator COMMAND [ARGS]");
    printStream.println("The commands are:");
    printStream.format("   %-10s Generate modified templates and skeleton test code\n",
        GENERATE_COMMAND);
    printStream.format("   %-10s Measure template-variable coverage\n", MEASURE_COMMAND);
  }
}
