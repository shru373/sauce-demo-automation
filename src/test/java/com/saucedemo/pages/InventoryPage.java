package com.saucedemo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class InventoryPage extends BasePage {

    private static final By TITLE = By.className("title");
    private static final By INVENTORY_ITEM = By.className("inventory_item");

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
}
