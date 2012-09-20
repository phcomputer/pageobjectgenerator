package com.google.testing.pogen.generator.template;

import com.google.testing.pogen.parser.template.TemplateParser;

/**
 * A utility class to find the preferred {@link TemplateUpdater} instance for the attribute name.
 * 
 * @author Kazunori Sakamoto
 */
public class TemplateUpdaters {
  private TemplateUpdaters() {}

  /**
   * Returns the preferred {@link TemplateParser} instance for the specified attribute name.
   * 
   * @param attributeName the name of the attribute to be assigned for tags containing template
   *        variables
   * @return the preferred {@link TemplateParser} instance
   */
  public static TemplateUpdater getPreferredUpdater(String attributeName) {
    if (attributeName.equals("class")) {
      return new TemplateUpdaterWithClassAttribute();
    } else {
      return new TemplateUpdaterWithoutClassAttribute(attributeName);
    }
  }

  /**
   * Returns the preferred {@link TemplateParser} instance for the specified attribute name.
   * 
   * @param attributeName the name of the attribute to be assigned for tags containing template
   *        variables
   * @return the preferred {@link TemplateParser} instance
   */
  public static TemplateUpdater getPreferredUpdater(String attributeName,
      String attributeValuePrefix) {
    if (attributeName.equals("class")) {
      return new TemplateUpdaterWithClassAttribute(attributeValuePrefix);
    } else {
      return new TemplateUpdaterWithoutClassAttribute(attributeName, attributeValuePrefix);
    }
  }
}
