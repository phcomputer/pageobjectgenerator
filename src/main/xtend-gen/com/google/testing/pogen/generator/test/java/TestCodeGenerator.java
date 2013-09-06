package com.google.testing.pogen.generator.test.java;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultiset;
import com.google.testing.pogen.generator.test.PageObjectUpdateException;
import com.google.testing.pogen.parser.template.HtmlTagInfo;
import com.google.testing.pogen.parser.template.TemplateInfo;
import com.google.testing.pogen.parser.template.VariableInfo;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

@SuppressWarnings("all")
public abstract class TestCodeGenerator {
  /**
   * The string to indicate the start of generated fields and getter methods.
   */
  private final String GENERATED_CODE_END_MARK = "/* -------------------- GENERATED CODE END -------------------- */";
  
  /**
   * The string to indicate the end of generated fields and getter methods.
   */
  private final String GENERATED_CODE_START_MARK = "/* ------------------- GENERATED CODE START ------------------- */";
  
  /**
   * The indent string.
   */
  private final String indent;
  
  /**
   * The new-line string.
   */
  private final String newLine;
  
  /**
   * The boolean value whether this instance uses css selectors.
   */
  private final String findByAnnotationLeft;
  
  private final String findByAnnotationRight;
  
  public static Object createUsingCssSelector() {
    return null;
  }
  
  /**
   * Constructs an instance with the default indent and new-line strings.
   * 
   * @param usedCssSelector boolean value whether this instance uses css selectors
   */
  private TestCodeGenerator(final String findByAnnotationLeft, final String findByAnnotationRight) {
    this(findByAnnotationLeft, findByAnnotationRight, "  ", "\n");
  }
  
  /**
   * Constructs an instance with the specified indent and the specified new-line strings.
   * 
   * @param usedCssSelector boolean value whether this instance uses css selectors
   * @param indent the string of indent for generating source code
   * @param newLine the string of new line for generating source code
   */
  private TestCodeGenerator(final String findByAnnotationLeft, final String findByAnnotationRight, final String indent, final String newLine) {
    this.findByAnnotationLeft = findByAnnotationLeft;
    this.findByAnnotationRight = findByAnnotationRight;
    this.indent = indent;
    this.newLine = newLine;
  }
  
  /**
   * Generates skeleton test code with getter methods for html tags, texts and attributes to
   * retrieve values of variables from Selenium2.
   * 
   * @param templateInfo the {@link TemplateInfo} of the template whose skeleton test code we want
   * 	 	   to generate
   * @param packageName the package name to generate skeleton test code
   * @param className the class name to generate skeleton test code
   * @return the generated skeleton test code
   */
  public String generate(final TemplateInfo templateInfo, final String packageName, final String className) {
    Preconditions.<TemplateInfo>checkNotNull(templateInfo);
    Preconditions.<String>checkNotNull(packageName);
    Preconditions.<String>checkNotNull(className);
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package ");
    _builder.append(packageName, "");
    _builder.append(";");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("import static org.junit.Assert.*;");
    _builder.newLine();
    _builder.append("import static org.hamcrest.Matchers.*;");
    _builder.newLine();
    _builder.newLine();
    _builder.append("import org.openqa.selenium.By;");
    _builder.newLine();
    _builder.append("import org.openqa.selenium.WebDriver;");
    _builder.newLine();
    _builder.append("import org.openqa.selenium.WebElement;");
    _builder.newLine();
    _builder.append("import org.openqa.selenium.support.FindBy;");
    _builder.newLine();
    _builder.append("import org.openqa.selenium.support.How;");
    _builder.newLine();
    _builder.newLine();
    _builder.append("import java.util.ArrayList;");
    _builder.newLine();
    _builder.append("import java.util.HashMap;");
    _builder.newLine();
    _builder.append("import java.util.List;");
    _builder.newLine();
    _builder.append("import java.util.regex.Matcher;");
    _builder.newLine();
    _builder.append("import java.util.regex.Pattern;");
    _builder.newLine();
    _builder.newLine();
    _builder.append("public class ");
    _builder.append(className, "");
    _builder.append("Page extends AbstractPage {");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("public ");
    _builder.append(className, "	");
    _builder.append("Page(WebDriver driver) {");
    _builder.newLineIfNotEmpty();
    _builder.append("\t\t");
    _builder.append("super(driver);");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("assertInvariant();");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("\t");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("private def assertInvariant() {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t");
    _builder.append(this.GENERATED_CODE_START_MARK, "	");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    String _fieldsAndGetters = this.getFieldsAndGetters(templateInfo);
    _builder.append(_fieldsAndGetters, "	");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append(this.GENERATED_CODE_END_MARK, "	");
    _builder.newLineIfNotEmpty();
    _builder.append("}");
    _builder.newLine();
    return _builder.toString();
  }
  
