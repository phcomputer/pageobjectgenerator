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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
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

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

/**
 * A class to extract template variables with its parent html tags by parsing a template with
 * CyberNeko HTML Parser.
 * 
 * @author Kazunori Sakamoto
 */
public abstract class RegexVariableExtractor extends SAXParser {
  /**
   * A string to enable AUGMENTATIONS feature.
   */
  private static final String AUGMENTATIONS = "http://cyberneko.org/html/features/augmentations";
  /**
   * A regular expression which indicates template variable to print.
   */
  private final Pattern variablePattern;
  /**
   * A stack of extracted information of a html tag such as the start position, the end position,
   * the id value and the information of template variables. This field is only used to create the
   * {@code sortedHtmlTagInfos} filed.
   */
  private final Stack<HtmlTagInfo> tagInfoStack;
  /**
   * A list of extracted information of a html tag sorted by appearance of the end tag which
   * contains template variables.
   */
  private final List<HtmlTagInfo> sortedHtmlTagInfos;
  /**
   * Positions of excluded parts such as "{call .t1}excluded part{/call}".
   */
  private final RangeSet<Integer> excludedRanges;
  /**
   * A string for divided characters because "<%=a%>" is diveded into "<" and "%=a%>".
   */
  private String lastText;
  /**
   * A name of the attribute to be assigned for tags containing template variables.
   */
  private String attributeName;
  /**
   * A immutable list of names of manipulatable tags.
   */
  private static final ImmutableList<String> manipulableTags;

  static {
    manipulableTags = ImmutableList.of("a", "link", "input", "button", "textarea", "select");
  }

  /**
   * Constructs an instance to extract template variables with the specified positions of excluded
   * parts and the given attribute name for memorizing the value.
   * 
   * @param excludedRanges the {@link RangeSet} with the positions of excluded parts
   * @param attributeName the name of the attribute to be assigned for tags containing template
   *        variables
   * @throws TemplateParseException if the specified template is in bad format
   */
  public RegexVariableExtractor(RangeSet<Integer> excludedRanges, String attributeName)
      throws TemplateParseException {
    this.excludedRanges = excludedRanges;
    this.attributeName = attributeName;
    this.tagInfoStack = new Stack<HtmlTagInfo>();
    this.sortedHtmlTagInfos = new ArrayList<HtmlTagInfo>();
    this.variablePattern = initializeVariablePattern();

    // CyberNeko HTML Parser supports AUGMENTATIONS
    try {
      setFeature(AUGMENTATIONS, true);
    } catch (SAXNotRecognizedException e) {
      throw new TemplateParseException(e);
    } catch (SAXNotSupportedException e) {
      throw new TemplateParseException(e);
    }
  }

  /**
   * Returns a regular expression for template variables.
   * 
   * @return a regular expression for template variables
   */
  protected abstract Pattern initializeVariablePattern();

  public List<HtmlTagInfo> getSortedHtmlTagInfos() {
    return Collections.unmodifiableList(sortedHtmlTagInfos);
  }

  @Override
  public void startDocument(XMLLocator locator, String encoding, NamespaceContext namespaceContext,
      Augmentations augs) throws XNIException {
    tagInfoStack.clear();
    sortedHtmlTagInfos.clear();
    lastText = "";

    super.startDocument(locator, encoding, namespaceContext, augs);
  }

  @Override
  public void startElement(QName element, XMLAttributes attrs, Augmentations augs) {
    processCharacters();

    // Ignore elements with prefix (:) to deal with not html elements such as "c:set" in JSP.
    if (element.prefix == null) {
      // Get offset information
      HTMLEventInfo info = (HTMLEventInfo) augs.getItem(AUGMENTATIONS);
      HtmlTagInfo tagInfo =
          new HtmlTagInfo(attrs.getValue(attributeName), info.getBeginCharacterOffset(),
              info.getEndCharacterOffset());
      tagInfoStack.push(tagInfo);

      for (int i = 0; i < attrs.getLength(); i++) {
        // Ignore variables appearing two more than
        Matcher matcher = variablePattern.matcher(attrs.getValue(i));
        while (matcher.find()) {
          int iGroup = getFirstAvailableGroupIndex(matcher);
          if (!excludedRanges.contains(matcher.start(iGroup))) {
            tagInfo.addVariableInfo(matcher.group(0), matcher.group(iGroup), matcher.start(iGroup),
                attrs.getQName(i));
          }
        }
        if (attrs.getQName(i).equals("id")) {
          tagInfo.setIdValue(attrs.getValue(i));
        } else if (attrs.getQName(i).equals("name")) {
          tagInfo.setNameValue(attrs.getValue(i));
        }
      }
    }
    super.startElement(element, attrs, augs);
  }

  private int getFirstAvailableGroupIndex(Matcher matcher) {
    int iGroup = 0;
    do {
      iGroup++;
    } while (matcher.group(iGroup) == null);
    return iGroup;
  }

  @Override
  public void endElement(QName element, Augmentations augs) throws XNIException {
    String text = processCharacters();

    // Ignore elements with prefix (:) to deal with not html elements such as "c:set" in JSP.
    // TODO(kazuu): Should we ignore elements with prefix (:)? Really?
    if (element.prefix == null) {
      HtmlTagInfo tagInfo = tagInfoStack.pop();
      if (!excludedRanges.contains(tagInfo.getStartIndex())) {
        for (String tag : manipulableTags) {
          if (StringUtils.equalsIgnoreCase(element.rawname, tag)) {
            String name = decideName(element, text, tagInfo);
            tagInfo.addManipulableTag(name, tagInfo.getStartIndex());
            break;
          }
        }
      }

      if (!tagInfo.hasVariables()) {
        sortedHtmlTagInfos.add(tagInfo);
      }
    }

    super.endElement(element, augs);
  }

  /**
   * @param element
   * @param text
   * @param tagInfo
   * @return
   */
  private String decideName(QName element, String text, HtmlTagInfo tagInfo) {
    // TODO: Write method explanation
    // TODO: Reconsider about <a href='{$url}'></a>
    String name = element.rawname;
    if (!Strings.isNullOrEmpty(tagInfo.getNameValue())) {
      name += "_" + tagInfo.getNameValue();
    }
    if (!Strings.isNullOrEmpty(tagInfo.getIdValue())) {
      name += "_" + tagInfo.getIdValue();
    }
    if (name == element.rawname && !Strings.isNullOrEmpty(text)) {
      name += "_" + text;
    }
    return name;
  }

  private String processCharacters() {
    String text = lastText;
    Matcher matcher = variablePattern.matcher(lastText);
    while (matcher.find()) {
      if (excludedRanges.contains(matcher.start(1))) {
        continue;
      }
      // tagInfoStack always has some elements
      // because NekoHTML add <html> tag as a root element automatically
      // Note that tags automatically added have -1 start/end indices
      HtmlTagInfo tagInfo = tagInfoStack.peek();
      int iGroup = getFirstAvailableGroupIndex(matcher);
      tagInfo.addVariableInfo(matcher.group(0), matcher.group(iGroup), matcher.start(iGroup));
    }
    lastText = "";
    return text;
  }

  @Override
  public void characters(XMLString string, Augmentations augs) throws XNIException {
    lastText += string.toString();

    super.characters(string, augs);
  }
}
