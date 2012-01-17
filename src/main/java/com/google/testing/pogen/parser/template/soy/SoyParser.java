// Copyright 2011 The PageObjectGenerator Authors.
// Copyright 2011 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS-IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.testing.pogen.parser.template.soy;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.google.common.collect.Ranges;
import com.google.testing.pogen.parser.template.HtmlTagInfo;
import com.google.testing.pogen.parser.template.Pair;
import com.google.testing.pogen.parser.template.RangeSet;
import com.google.testing.pogen.parser.template.TemplateParseException;
import com.google.testing.pogen.parser.template.TemplateParser;
import com.google.testing.pogen.parser.template.TreeRangeSet;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class to parse a soy template by using {@link SoyVariableExtractor}
 *
 * @author Kazunori Sakamoto
 */
public class SoyParser extends TemplateParser {
  /**
   * A regular expression which indicates the start of a call tag or the full
   * call tag if it is self contained.
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

  @Override
  protected List<HtmlTagInfo> parseTagsContainingVariables(String template)
      throws TemplateParseException {
    Preconditions.checkNotNull(template);
    // Exclude template variables in call parameters.
    // E.g. about {call .t1}{paramarg1}{$p1}{/param}{/call}, $p1 isn't targeted.
    // Because parameters should be tested in the callee side.
    RangeSet<Integer> excludedPart =
        getIndexRangesOfNonNestedTags(template, CALL_START_PATTERN, CALL_END_PATTERN);
    SoyVariableExtractor extractor = new SoyVariableExtractor(excludedPart);
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
   * Gets a {@link Map} of names and position ranges which locates the parts
   * between start and end tags of template command in the specified template.
   *
   * @param template the string of the template to be parsed
   * @return the {@link Map} of the names and the indexes which locates the
   *         parts between start and end tags of template command
   * @throws TemplateParseException if the specified template is in bad format
   */
  private static Map<String, Range<Integer>> parseTemplateTags(String template)
      throws TemplateParseException {
    return getNamedIndexRangesOfNonNestedTags(template, TEMPLATE_START_PATTERN,
        TEMPLATE_END_PATTERN, 1);
  }

  /**
   * Gets a {@link RangeSet} of indexes which locates the parts between start
   * and end tags of for/foreach command in the specified template.
   *
   * @param template the string of the template to be parsed
   * @return the {@link RangeSet} of the indexes which locates the parts between
   *         start and end tags of for/foreach command in the specified template
   * @throws TemplateParseException if the specified template is in bad format
   */
  private static RangeSet<Integer> parseForTags(String template) throws TemplateParseException {
    return getIndexRangesOfNestedTags(template, FOR_START_PATTERN, FOR_END_PATTERN);
  }

  /**
   * Returns start indexes of strings that match the indicated regular
   * expression.
   *
   * @param text the string to be parsed
   * @param pattern the regular expression to find
   * @return the start indexes of matched strings with the indicated pattern
   */
  private static List<Integer> getMatchedIndexes(String text, Pattern pattern) {
    List<Integer> results = Lists.newArrayList();
    Matcher m = pattern.matcher(text);
    while (m.find()) {
      results.add(m.start());
    }
    return results;
  }

  /**
   * Returns a list of pairs containing matched strings and their start index
   * for the specified regular expression and the given group index.
   *
   * @param text the string to be parsed
   * @param pattern the regular expression to find
   * @param groupIndex the index of the group whose matched string we want to
   *        retrieve
   * @return the start indexes of matched strings with the indicated pattern
   */
  private static List<Pair<String, Integer>> getMatchedStringAndIndexes(String text,
      Pattern pattern, int groupIndex) {
    List<Pair<String, Integer>> results = Lists.newArrayList();
    Matcher m = pattern.matcher(text);
    while (m.find()) {
      results.add(Pair.of(m.group(groupIndex), m.start()));
    }
    return results;
  }

