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

package com.google.testing.pogen;

import java.io.File;
import java.io.IOException;

/**
 * This class encapsulates a processing-file error condition that occurred while
 * processing for propagating errors in the main method.
 *
 * @author Kazunori Sakamoto
 */
public class FileProcessException extends IOException {

  /**
   * Constructs a processing-file exception with the specified file and the
   * specified detail message without period.
   *
   * @param message the string to describe the detail message without period
   * @param file the file that caused the error
   */
  public FileProcessException(String message, File file) {
    super(message + ":\n" + file.getPath() + ".");
  }

  /**
   * Constructs a processing-file exception with the specified file and the
   * specified detail message without period and the supplied {@link Exception}.
   *
   * @param message the string to describe the detail message without period
   * @param file the file that caused the error
   * @param exception the supplied {@link Exception}
   */
  public FileProcessException(String message, File file, Exception exception) {
    super(message + ":\n" + file.getPath() + "."
        + (exception.getMessage() != null ? "\n" + exception.getMessage() : ""));
  }
}
