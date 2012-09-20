package com.google.testing.pogen.pages;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import java.util.ArrayList;
import java.util.List;

public class IndexPage extends AbstractPage {
  public IndexPage(WebDriver driver) {
    super(driver);
    assertInvariant();
  }

  private void assertInvariant() {
  }

  /* ------------------- GENERATED CODE START ------------------- */
  @FindBy(how = How.XPATH, using = "//*[@lang='__pogen_0']")
  private WebElement title;
  @FindBy(how = How.XPATH, using = "//*[@lang='__pogen_1']")
  private WebElement value;
  @FindBy(how = How.XPATH, using = "//*[@lang='__pogen_2']")
  private WebElement value2;
  @FindBy(how = How.XPATH, using = "//*[@lang='test3']")
  private WebElement value3;

  public WebElement getElementForTitle() {
    return title;
  }

  public String getTextForTitle() {
    return title.getText();
  }

  public WebElement getElementForValue() {
    return value;
  }

  public String getTextForValue() {
    return value.getText();
  }

  public WebElement getElementForValue2() {
    return value2;
  }

  public String getTextForValue2() {
    return value2.getText();
  }

  public WebElement getElementForValue3() {
    return value3;
  }

  public String getTextForValue3() {
    return value3.getText();
  }

  public List<WebElement> getElementsForValue4() {
    List<WebElement> result = new ArrayList<WebElement>();
    for (WebElement e : driver.findElements(By.xpath("//*[@lang='__pogen_3']"))) {
      result.add(e);
    }
    return result;
  }

  public List<String> getTextsForValue4() {
    List<String> result = new ArrayList<String>();
    for (WebElement e : driver.findElements(By.xpath("//*[@lang='__pogen_3']"))) {
      result.add(e.getText());
    }
    return result;
  }
/* -------------------- GENERATED CODE END -------------------- */
}
