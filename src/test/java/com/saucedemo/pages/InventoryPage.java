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
    private static final By MENU_BUTTON = By.id("react-burger-menu-btn");
    private static final By MENU_CLOSE = By.id("react-burger-cross-btn");
    private static final By LOGOUT = By.cssSelector("[data-test='logout-sidebar-link']");
    private static final By RESET = By.cssSelector("[data-test='reset-sidebar-link']");
    private static final By LOGIN_BUTTON = By.id("login-button");
    private static final By TWITTER = By.cssSelector("[data-test='social-twitter']");
    private static final By FACEBOOK = By.cssSelector("[data-test='social-facebook']");
    private static final By LINKEDIN = By.cssSelector("[data-test='social-linkedin']");

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
        // The button flips from "Add to cart" to "Remove" once the item is in the cart;
        // waiting for that flip means the badge has settled before any test reads it, and
        // lets us retry the click (some React clicks are swallowed, especially on Linux CI).
        clickUntilVisible(addButton(productName), removeButton(productName));
        return this;
    }

    /** Removes a product from the cart via the inventory listing, then waits until the removal is confirmed. */
    public InventoryPage removeFromCart(String productName) {
        clickUntilVisible(removeButton(productName), addButton(productName));
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

    /** Opens the hamburger menu and waits for it to slide open (retrying the click if ignored). */
    private void openMenu() {
        clickUntilVisible(MENU_BUTTON, LOGOUT); // a menu item appearing means the panel is open
    }

    /** Logs out via the menu, returning to the login screen. */
    public LoginPage logout() {
        openMenu();
        clickUntilVisible(LOGOUT, LOGIN_BUTTON);
        return new LoginPage(driver);
    }

    /** Resets the app state (clears the cart), then closes the menu. Stays on this page. */
    public InventoryPage resetAppState() {
        openMenu();
        jsClick(RESET); // a menu onClick action with nothing to navigate to; JS makes it reliable
        clickUntilGone(MENU_CLOSE);
        return this;
    }

    /** Opens a product's own page by clicking its name. */
    public ProductDetailPage openProduct(String productName) {
        By nameLink = By.xpath(
            "//div[@data-test='inventory-item-name'][normalize-space()='" + productName + "']");
        navigate(nameLink, "inventory-item.html");
        return new ProductDetailPage(driver);
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

    public String twitterLink() {
        return attributeOf(TWITTER, "href");
    }

    public String facebookLink() {
        return attributeOf(FACEBOOK, "href");
    }

    public String linkedinLink() {
        return attributeOf(LINKEDIN, "href");
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
