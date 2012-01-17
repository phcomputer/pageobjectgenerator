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

package com.google.testing.pogen.measurer;

import com.google.common.base.Preconditions;

/**
 * A class to represent the measurement result of template-variable coverage
 * defined as "the number of variables with id / the number of all variables".
 *
 * @author Kazunori Sakamoto
 */
public class VariableCoverage {
  /**
   * The number of all template variables.
   */
  private final int allVariableCount;

  /**
   * The number of template variables with id attributes.
   */
  private final int variableWithIdCount;

  /**
   * Constructs an instance with the specified the numbers of all template
   * variables and of template variables with id attributes.
   *
   * @param allVariableCount the number of all template variables
   * @param variableWithIdCount the number of template variables with id
   *        attributes
   */
  public VariableCoverage(int allVariableCount, int variableWithIdCount) {
    Preconditions.checkArgument(allVariableCount >= 0);
    Preconditions.checkArgument(variableWithIdCount >= 0);

    this.allVariableCount = allVariableCount;
    this.variableWithIdCount = variableWithIdCount;
  }

  /**
   * Returns template-variable coverage defined as
   * "the number of variables with id / the number of all variables". Note that
   * returns NaN if there is no template variable.
   *
   * @return the template-variable coverage defined as
   *         "the number of variables with id / the number of all variables" if
   *         there are one or more template variables, {@code NaN} otherwise.
   */
  public double getCoverage() {
    // TODO(kazuu): Avoid dividing by zero
    return (double) variableWithIdCount / allVariableCount;
  }

  public int getAllVariableCount() {
    return allVariableCount;
  }

  public int getVariableWithIdCount() {
    return variableWithIdCount;
  }
}