  /**
   * Returns a {@link RangeSet} of indexes between the start and the end tags
   * for non-nested tags.
   *
   * @param text the string to be parsed
   * @param startPattern the regular expression of start tags to find
   * @param endPattern the regular expression of end tags to find
   * @return the {@link RangeSet} of the indexes between the start and the end
   *         tags
   * @throws TemplateParseException if the specified template is in bad format
   *         where broken pairs of start and end tags appear
   */
  private static RangeSet<Integer> getIndexRangesOfNonNestedTags(String text, Pattern startPattern,
      Pattern endPattern) throws TemplateParseException {

    RangeSet<Integer> rangeSet = TreeRangeSet.create();

    List<Integer> startIndexes = getMatchedIndexes(text, startPattern);
    List<Integer> endIndexes = getMatchedIndexes(text, endPattern);
    // Check whether all start tags and end tags are paired correctly
    if (startIndexes.size() != endIndexes.size()) {
      throw new TemplateParseException(String.format(
          "There are broken pairs of start and end tags (#start tags: %d, #end tags: %d)",
          startIndexes.size(), endIndexes.size()));
    }

    for (int i = 0; i < startIndexes.size(); i++) {
      rangeSet.add(Ranges.closedOpen(startIndexes.get(i), endIndexes.get(i)));
    }
    return rangeSet;
  }

  /**
   * Returns a {@link RangeSet} of indexes between the start and the end tags
   * for nested tags. If there're nested tags, returns the outermost ranges.
   *
   * @param text the string to be parsed
   * @param startPattern the regular expression of start tags to find
   * @param endPattern the regular expression of end tags to find
   * @return the {@link RangeSet} of the indexes between the start and the end
   *         tags
   * @throws TemplateParseException if the specified template is in bad format
   *         where broken pairs of start and end tags appear
   */
  private static RangeSet<Integer> getIndexRangesOfNestedTags(String text, Pattern startPattern,
      Pattern endPattern) throws TemplateParseException {
    RangeSet<Integer> rangeSet = TreeRangeSet.create();

    List<Integer> startIndexes = getMatchedIndexes(text, startPattern);
    List<Integer> endIndexes = getMatchedIndexes(text, endPattern);

    int startIndex = 0, endIndex = 0;
    // Check whether the sizes of start tags and end tags are equal
    if (startIndexes.size() != endIndexes.size()) {
      throw new TemplateParseException(String.format(
          "The sizes of start tags and end tags are not equal (#start tags: %d, #end tags: %d).",
          startIndexes.size(), endIndexes.size()));
    }

    int depth = 0, lastStartIndex = 0;
    while (endIndex < endIndexes.size()) {
      boolean consumedStartIndexes = startIndex == startIndexes.size();
      // Process a previous index before a next index
      if (consumedStartIndexes || endIndexes.get(endIndex) < startIndexes.get(startIndex)) {
        if (--depth <= 0) {
          if (depth < 0) {
            // Check whether all start tags and end tags are paired correctly
            throw new TemplateParseException(
                String.format("Broken pairs of start and end tags are found."));
          }
          rangeSet.add(Ranges.closedOpen(lastStartIndex, endIndexes.get(endIndex)));
        }
        endIndex++;
      } else {
        if (depth++ == 0) {
          lastStartIndex = startIndexes.get(startIndex);
        }
        startIndex++;
      }
    }
    return rangeSet;
  }

  /**
   * Returns a {@link Map} of names and indexes between the start and the end
   * tags for non-nested tags.
   *
   * @param text the string to be parsed
   * @param startPattern the regular expression of start tags to find
   * @param endPattern the regular expression of end tags to find
   * @param groupIndex the index of the group in matched parts to retrieve a
   *        name
   * @return the {@link Map} of the names and the indexes between the start and
   *         the end tags
   * @throws TemplateParseException if the specified template is in bad format
   *         where broken pairs of start and end tags appear
   */
  private static Map<String, Range<Integer>> getNamedIndexRangesOfNonNestedTags(String text,
      Pattern startPattern, Pattern endPattern, int groupIndex) throws TemplateParseException {
    Map<String, Range<Integer>> templates = Maps.newHashMap();

    List<Pair<String, Integer>> starts = getMatchedStringAndIndexes(text, startPattern, groupIndex);
    List<Integer> ends = getMatchedIndexes(text, endPattern);
    // Check whether the sizes of start tags and end tags are equal
    if (starts.size() != ends.size()) {
      throw new TemplateParseException(String.format(
          "There are broken pairs of start and end tags (#start tags: %d, #end tags: %d)",
          starts.size(), ends.size()));
    }

    int endIndexesIndex = 0;
    for (Pair<String, Integer> nameAndIndex : starts) {
      int endIndex = ends.get(endIndexesIndex++);
      // Check whether all start tags and end tags are paired correctly
      if (nameAndIndex.second >= endIndex) {
        throw new TemplateParseException(
            String.format("Broken pairs of start and end tags are found."));
      }
      templates.put(nameAndIndex.first, Ranges.closedOpen(nameAndIndex.second, endIndex));
    }
    return templates;
  }
}
