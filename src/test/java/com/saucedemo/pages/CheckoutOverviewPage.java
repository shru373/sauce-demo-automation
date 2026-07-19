package com.saucedemo.pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** checkout-step-two.html: the order summary with item total, tax, and grand total. */
public class CheckoutOverviewPage extends BasePage {

    private static final By TITLE = By.cssSelector("[data-test='title']");
    private static final By ITEM_NAME = By.cssSelector("[data-test='inventory-item-name']");
    private static final By ITEM_PRICE = By.cssSelector("[data-test='inventory-item-price']"); // "$29.99"
    private static final By ITEM_TOTAL = By.cssSelector("[data-test='subtotal-label']"); // "Item total: $39.98"
    private static final By TAX = By.cssSelector("[data-test='tax-label']");              // "Tax: $3.20"
    private static final By TOTAL = By.cssSelector("[data-test='total-label']");          // "Total: $43.18"
    private static final By FINISH = By.cssSelector("[data-test='finish']");

    public CheckoutOverviewPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return driver.getCurrentUrl().contains("/checkout-step-two.html") && isDisplayed(TITLE);
    }

    public String title() {
        return textOf(TITLE);
    }

    public List<String> itemNames() {
        return textsOf(ITEM_NAME);
    }

    /** The price of each line item, e.g. [29.99, 9.99]. */
    public List<Double> itemPrices() {
        return textsOf(ITEM_PRICE).stream().map(CheckoutOverviewPage::money).toList();
    }

    /** The sum of the individual line-item prices, which should equal {@link #itemTotal()}. */
    public double lineItemsSum() {
        return itemPrices().stream().mapToDouble(Double::doubleValue).sum();
    }

    /** The pre-tax total the page claims for the items. */
    public double itemTotal() {
        return money(textOf(ITEM_TOTAL));
    }

    public double tax() {
        return money(textOf(TAX));
    }

    /** The grand total the page charges the buyer. */
    public double total() {
        return money(textOf(TOTAL));
    }

    public CheckoutCompletePage finish() {
        navigate(FINISH, "checkout-complete");
        return new CheckoutCompletePage(driver);
    }

    /** Pulls the dollar amount out of a summary label such as "Item total: $39.98". */
    private static double money(String label) {
        return Double.parseDouble(label.substring(label.indexOf('$') + 1).trim());
    }
}
