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

import com.google.testing.pogen.parser.template.HtmlTagInfo;
import com.google.testing.pogen.parser.template.RangeSet;
import com.google.testing.pogen.parser.template.TemplateParseException;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.cyberneko.html.HTMLEventInfo;
import org.cyberneko.html.parsers.SAXParser;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class to extract template variables with its parent html tags by parsing a
 * template with CyberNeko HTML Parser.
 *
 * @author Kazunori Sakamoto
 */
public class SoyVariableExtractor extends SAXParser {
  /**
   * A string to enable AUGMENTATIONS feature.
   */
  private static final String AUGMENTATIONS = "http://cyberneko.org/html/features/augmentations";
  /**
   * A regular expression which indicates template variable to print.
   */
  private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\$([^{|]*)(|[^{]*)?\\}");
  /**
   * A stack of extracted information of a html tag such as the start position,
   * the end position, the id value and the information of template variables.
   * This field is only used to create the {@code sortedHtmlTagInfos} filed.
   */
  private final Stack<HtmlTagInfo> tagInfoStack;
  /**
   * A list of extracted information of a html tag sorted by appearance of the
   * end tag which contains template variables.
   */
  private final List<HtmlTagInfo> sortedHtmlTagInfos;
  /**
   * Positions of excluded parts such as "{call .t1}excluded part{/call}".
   */
  private final RangeSet<Integer> excludedRanges;

  /**
   * Constructs an instance to extract template variables with the specified
   * positions of excluded parts.
   *
   * @param excludedRanges a {@link RangeSet} with the positions of excluded
   *        parts
   * @throws TemplateParseException if the specified template is in bad format
   */
  public SoyVariableExtractor(RangeSet<Integer> excludedRanges) throws TemplateParseException {
    this.excludedRanges = excludedRanges;
    this.tagInfoStack = new Stack<HtmlTagInfo>();
    this.sortedHtmlTagInfos = new ArrayList<HtmlTagInfo>();

    // CyberNeko HTML Parser supports AUGMENTATIONS
    try {
      setFeature(AUGMENTATIONS, true);
    } catch (SAXNotRecognizedException e) {
      throw new TemplateParseException(e);
    } catch (SAXNotSupportedException e) {
      throw new TemplateParseException(e);
    }
  }

  public List<HtmlTagInfo> getSortedHtmlTagInfos() {
    return Collections.unmodifiableList(sortedHtmlTagInfos);
  }

  @Override
  public void startDocument(XMLLocator locator, String encoding, NamespaceContext namespaceContext,
      Augmentations augs) throws XNIException {
    tagInfoStack.clear();
    sortedHtmlTagInfos.clear();

    super.startDocument(locator, encoding, namespaceContext, augs);
  }

  @Override
  public void startElement(QName element, XMLAttributes attrs, Augmentations augs) {
    // Get offset information
    HTMLEventInfo info = (HTMLEventInfo) augs.getItem(AUGMENTATIONS);
    HtmlTagInfo tagInfo =
        new HtmlTagInfo(attrs.getValue("id"), info.getBeginCharacterOffset(),
            info.getEndCharacterOffset());
    tagInfoStack.push(tagInfo);

    for (int i = 0; i < attrs.getLength(); i++) {
      // Ignore variables appearing two more than
      Matcher matcher = VARIABLE_PATTERN.matcher(attrs.getValue(i));
      while (matcher.find()) {
        if (!excludedRanges.contains(matcher.start(1))) {
          tagInfo.addVariableInfo(matcher.group(1), matcher.start(1), attrs.getQName(i));
        }
      }
    }

    super.startElement(element, attrs, augs);
  }

  @Override
  public void endElement(QName element, Augmentations augs) throws XNIException {
    HtmlTagInfo tagInfo = tagInfoStack.pop();
    if (!tagInfo.hasVariables()) {
      sortedHtmlTagInfos.add(tagInfo);
    }

    super.endElement(element, augs);
  }

  @Override
  public void characters(XMLString string, Augmentations augs) throws XNIException {
    String text = string.toString();
    Matcher matcher = VARIABLE_PATTERN.matcher(text);
    while (matcher.find()) {
      if (excludedRanges.contains(matcher.start(1))) {
        continue;
      }
      // tagInfoStack always has some elements because NekoHTML add <html> tag
      // as a root element automatically
      // Note that tags automatically added have -1 start/end indexes
      HtmlTagInfo tagInfo = tagInfoStack.peek();
      tagInfo.addVariableInfo(matcher.group(1), matcher.start(1));
    }

    super.characters(string, augs);
  }
}
