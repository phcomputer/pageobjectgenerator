package com.google.testing.pogen.pages;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IndexPage extends AbstractPage {
  public IndexPage(WebDriver driver) {
    super(driver);
    assertInvariant();
  }
  
  private void assertInvariant() {
  }
  
  /* ------------------- GENERATED CODE START ------------------- */
  private static Pattern commentPattern = Pattern.compile("<!--POGEN,([^,]*),([^,]*),(.*?)-->", Pattern.DOTALL);
  
  @FindBy(how = How.CSS, using = ".__pogen_0")
  private WebElement title;
  
  public WebElement getElementOfTitle() {
    return title;
  }
  
  public String getTextOfTitle() {
    Matcher matcher = commentPattern.matcher(driver.getPageSource());
    while (matcher.find()) {
      if (matcher.group(1).equals("__pogen_0") && matcher.group(2).equals("title")) {
        return matcher.group(3);
      }
    }
    return null;
  }
  
  @FindBy(how = How.CSS, using = ".__pogen_1")
  private WebElement value;
  
  public WebElement getElementOfValue() {
    return value;
  }
  
  public String getTextOfValue() {
    Matcher matcher = commentPattern.matcher(driver.getPageSource());
    while (matcher.find()) {
      if (matcher.group(1).equals("__pogen_1") && matcher.group(2).equals("value")) {
        return matcher.group(3);
      }
    }
    return null;
  }
  
  @FindBy(how = How.CSS, using = ".__pogen_2")
  private WebElement value2;
  
  public WebElement getElementOfValue2() {
    return value2;
  }
  
  public String getTextOfValue2() {
    Matcher matcher = commentPattern.matcher(driver.getPageSource());
    while (matcher.find()) {
      if (matcher.group(1).equals("__pogen_2") && matcher.group(2).equals("value2")) {
        return matcher.group(3);
      }
    }
    return null;
  }
  
  @FindBy(how = How.CSS, using = ".__pogen_3")
  private WebElement value_PO2;
  
  public WebElement getElementOfValue_PO2() {
    return value_PO2;
  }
  
  public String getTextOfValue_PO2() {
    Matcher matcher = commentPattern.matcher(driver.getPageSource());
    while (matcher.find()) {
      if (matcher.group(1).equals("__pogen_3") && matcher.group(2).equals("value")) {
        return matcher.group(3);
      }
    }
    return null;
  }
  
  @FindBy(how = How.CSS, using = ".__pogen_4")
  private WebElement value3;
  
  public WebElement getElementOfValue3() {
    return value3;
  }
  
  public String getTextOfValue3() {
    Matcher matcher = commentPattern.matcher(driver.getPageSource());
    while (matcher.find()) {
      if (matcher.group(1).equals("__pogen_4") && matcher.group(2).equals("value3")) {
        return matcher.group(3);
      }
    }
    return null;
  }
  
  @FindBy(how = How.CSS, using = ".__pogen_5")
  private List<WebElement> value4;
  
  public List<WebElement> getElementsOfValue4() {
    return value4;
  }
  
  public List<String> getTextsOfValue4() {
    List<String> result = new ArrayList<String>();
    Matcher matcher = commentPattern.matcher(driver.getPageSource());
    while (matcher.find()) {
      if (matcher.group(1).equals("__pogen_5") && matcher.group(2).equals("value4")) {
        result.add(matcher.group(3));
      }
    }
    return result;
  }
/* -------------------- GENERATED CODE END -------------------- */
}
