package com.saucedemo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** inventory-item.html: a single product's own page, reached by clicking its name. */
public class ProductDetailPage extends BasePage {

    private static final By NAME = By.cssSelector("[data-test='inventory-item-name']");
    private static final By PRICE = By.cssSelector("[data-test='inventory-item-price']");
    private static final By BACK = By.cssSelector("[data-test='back-to-products']");

    public ProductDetailPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return driver.getCurrentUrl().contains("/inventory-item.html") && isDisplayed(NAME);
    }

    public String name() {
        return textOf(NAME);
    }

    public double price() {
        return Double.parseDouble(textOf(PRICE).replace("$", "").trim());
    }

    public InventoryPage backToProducts() {
        navigate(BACK, "inventory.html");
        return new InventoryPage(driver);
    }
}
