package com.saucedemo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    public static final String URL = "https://www.saucedemo.com/";

    private static final By USERNAME = By.id("user-name");
    private static final By PASSWORD = By.id("password");
    private static final By LOGIN_BUTTON = By.id("login-button");
    private static final By ERROR_MESSAGE = By.cssSelector("[data-test='error']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open() {
        driver.get(URL);
        visible(USERNAME);
        return this;
    }

    public boolean isLoaded() {
        return isDisplayed(LOGIN_BUTTON);
    }

    /** Submits credentials that are expected to succeed. */
    public InventoryPage loginAs(String username, String password) {
        submitCredentials(username, password);
        return new InventoryPage(driver);
    }

    /** Submits credentials that are expected to be rejected, staying on this page. */
    public LoginPage loginExpectingFailure(String username, String password) {
        submitCredentials(username, password);
        return this;
    }

    private void submitCredentials(String username, String password) {
        type(USERNAME, username);
        type(PASSWORD, password);
        click(LOGIN_BUTTON);
    }

    public String errorMessage() {
        return textOf(ERROR_MESSAGE);
    }

    public boolean hasError() {
        return isDisplayed(ERROR_MESSAGE);
    }
}
