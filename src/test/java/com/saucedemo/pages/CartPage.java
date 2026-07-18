package com.saucedemo.pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CartPage extends BasePage {

    public static final String URL = "https://www.saucedemo.com/cart.html";

    private static final By TITLE = By.cssSelector("[data-test='title']");
    private static final By CART_ITEM = By.cssSelector("[data-test='inventory-item']");
    private static final By ITEM_NAME = By.cssSelector("[data-test='inventory-item-name']");
    private static final By CONTINUE_SHOPPING = By.cssSelector("[data-test='continue-shopping']");
    private static final By CHECKOUT = By.cssSelector("[data-test='checkout']");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return driver.getCurrentUrl().contains("/cart.html") && isDisplayed(TITLE);
    }

    public String title() {
        return textOf(TITLE);
    }

    public int itemCount() {
        return driver.findElements(CART_ITEM).size();
    }

    /** The names of the products currently in the cart, in listing order. */
    public List<String> itemNames() {
        return textsOf(ITEM_NAME);
    }

    /** Removes a product from the cart, then waits until its row is gone. */
    public CartPage removeItem(String productName) {
        By removeButton = removeButton(productName);
        click(removeButton);
        waitGone(removeButton);
        return this;
    }

    public InventoryPage continueShopping() {
        navigate(CONTINUE_SHOPPING, "inventory.html");
        return new InventoryPage(driver);
    }

    public CheckoutInformationPage checkout() {
        navigate(CHECKOUT, "checkout-step-one");
        return new CheckoutInformationPage(driver);
    }

    private static By removeButton(String productName) {
        return By.xpath(
            "//div[@data-test='inventory-item']"
                + "[.//div[@data-test='inventory-item-name'][normalize-space()='" + productName + "']]"
                + "//button[starts-with(@data-test,'remove')]");
    }
}
