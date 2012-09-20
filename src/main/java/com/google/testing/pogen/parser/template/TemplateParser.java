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

package com.google.testing.pogen.parser.template;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.google.common.collect.Ranges;

/**
 * A class to parse a template to retrieve the {@link TemplateInfo} instance.
 * 
 * @author Kazunori Sakamoto
 */
public abstract class TemplateParser {

  /**
   * A name of the attribute to be assigned for tags containing template variables.
   */
  protected final String attributeName;

  /**
   * Constructs the instance of {@link TemplateParser} with the specified attribute name.
   * 
   * @param attributeName the name of the attribute to be assigned for tags containing template
   *        variables
   */
  public TemplateParser(String attributeName) {
    this.attributeName = attributeName;
  }

  /**
   * Retrieves the information of the specified template by parsing it.
   * 
   * @param template the string of the template to be parsed
   * @return the information of the specified template
   * @throws TemplateParseException if the specified template is in bad format
   */
  public TemplateInfo parse(String template) throws TemplateParseException {
    Preconditions.checkNotNull(template);

    List<HtmlTagInfo> htmlTagInfos = parseTagsContainingVariables(template);
    RangeSet<Integer> repeatedParts = parseRepeatedPart(template);
    return new TemplateInfo(template, htmlTagInfos, repeatedParts);
  }

  /**
   * Retrieves an information list of html tags which contain template variables by parsing the
   * specified template.
   * 
   * @param template the string of the template to be parsed
   * @return the information list of html tags which contain template variables
   * @throws TemplateParseException if the specified template is in bad format
   */
  protected abstract List<HtmlTagInfo> parseTagsContainingVariables(String template)
      throws TemplateParseException;

  /**
   * Retrieves a {@link RangeSet} of indexes where the repeated part are located in the specified
   * template. Note that repeated part is inner part of for/foreach tags and of template tags called
   * from another repeated part. For example, {template .t1}repeated part{/template}{foreach
   * ...}repeated part{call .t1}{/foreach}.
   * 
   * @param template the string of the template to be parsed
   * @return the {@link RangeSet} of the indexes where repeated part are located
   * @throws TemplateParseException if the specified template is in bad format
   */
  protected abstract RangeSet<Integer> parseRepeatedPart(String template)
      throws TemplateParseException;

  /**
   * Returns start indexes of strings that match the indicated regular expression.
   * 
   * @param text the string to be parsed
   * @param pattern the regular expression to find
   * @return the start indexes of matched strings with the indicated pattern
   */
  protected static List<Integer> getMatchedIndexes(String text, Pattern pattern) {
    List<Integer> results = Lists.newArrayList();
    Matcher m = pattern.matcher(text);
    while (m.find()) {
      results.add(m.start());
    }
    return results;
  }

  /**
   * Returns a list of pairs containing matched strings and their start index for the specified
   * regular expression and the given group index.
   * 
   * @param text the string to be parsed
   * @param pattern the regular expression to find
   * @param groupIndex the index of the group whose matched string we want to retrieve
   * @return the start indexes of matched strings with the indicated pattern
   */
  protected static List<Pair<String, Integer>> getMatchedStringAndIndexes(String text,
      Pattern pattern, int groupIndex) {
    List<Pair<String, Integer>> results = Lists.newArrayList();
    Matcher m = pattern.matcher(text);
    while (m.find()) {
      results.add(Pair.of(m.group(groupIndex), m.start()));
    }
    return results;
  }

  /**
   * Returns a {@link RangeSet} of indexes between the start and the end tags for non-nested tags.
   * 
   * @param text the string to be parsed
   * @param startPattern the regular expression of start tags to find
   * @param endPattern the regular expression of end tags to find
   * @return the {@link RangeSet} of the indexes between the start and the end tags
   * @throws TemplateParseException if the specified template is in bad format where broken pairs of
   *         start and end tags appear
   */
  protected static RangeSet<Integer> getIndexRangesOfNonNestedTags(String text,
      Pattern startPattern, Pattern endPattern) throws TemplateParseException {

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
   * Returns a {@link RangeSet} of indexes between the start and the end tags for nested tags. If
   * there're nested tags, returns the outermost ranges.
   * 
   * @param text the string to be parsed
   * @param startPattern the regular expression of start tags to find
   * @param endPattern the regular expression of end tags to find
   * @return the {@link RangeSet} of the indexes between the start and the end tags
   * @throws TemplateParseException if the specified template is in bad format where broken pairs of
   *         start and end tags appear
   */
  protected static RangeSet<Integer> getIndexRangesOfNestedTags(String text, Pattern startPattern,
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
   * Returns a {@link Map} of names and indexes between the start and the end tags for non-nested
   * tags.
   * 
   * @param text the string to be parsed
   * @param startPattern the regular expression of start tags to find
   * @param endPattern the regular expression of end tags to find
   * @param groupIndex the index of the group in matched parts to retrieve a name
   * @return the {@link Map} of the names and the indexes between the start and the end tags
   * @throws TemplateParseException if the specified template is in bad format where broken pairs of
   *         start and end tags appear
   */
  protected static Map<String, Range<Integer>> getNamedIndexRangesOfNonNestedTags(String text,
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
