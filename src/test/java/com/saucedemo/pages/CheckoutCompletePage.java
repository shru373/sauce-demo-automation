package com.saucedemo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** checkout-complete.html: the "Thank you for your order!" confirmation. */
public class CheckoutCompletePage extends BasePage {

    private static final By TITLE = By.cssSelector("[data-test='title']");
    private static final By HEADER = By.cssSelector("[data-test='complete-header']");
    private static final By BACK_HOME = By.cssSelector("[data-test='back-to-products']");

    public CheckoutCompletePage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return driver.getCurrentUrl().contains("/checkout-complete.html") && isDisplayed(TITLE);
    }

    public String title() {
        return textOf(TITLE);
    }

    /** The order confirmation banner, e.g. "Thank you for your order!". */
    public String confirmationHeader() {
        return textOf(HEADER);
    }

    public InventoryPage backHome() {
        navigate(BACK_HOME, "inventory.html");
        return new InventoryPage(driver);
    }
}
