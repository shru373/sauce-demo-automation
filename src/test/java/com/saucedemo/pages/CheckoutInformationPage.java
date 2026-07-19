package com.saucedemo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** checkout-step-one.html: the buyer's name and postal code. */
public class CheckoutInformationPage extends BasePage {

    private static final By TITLE = By.cssSelector("[data-test='title']");
    private static final By FIRST_NAME = By.cssSelector("[data-test='firstName']");
    private static final By LAST_NAME = By.cssSelector("[data-test='lastName']");
    private static final By POSTAL_CODE = By.cssSelector("[data-test='postalCode']");
    private static final By CONTINUE = By.cssSelector("[data-test='continue']");
    private static final By ERROR = By.cssSelector("[data-test='error']");

    public CheckoutInformationPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return driver.getCurrentUrl().contains("/checkout-step-one.html") && isDisplayed(TITLE);
    }

    public String title() {
        return textOf(TITLE);
    }

    /** Fills the form. Blank values are allowed on purpose, so validation can be tested later. */
    public CheckoutInformationPage enterInformation(String firstName, String lastName, String postalCode) {
        type(FIRST_NAME, firstName);
        type(LAST_NAME, lastName);
        type(POSTAL_CODE, postalCode);
        return this;
    }

    public CheckoutOverviewPage continueToOverview() {
        navigate(CONTINUE, "checkout-step-two");
        return new CheckoutOverviewPage(driver);
    }

    /**
     * Clicks Continue but stays here, for cases where the form is expected to be rejected.
     * Uses a JS click because this app's form ignores a native click (see BasePage.navigate),
     * and a click that never reaches React would never raise the validation error we assert on.
     */
    public CheckoutInformationPage continueExpectingError() {
        jsClick(CONTINUE);
        return this;
    }

    public String errorMessage() {
        return textOf(ERROR);
    }
}
