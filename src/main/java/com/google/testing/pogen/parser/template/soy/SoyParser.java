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

package com.google.testing.pogen.parser.template.soy;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;
import com.google.testing.pogen.parser.template.HtmlTagInfo;
import com.google.testing.pogen.parser.template.Pair;
import com.google.testing.pogen.parser.template.RangeSet;
import com.google.testing.pogen.parser.template.TemplateParseException;
import com.google.testing.pogen.parser.template.TemplateParser;

/**
 * A class to parse a soy template by using {@link SoyVariableExtractor}.
 * 
 * @author Kazunori Sakamoto
 */
public class SoyParser extends TemplateParser {
  /**
   * A regular expression which indicates the start of a call tag or the full call tag if it is self
   * contained.
   */
  private static final Pattern CALL_PATTERN = Pattern.compile("\\{call\\s+([^\\s/}]+)");
  /**
   * A regular expression which indicates the start tag of for.
   */
  private static final Pattern FOR_START_PATTERN = Pattern.compile("\\{for");
  /**
   * A regular expression which indicates the end tag of for.
   */
  private static final Pattern FOR_END_PATTERN = Pattern.compile("\\{/for");
  /**
   * A regular expression which indicates the start tags of template.
   */
  private static final Pattern TEMPLATE_START_PATTERN = Pattern
      .compile("\\{template\\s+([^\\s}]+)");
  /**
   * A regular expression which indicates the end tags of template.
   */
  private static final Pattern TEMPLATE_END_PATTERN = Pattern.compile("\\{/template");
  /**
   * A regular expression which indicates the start tags of call.
   */
  private static final Pattern CALL_START_PATTERN = Pattern.compile("\\{call\\s+[^\\s/}]+\\}");
  /**
   * A regular expression which indicates the end tags of call.
   */
  private static final Pattern CALL_END_PATTERN = Pattern.compile("\\{/call\\}");

  /**
   * Constructs the instance of {@link TemplateParser} with the specified attribute name to be
   * inserted.
   * 
   * @param attributeName the attribute name to be inserted
   */
  public SoyParser(String attributeName) {
    super(attributeName);
  }

  @Override
  protected List<HtmlTagInfo> parseTagsContainingVariables(String template)
      throws TemplateParseException {
    Preconditions.checkNotNull(template);
    // Exclude template variables in call parameters.
    // E.g. about {call .t1}{paramarg1}{$p1}{/param}{/call}, $p1 isn't targeted.
    // Because parameters should be tested in the callee side.
    RangeSet<Integer> excludedPart =
        getIndexRangesOfNonNestedTags(template, CALL_START_PATTERN, CALL_END_PATTERN);
    SoyVariableExtractor extractor = new SoyVariableExtractor(excludedPart, attributeName);
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
    // TODO(kazuu): We parse CALL_PATTERNs only in 1 file. We can't detect
    // repeated pattern for calling other template.

    // Get a set of merged ranges which locates the inner parts of
    // for/foreach
    RangeSet<Integer> forRanges = parseForTags(template);

    Map<String, Range<Integer>> templateRanges = parseTemplateTags(template);
    List<Pair<String, Integer>> calls = getMatchedStringAndIndexes(template, CALL_PATTERN, 1);

    boolean added;
    do {
      added = false;
      for (int i = calls.size() - 1; i >= 0; i--) {
        Pair<String, Integer> nameAndIndex = calls.get(i);
        if (forRanges.contains(nameAndIndex.second)) {
          // If a template definition is called from another repeated part, it's
          // treated as repeated part
          forRanges.add(templateRanges.get(nameAndIndex.first));
          calls.remove(i);
          added = true;
        }
      }
    } while (added);
    return forRanges;
  }

  /**
   * Gets a {@link Map} of names and position ranges which locates the parts between start and end
   * tags of template command in the specified template.
   * 
   * @param template the string of the template to be parsed
   * @return the {@link Map} of the names and the indexes which locates the parts between start and
   *         end tags of template command
   * @throws TemplateParseException if the specified template is in bad format
   */
  private static Map<String, Range<Integer>> parseTemplateTags(String template)
      throws TemplateParseException {
    return getNamedIndexRangesOfNonNestedTags(template, TEMPLATE_START_PATTERN,
        TEMPLATE_END_PATTERN, 1);
  }

  /**
   * Gets a {@link RangeSet} of indexes which locates the parts between start and end tags of
   * for/foreach command in the specified template.
   * 
   * @param template the string of the template to be parsed
   * @return the {@link RangeSet} of the indexes which locates the parts between start and end tags
   *         of for/foreach command in the specified template
   * @throws TemplateParseException if the specified template is in bad format
   */
  private static RangeSet<Integer> parseForTags(String template) throws TemplateParseException {
    return getIndexRangesOfNestedTags(template, FOR_START_PATTERN, FOR_END_PATTERN);
  }
}
