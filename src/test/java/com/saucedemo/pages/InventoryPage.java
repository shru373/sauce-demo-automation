package com.saucedemo.pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class InventoryPage extends BasePage {

    private static final By TITLE = By.className("title");
    private static final By INVENTORY_ITEM = By.className("inventory_item");
    private static final By CART_LINK = By.cssSelector("[data-test='shopping-cart-link']");
    private static final By CART_BADGE = By.cssSelector("[data-test='shopping-cart-badge']");

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    public String title() {
        return textOf(TITLE);
    }

    public int itemCount() {
        visible(INVENTORY_ITEM);
        return driver.findElements(INVENTORY_ITEM).size();
    }

    public boolean isLoaded() {
        return driver.getCurrentUrl().contains("/inventory.html") && isDisplayed(TITLE);
    }

    /** Adds a product to the cart by its visible name, then waits until the add is confirmed. */
    public InventoryPage addToCart(String productName) {
        click(addButton(productName));
        // The button flips from "Add to cart" to "Remove" once the item is in the cart;
        // waiting for that flip means the badge has settled before any test reads it.
        visible(removeButton(productName));
        return this;
    }

    /** Removes a product from the cart via the inventory listing, then waits until the removal is confirmed. */
    public InventoryPage removeFromCart(String productName) {
        click(removeButton(productName));
        visible(addButton(productName));
        return this;
    }

    /** The cart badge is absent (not zero) when the cart is empty, so a missing badge means 0. */
    public int cartBadgeCount() {
        List<WebElement> badge = driver.findElements(CART_BADGE);
        return badge.isEmpty() ? 0 : Integer.parseInt(badge.get(0).getText().trim());
    }

    public CartPage openCart() {
        click(CART_LINK);
        return new CartPage(driver);
    }

    // Each product card holds exactly one button, toggling between "Add to cart" and "Remove".
    // We find the card by its visible product name, so there is no element id to slug together
    // (which would break on names like "Test.allTheThings() T-Shirt (Red)").
    private static By addButton(String productName) {
        return productButton(productName, "add-to-cart");
    }

    private static By removeButton(String productName) {
        return productButton(productName, "remove");
    }

    private static By productButton(String productName, String action) {
        return By.xpath(
            "//div[@data-test='inventory-item']"
                + "[.//div[@data-test='inventory-item-name'][normalize-space()='" + productName + "']]"
                + "//button[starts-with(@data-test,'" + action + "')]");
    }
}
