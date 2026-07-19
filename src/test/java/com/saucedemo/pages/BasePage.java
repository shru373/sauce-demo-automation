package com.saucedemo.pages;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
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

    /** Clicks via JavaScript so the event reaches React even when a native click is ignored. */
    protected void jsClick(By locator) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", clickable(locator));
    }

    /**
     * Clicks a control that navigates to another page, then confirms we arrived.
     *
     * On this app's checkout pages, a normal Selenium click doesn't always reach React's
     * handler (the same reason typing needs a JS fallback in {@link #type}); the browser
     * then does a plain form submit that just reloads the page, so we appear stuck. If the
     * first click doesn't take us to the destination, we retry it as a JavaScript click,
     * which dispatches an event React does react to. Idempotent navigation makes this safe.
     */
    protected void navigate(By control, String destinationUrlFragment) {
        click(control);
        try {
            wait.until(ExpectedConditions.urlContains(destinationUrlFragment));
        } catch (TimeoutException firstClickIgnored) {
            jsClick(control);
            wait.until(ExpectedConditions.urlContains(destinationUrlFragment));
        }
    }

    /** Like {@link #navigate}, but confirms arrival by an element appearing rather than a URL. */
    protected void navigateToElement(By control, By destinationMarker) {
        click(control);
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(destinationMarker));
        } catch (TimeoutException firstClickIgnored) {
            jsClick(control);
            wait.until(ExpectedConditions.visibilityOfElementLocated(destinationMarker));
        }
    }

    protected void type(By locator, String text) {
        WebElement field = visible(locator);
        field.clear();
        // Clearing alone represents "the user left this field blank"; sendKeys rejects "".
        if (text == null || text.isEmpty()) {
            return;
        }
        field.sendKeys(text);
        // Some of this app's React-controlled inputs (the checkout form) silently swallow
        // synthesized keystrokes, leaving the field empty. Where sendKeys works (e.g. login)
        // we keep the real typing; only when it didn't take do we fall back to setting the
        // value natively and firing the input event React listens for.
        if (!text.equals(field.getAttribute("value"))) {
            setValueViaReact(field, text);
        }
    }

    private void setValueViaReact(WebElement field, String text) {
        ((JavascriptExecutor) driver).executeScript(
            "const el = arguments[0], value = arguments[1];"
                + "const setter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;"
                + "setter.call(el, value);"
                + "el.dispatchEvent(new Event('input', { bubbles: true }));",
            field, text);
    }

    protected String textOf(By locator) {
        return visible(locator).getText();
    }

    protected String attributeOf(By locator, String attribute) {
        return visible(locator).getAttribute(attribute);
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
