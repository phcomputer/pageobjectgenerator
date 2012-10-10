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

package com.google.testing.pogen.generator.test.java;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests for {@link NameConverter}.
 * 
 * @author Kazunori Sakamoto
 */
@RunWith(JUnit4.class)
public class NameConverterTest {
  @Test
  public void testReplaceSignsToTexts() {
    assertEquals("A_minus_B_minus_C", NameConverter.replaceSignsToTexts("A-B-C"));
    assertEquals("A_plus_B_minus_C_multiply_D_divide_E_dot_F",
        NameConverter.replaceSignsToTexts("A+B-C*D/E.F"));
  }

  @Test
  public void testJavaClassName() {
    assertEquals("ABC", NameConverter.getJavaClassName("a-b-c"));
    assertEquals("ABC", NameConverter.getJavaClassName("a_b_c"));
    assertEquals("ABC", NameConverter.getJavaClassName("a__b__c"));
    assertEquals("ABC", NameConverter.getJavaClassName("_a_b_c_"));
    assertEquals("A2B2C2", NameConverter.getJavaClassName("a2_b2_c2"));
    assertEquals("AaBbCc", NameConverter.getJavaClassName("aa_bb_cc"));
  }

  @Test
  public void testJavaIdentifier() {
    assertEquals("test", NameConverter.getJavaIdentifier("test"));
    assertEquals("a_test", NameConverter.getJavaIdentifier("a.test"));
    assertEquals("_00", NameConverter.getJavaIdentifier("100"));
  }
}
