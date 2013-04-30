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

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.google.testing.pogen.generator.test.java.NameConverter;

/**
 * A class to contain the information of a template variable such as the variable name, the
 * attribute names and the start position. Note that template variable means variable in html
 * template files such as soy and php.
 * 
 * @author Kazunori Sakamoto
 */
public class VariableInfo {
  /**
   * A command text to print the template variable.
   */
  private final String printCommandText;
  /**
   * A name of this template variable.
   */
  private final String name;
  /**
   * A start position of this template variable in the parsed template.
   */
  private final int startIndex;
  /**
   * A boolean whether this template variable is contained by a text element.
   */
  private boolean containedByText;
  /**
   * A boolean whether this template variable is dummy for the manipulable tags such as a and input.
   */
  private boolean manipulableTag;
  /**
   * Sorted names of attributes which contain this template variable. Note that an empty string of
   * an attribute name means this variable appears in a text element.
   */
  private final TreeSet<String> attributeNames;

  /**
   * Constructs the information of a template variable with the specified name, the specified start
   * position and the specified attribute name.
   * 
   * @param printCommandText the command text to print the template variable
   * @param name the name of this template variable
   * @param startIndex the start position of this template variable in the parsed template
   * @param containedByText the boolean whether this template variable is contained by a text
   *        element
   * @param manipulableTag the boolean whether this template variable is dummy for
   *        the manipulable tags such as a and input
   */
  public VariableInfo(String printCommandText, String name, int startIndex,
      boolean containedByText, boolean manipulableTag) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(name));
    Preconditions.checkArgument(startIndex >= 0);

    this.printCommandText = printCommandText;
    name = NameConverter.replaceSignsToTexts(name);
    this.name = NameConverter.getJavaIdentifier(name);
    this.startIndex = startIndex;
    this.attributeNames = Sets.newTreeSet();
    this.containedByText = containedByText;
    this.manipulableTag = manipulableTag;
  }

  /**
   * Adds the specified name of the attribute which contains this template variable.
   * 
   * @param attributeName the name of the attribute where this variable appears to be added
   */
  public void addAttributeName(String attributeName) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(attributeName));

    attributeNames.add(attributeName);
  }

  public String getPrintCommandText() {
    return printCommandText;
  }

  public String getName() {
    return name;
  }

  public Set<String> getSortedAttributeNames() {
    return Collections.unmodifiableSet(attributeNames);
  }

  public int getStartIndex() {
    return startIndex;
  }

  public boolean isContainedByText() {
    return containedByText;
  }

  public boolean isManipulableTag() {
    return manipulableTag;
  }
}
