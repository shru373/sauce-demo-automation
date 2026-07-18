package com.saucedemo.pages;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Shared behaviour for every page object: element lookups always go through an
 * explicit wait, so tests never depend on incidental timing.
 */
public abstract class BasePage {

    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, TIMEOUT);
    }

    protected WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement clickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void click(By locator) {
        clickable(locator).click();
    }

    protected void type(By locator, String text) {
        WebElement field = visible(locator);
        field.clear();
        // sendKeys with an empty string is rejected by some drivers; clearing is
        // enough to represent "the user left this field blank".
        if (text != null && !text.isEmpty()) {
            field.sendKeys(text);
        }
    }

    protected String textOf(By locator) {
        return visible(locator).getText();
    }

    /** The text of every element matching the locator, in document order. Empty if none match. */
    protected List<String> textsOf(By locator) {
        return driver.findElements(locator).stream().map(WebElement::getText).toList();
    }

    /** Blocks until nothing matching the locator is present or visible. */
    protected void waitGone(By locator) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    protected boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
