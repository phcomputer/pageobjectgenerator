package com.google.testing.pogen.generator.test.java;


/**
 * A utility class to find the preferred {@link TestCodeGenerator} instance for the attribute name.
 * 
 * @author Kazunori Sakamoto
 */
public class TestCodeGenerators {
  private TestCodeGenerators() {}

  /**
   * Returns the preferred {@link TestCodeGenerator} instance for the specified attribute name with
   * the default indent and the default new-line strings.
   * 
   * @param assignedAttributeName the assigned attribute name to find html elements containing
   *        template variables
   * @return the preferred {@link TestCodeGenerator} instance
   */
  public static TestCodeGenerator getPreferredGenerator(String assignedAttributeName) {
    if (assignedAttributeName.equals("class")) {
      return new TestCodeGeneratorWithClassAttribute();
    } else {
      return new TestCodeGeneratorWithoutClassAttribute(assignedAttributeName);
    }
  }

  /**
   * Returns the preferred {@link TestCodeGenerator} instance for the specified attribute name with
   * the specified indent and the specified new-line strings.
   * 
   * @param assignedAttributeName the assigned attribute name to find html elements containing
   *        template variables
   * @param indent the string of indent for generating source code
   * @param newLine the string of new line for generating source code
   * @return the preferred {@link TestCodeGenerator} instance
   */
  public static TestCodeGenerator getPreferredGenerator(String assignedAttributeName,
      String indent, String newLine) {
    if (assignedAttributeName.equals("class")) {
      return new TestCodeGeneratorWithClassAttribute(indent, newLine);
    } else {
      return new TestCodeGeneratorWithoutClassAttribute(assignedAttributeName, indent, newLine);
    }
  }
}
