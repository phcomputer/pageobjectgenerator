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

package com.google.testing.pogen.parser.template.ejs;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.regex.Pattern;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.common.base.Preconditions;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import com.google.testing.pogen.parser.template.HtmlTagInfo;
import com.google.testing.pogen.parser.template.RegexVariableExtractor;
import com.google.testing.pogen.parser.template.TemplateParseException;
import com.google.testing.pogen.parser.template.TemplateParser;

/**
 * A class to parse a ejs template by using {@link EjsVariableExtractor}.
 * 
 * @author Kazunori Sakamoto
 */
public class EjsParser extends TemplateParser {
  /**
   * A regular expression which indicates the start tag of for.
   */
  private static final Pattern FOR_START_PATTERN = Pattern.compile("<%\\s*for");
  /**
   * A regular expression which indicates the end tag of for.
   */
  private static final Pattern FOR_END_PATTERN = Pattern.compile("<%\\s*}");

  /**
   * Constructs the instance of {@link TemplateParser} with the specified attribute name.
   * 
   * @param attributeName the name of the attribute to be assigned for tags containing template
   *        variables
   */
  public EjsParser(String attributeName) {
    super(attributeName);
  }

  @Override
  protected List<HtmlTagInfo> parseTagsContainingVariables(String template,
      RangeSet<Integer> repeatedParts) throws TemplateParseException {
    Preconditions.checkNotNull(template);
    RegexVariableExtractor extractor =
        new RegexVariableExtractor(repeatedParts, TreeRangeSet.<Integer>create(), attributeName,
            Pattern.compile("<%=\\s*(.*?)%>"));
    try {
      extractor.parse(new InputSource(new StringReader(template)));
    } catch (SAXException e) {
      throw new TemplateParseException(e);
    } catch (IOException e) {
      throw new TemplateParseException(e);
    }
    return extractor.getSortedHtmlTagInfos();
  }

  @Override
  protected RangeSet<Integer> parseRepeatedPart(String template) throws TemplateParseException {
    // Get a set of merged ranges which locates the inner parts of for/foreach
    return getIndexRangesOfNestedTags(template, FOR_START_PATTERN, FOR_END_PATTERN);
  }
}
