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

package com.google.testing.pogen.generator.test.java.page;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.openqa.selenium.WebDriver;

/**
 * Tests for {@link AbstractPage}.
 *
 * @author Kazunori Sakamoto
 */
@RunWith(JUnit4.class)
public class AbstractPageTest {
  static class AbstractPageForTest extends AbstractPage {
    public AbstractPageForTest(WebDriver driver) {
      super(driver);
      assertSame(driver, this.driver);
    }
  }

  @Test
  public void createAbstractPage() {
    new AbstractPageForTest(mock(WebDriver.class));
  }
}
