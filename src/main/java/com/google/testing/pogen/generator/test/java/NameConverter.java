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

package com.google.testing.pogen.generator.test.java;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * A utility class to convert various names to adapt for Java naming rule.
 *
 * @author Kazunori Sakamoto
 */
public class NameConverter {
  /**
   * The character to replace non-suitable characters.
   */
  private static final char REPLACE = '_';

  private NameConverter() {
  }

  /**
   * Converts the specified file name to a Java class name.
   *
   * @param fileName the file name to be converted
   * @return the Java class name converted from the specified file name
   */
  public static String getJavaClassName(String fileName) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(fileName));

    StringBuilder result = new StringBuilder();
    String[] strings = getJavaIdentifier(fileName).split("" + REPLACE);
    for (String string : strings) {
      result.append(StringUtil.capitalize(string));
    }
    return result.toString();
  }

  /**
   * Converts the specified string to a proper Java identifier.
   *
   * @param str the string to be converted
   * @return the Java identifier converted from the specified string
   */
  public static String getJavaIdentifier(String str) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(str));

    StringBuilder result = new StringBuilder();
    char ch = str.charAt(0);
    result.append(Character.isJavaIdentifierStart(ch) ? ch : REPLACE);

    for (int i = 1; i < str.length(); i++) {
      ch = str.charAt(i);
      result.append(Character.isJavaIdentifierPart(ch) ? ch : REPLACE);
    }
    return result.toString();
  }
}
