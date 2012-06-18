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

package com.google.testing.pogen.generator.test;

/**
 * This class encapsulates a test-code-updating related error condition that occurred while
 * generating skeleton test code designed by PageObject pattern.
 * 
 * @author Kazunori Sakamoto
 */
public class PageObjectUpdateException extends Exception {

  private static final long serialVersionUID = -3513794700993868129L;

  /**
   * Construct a test-code-updating exception with no detail message.
   */
  public PageObjectUpdateException() {}

  /**
   * Construct a test-code-updating exception chaining the supplied {@link Exception}.
   * 
   * @param exception the supplied {@link Exception}
   */
  public PageObjectUpdateException(Exception exception) {
    super(exception);
  }
}