  /**
   * Updates existing test code with getter methods for html tags, texts and attributes to retrieve
   * the values of the variables from Selenium2.
   * 
   * @param templateInfo the {@link TemplateInfo} of the template whose skeleton test code we want
   * 	 	   to generate
   * @param code the existing test code
   * @return the updated skeleton test code
   * @throws PageObjectUpdateException if the existing test code doesn't have generated code
   */
  public String update(final TemplateInfo templateInfo, final String code) throws PageObjectUpdateException {
    Preconditions.<TemplateInfo>checkNotNull(templateInfo);
    Preconditions.<String>checkNotNull(code);
    StringBuilder _stringBuilder = new StringBuilder();
    final StringBuilder builder = _stringBuilder;
    final int startIndex = code.indexOf(this.GENERATED_CODE_START_MARK);
    final int endIndex = code.indexOf(this.GENERATED_CODE_END_MARK);
    boolean _or = false;
    boolean _or_1 = false;
    boolean _lessThan = (startIndex < 0);
    if (_lessThan) {
      _or_1 = true;
    } else {
      boolean _lessThan_1 = (endIndex < 0);
      _or_1 = (_lessThan || _lessThan_1);
    }
    if (_or_1) {
      _or = true;
    } else {
      boolean _lessThan_2 = (endIndex < startIndex);
      _or = (_or_1 || _lessThan_2);
    }
    if (_or) {
      PageObjectUpdateException _pageObjectUpdateException = new PageObjectUpdateException("There are no proper start/end marks.");
      throw _pageObjectUpdateException;
    }
    int _length = this.GENERATED_CODE_START_MARK.length();
    int _plus = (startIndex + _length);
    CharSequence _subSequence = code.subSequence(0, _plus);
    builder.append(_subSequence);
    builder.append(this.newLine);
    String _fieldsAndGetters = this.getFieldsAndGetters(templateInfo);
    builder.append(_fieldsAndGetters);
    int _length_1 = code.length();
    CharSequence _subSequence_1 = code.subSequence(endIndex, _length_1);
    builder.append(_subSequence_1);
    return builder.toString();
  }
  
