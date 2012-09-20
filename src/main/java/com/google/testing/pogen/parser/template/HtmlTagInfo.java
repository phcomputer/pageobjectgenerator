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

import java.util.Collection;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

/**
 * A class to contain the information of a html tag such as its location, its specific attribute
 * value and template variables which it contains. The instance of this class is used to retrieve
 * template variables on the html tag unit.
 * 
 * @author Kazunori Sakamoto
 */
public class HtmlTagInfo {
  /**
   * A start position in the parsed template which contains this tag. E.g. about
   * {@literal "<html><img /></html>"}, {@code startIndex} of the img tag is 6.
   */
  private final int startIndex;
  /**
   * An end position in the parsed template which contains this tag. E.g. about
   * {@literal "<html><img /></html>"}, {@code endIndex} of the img tag is 13.
   */
  private final int endIndex;
  /**
   * A specific attribute value of this tag. This is null if this html tag has no attribute. Note
   * that the variable extractor chooses attributes to memorize.
   */
  private String attributeValue;
  /**
   * A map of variable names and {@link VariableInfo} instances.
   */
  private final Map<String, VariableInfo> variables;

  /**
   * Constructs an instance with the specific attribute value, the specified start and the specified
   * end positions. Note that when a template variable has no parent html tag, this class assumes it
   * has a virtual root html tag and {@code startIndex} and {@code endIndex} should be -1.
   * 
   * @param attributeValue the specific attribute value of this tag
   * @param startIndex the start position of this tag
   * @param endIndex the end position of this tag
   */
  public HtmlTagInfo(@Nullable String attributeValue, int startIndex, int endIndex) {
    Preconditions.checkArgument(startIndex >= -1);
    Preconditions.checkArgument(endIndex >= -1);

    this.attributeValue = attributeValue;
    this.startIndex = startIndex;
    this.endIndex = endIndex;
    this.variables = Maps.newHashMap();
  }

  /**
   * Adds a {@link VariableInfo} instance with the specified variable name and the specified start
   * index.
   * 
   * @param variableName the name of the template variable to be added
   * @param variableStartIndex the start position of the template variable in the parsed template
   */
  public void addVariableInfo(String variableName, int variableStartIndex) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(variableName));
    Preconditions.checkArgument(variableStartIndex >= 0);

    VariableInfo varInfo = getOrCreateVariableInfo(variableName, variableStartIndex);
    varInfo.setContainedByText(true);
  }

  /**
   * Adds a {@link VariableInfo} instance with the specified variable name, the specified attribute
   * name and the specified start index.
   * 
   * @param variableName the name of the template variable to be added
   * @param variableStartIndex the start position of the template variable in the parsed template
   * @param attributeName the name of the attribute which contains the template variable
   */
  public void addVariableInfo(String variableName, int variableStartIndex, String attributeName) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(variableName));
    Preconditions.checkArgument(variableStartIndex >= 0);
    Preconditions.checkNotNull(attributeName);

    VariableInfo varInfo = getOrCreateVariableInfo(variableName, variableStartIndex);
    varInfo.addAttributeName(attributeName);
  }

  /**
   * Returns the existing {@link VariableInfo} instance or a generated one if it doesn't exist.
   * 
   * @param variableName the name of the template variable to be added
   * @param variableStartIndex the start position of the template variable in the parsed template
   * @return the existing {@link VariableInfo} instance or a generated one if it doesn't exist
   */
  private VariableInfo getOrCreateVariableInfo(String variableName, int variableStartIndex) {
    VariableInfo varInfo = variables.get(variableName);
    if (varInfo == null) {
      varInfo = new VariableInfo(variableName, variableStartIndex);
      variables.put(variableName, varInfo);
    }
    return varInfo;
  }

  /**
   * Returns {@code true} if this html tag has template variables.
   * 
   * @return {@code true} if this html tag has template variables
   */
  public boolean hasVariables() {
    return variables.isEmpty();
  }

  /**
   * Returns {@code true} if this html tag has the specific attribute. Note that the variable
   * extractor chooses attributes to memorize.
   * 
   * @return {@code true} if this html tag has the specific attribute
   */
  public boolean hasAttributeValue() {
    return attributeValue != null;
  }

  /**
   * Returns {@code true} if this html tag has a parent tag.
   * 
   * @return {@code true} if this html tag has a parent tag
   */
  public boolean hasParentTag() {
    // When a template variable has no parent tag, this class assumes it has a
    // virtual root html tag and its start and end indexes should be -1
    return startIndex >= 0 && endIndex >= 0;
  }

  public int getStartIndex() {
    return startIndex;
  }

  public int getEndIndex() {
    return endIndex;
  }

  public String getAttributeValue() {
    return attributeValue;
  }

  public Collection<VariableInfo> getVariableInfos() {
    return variables.values();
  }

  public void setId(String idValue) {
    this.attributeValue = idValue;
  }
}
