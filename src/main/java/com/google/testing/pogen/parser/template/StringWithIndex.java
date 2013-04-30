package com.google.testing.pogen.parser.template;

/**
 * A class which contains a string and its position.
 */
public class StringWithIndex {
  /**
   * A string.
   */
  private String string;
  /**
   * A position of the string.
   */
  private int index;

  public StringWithIndex(String string, int index) {
    super();
    this.string = string;
    this.index = index;
  }

  public String getString() {
    return string;
  }

  public void setString(String string) {
    this.string = string;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }
}