  /**
   * Appends the body of skeleton test code, that is, only html tag fields and getter methods to
   * retrieve the values of the variables into the given string builder.
   * 
   * @param builder {@link StringBuilder} the generated test code will be appended to
   * @param templateInfo the {@link TemplateInfo} of the template whose skeleton test code we want
   * 	 	   to generate
   */
  private String getFieldsAndGetters(final TemplateInfo templateInfo) {
    StringBuilder _stringBuilder = new StringBuilder();
    final StringBuilder builder = _stringBuilder;
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("private static Pattern commentPattern = Pattern.compile(\\\"<!--POGEN,([^,]*),([^,]*),(.*?)-->\\\", Pattern.DOTALL);");
    _builder.newLine();
    builder.append(_builder);
    final HashMultiset<String> varNameCounter = HashMultiset.<String>create();
    List<HtmlTagInfo> _htmlTagInfos = templateInfo.getHtmlTagInfos();
    final Procedure1<HtmlTagInfo> _function = new Procedure1<HtmlTagInfo>() {
      public void apply(final HtmlTagInfo tagInfo) {
        boolean _hasParentTag = tagInfo.hasParentTag();
        if (_hasParentTag) {
          return;
        }
        final String attrValue = tagInfo.getAttributeValue();
        final boolean isRepeated = tagInfo.isRepeated();
        Collection<VariableInfo> _variableInfos = tagInfo.getVariableInfos();
        for (final VariableInfo varInfo : _variableInfos) {
          {
            String _name = varInfo.getName();
            varNameCounter.add(_name);
            String _name_1 = varInfo.getName();
            final int varIndex = varNameCounter.count(_name_1);
            final String varName = varInfo.getName();
            String _convertToString = TestCodeGenerator.this.convertToString(varIndex);
            final String newVarName = (varName + _convertToString);
            boolean _not = (!isRepeated);
            if (_not) {
              StringConcatenation _builder = new StringConcatenation();
              _builder.append(TestCodeGenerator.this.findByAnnotationLeft, "");
              _builder.newLineIfNotEmpty();
              _builder.append("private WebElement elementOf");
              _builder.append(newVarName, "");
              _builder.append(";");
              _builder.newLineIfNotEmpty();
              _builder.newLine();
              _builder.append("public WebElement getElementOf");
              String _capitalize = StringUtils.capitalize(newVarName);
              _builder.append(_capitalize, "");
              _builder.append("() {");
              _builder.newLineIfNotEmpty();
              _builder.append("\t");
              _builder.append("return elementOf");
              _builder.append(newVarName, "	");
              _builder.append(";");
              _builder.newLineIfNotEmpty();
              _builder.append("}");
              _builder.newLine();
              {
                Set<String> _sortedAttributeNames = varInfo.getSortedAttributeNames();
                for(final String attrName : _sortedAttributeNames) {
                  _builder.newLine();
                  _builder.append("public String getAttributeOf");
                  _builder.append(attrName, "");
                  _builder.append("On");
                  String _capitalize_1 = StringUtils.capitalize(newVarName);
                  _builder.append(_capitalize_1, "");
                  _builder.append("() {");
                  _builder.newLineIfNotEmpty();
                  _builder.append("\t");
                  _builder.append("return elementOf");
                  _builder.append(newVarName, "	");
                  _builder.append(".getAttribute(\"");
                  _builder.append(attrName, "	");
                  _builder.append("\");");
                  _builder.newLineIfNotEmpty();
                  _builder.append("}");
                  _builder.newLine();
                }
              }
              builder.append(_builder);
            } else {
              StringConcatenation _builder_1 = new StringConcatenation();
              String _findByAnnotation = TestCodeGenerator.this.getFindByAnnotation();
              _builder_1.append(_findByAnnotation, "");
              _builder_1.newLineIfNotEmpty();
              _builder_1.append("private List<WebElement> elementsOf");
              _builder_1.append(newVarName, "");
              _builder_1.append(";");
              _builder_1.newLineIfNotEmpty();
              _builder_1.newLine();
              _builder_1.append("public List<WebElement> getElementsOf");
              String _capitalize_2 = StringUtils.capitalize(newVarName);
              _builder_1.append(_capitalize_2, "");
              _builder_1.append("() {");
              _builder_1.newLineIfNotEmpty();
              _builder_1.append("\t");
              _builder_1.append("return elementsOf");
              _builder_1.append(newVarName, "	");
              _builder_1.append(";");
              _builder_1.newLineIfNotEmpty();
              _builder_1.append("}");
              _builder_1.newLine();
              {
                Set<String> _sortedAttributeNames_1 = varInfo.getSortedAttributeNames();
                for(final String attrName_1 : _sortedAttributeNames_1) {
                  _builder_1.newLine();
                  _builder_1.append("public List<String> getAttributesOf");
                  _builder_1.append(attrName_1, "");
                  _builder_1.append("On");
                  String _capitalize_3 = StringUtils.capitalize(newVarName);
                  _builder_1.append(_capitalize_3, "");
                  _builder_1.append("() {");
                  _builder_1.newLineIfNotEmpty();
                  _builder_1.append("\t");
                  _builder_1.append("List<String> result = new ArrayList<String>();");
                  _builder_1.newLine();
                  _builder_1.append("\t");
                  _builder_1.append("for (WebElement e : elementsOf");
                  _builder_1.append(newVarName, "	");
                  _builder_1.append(") {");
                  _builder_1.newLineIfNotEmpty();
                  _builder_1.append("\t\t");
                  _builder_1.append("result.add(e.getAttribute(\"");
                  _builder_1.append(attrName_1, "		");
                  _builder_1.append("\"));");
                  _builder_1.newLineIfNotEmpty();
                  _builder_1.append("\t");
                  _builder_1.append("}");
                  _builder_1.newLine();
                  _builder_1.append("\t");
                  _builder_1.append("return result;");
                  _builder_1.newLine();
                  _builder_1.append("}");
                  _builder_1.newLine();
                }
              }
              builder.append(_builder_1);
            }
            boolean _equals = (varIndex == 1);
            if (_equals) {
              StringConcatenation _builder_2 = new StringConcatenation();
              _builder_2.append("public String getTextOf");
              String _capitalize_4 = StringUtils.capitalize(varName);
              _builder_2.append(_capitalize_4, "");
              _builder_2.append(" {");
              _builder_2.newLineIfNotEmpty();
              _builder_2.append("\t");
              _builder_2.append("Matcher matcher = commentPattern.matcher(driver.getPageSource());");
              _builder_2.newLine();
              _builder_2.append("\t");
              _builder_2.append("while (matcher.find()) {");
              _builder_2.newLine();
              _builder_2.append("\t\t");
              _builder_2.append("if (matcher.group(1).equals(\\\"");
              _builder_2.append(attrValue, "		");
              _builder_2.append("\\\") && matcher.group(2).equals(\\\"");
              _builder_2.append(varName, "		");
              _builder_2.append("\\\")) {");
              _builder_2.newLineIfNotEmpty();
              _builder_2.append("\t\t\t");
              _builder_2.append("return matcher.group(3);");
              _builder_2.newLine();
              _builder_2.append("\t\t");
              _builder_2.append("}");
              _builder_2.newLine();
              _builder_2.append("\t");
              _builder_2.append("}");
              _builder_2.newLine();
              _builder_2.append("\t");
              _builder_2.append("return null;");
              _builder_2.newLine();
              _builder_2.append("}");
              _builder_2.newLine();
              builder.append(_builder_2);
            }
          }
        }
      }
    };
    IterableExtensions.<HtmlTagInfo>forEach(_htmlTagInfos, _function);
    return builder.toString();
  }
  
  protected abstract String getFindByAnnotation();
  
  /**
   * Converts the specified number to a string. Results an empty string if the number is 1.
   * 
   * @param number the number to be converted
   * 
   * @return an empty string if the specified number is 1, otherwise prefix + number
   */
  private String convertToString(final int number) {
    String _xifexpression = null;
    boolean _equals = (number == 1);
    if (_equals) {
      _xifexpression = "";
    } else {
      String _valueOf = String.valueOf(number);
      _xifexpression = _valueOf;
    }
    return _xifexpression;
  }
}
