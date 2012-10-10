package com.google.testing.pogen.parser.template;

import com.google.testing.pogen.parser.template.ejs.EjsParser;
import com.google.testing.pogen.parser.template.erb.ErbParser;
import com.google.testing.pogen.parser.template.jsf.JsfParser;
import com.google.testing.pogen.parser.template.soy.SoyParser;

/**
 * A utility class to find the preferred {@link TemplateParser} instance for the template file.
 * 
 * @author Kazunori Sakamoto
 */
public class TemplateParsers {
  private TemplateParsers() {}

  /**
   * Returns the preferred {@link TemplateParser} instance for the specified path of the template
   * file and the given attribute name.
   * 
   * @param templatePath the path of the template file to be find
   * @param attributeName the name of the attribute to be assigned for tags containing template
   *        variables
   * @return the preferred {@link TemplateParser} instance
   */
  public static TemplateParser getPreferredParser(String templatePath, String attributeName) {
    if (templatePath.endsWith(".ejs")) {
      return new EjsParser(attributeName);
    } else if (templatePath.endsWith(".erb")) {
      return new ErbParser(attributeName);
    } else if (templatePath.endsWith(".xhtml")) {
      return new JsfParser(attributeName);
    } else {
      return new SoyParser(attributeName);
    }
  }
}
