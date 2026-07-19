package com.saucedemo.pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class InventoryPage extends BasePage {

    private static final By TITLE = By.className("title");
    private static final By INVENTORY_ITEM = By.className("inventory_item");
    private static final By CART_LINK = By.cssSelector("[data-test='shopping-cart-link']");
    private static final By CART_BADGE = By.cssSelector("[data-test='shopping-cart-badge']");
    private static final By SORT = By.cssSelector("[data-test='product-sort-container']");
    private static final By ITEM_NAME = By.cssSelector("[data-test='inventory-item-name']");
    private static final By ITEM_PRICE = By.cssSelector("[data-test='inventory-item-price']");

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
        navigate(CART_LINK, "cart.html");
        return new CartPage(driver);
    }

    /** Chooses a sort order by the dropdown's option value: az, za, lohi, or hilo. */
    public InventoryPage sortBy(String optionValue) {
        new Select(visible(SORT)).selectByValue(optionValue);
        return this;
    }

    /** Product names in the order currently displayed. */
    public List<String> productNames() {
        return textsOf(ITEM_NAME);
    }

    /** Product prices in the order currently displayed, e.g. [7.99, 9.99, ...]. */
    public List<Double> productPrices() {
        return textsOf(ITEM_PRICE).stream()
                .map(price -> Double.parseDouble(price.replace("$", "").trim()))
                .toList();
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
