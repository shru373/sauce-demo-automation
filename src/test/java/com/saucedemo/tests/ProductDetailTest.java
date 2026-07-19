package com.saucedemo.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.pages.ProductDetailPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/** Opening a product shows its own detail page. */
public class ProductDetailTest extends BaseTest {

    private static final String PASSWORD = "secret_sauce";
    private static final String BACKPACK = "Sauce Labs Backpack";

    private InventoryPage inventory;

    @BeforeMethod(alwaysRun = true)
    public void logIn() {
        inventory = new LoginPage(driver).open().loginAs("standard_user", PASSWORD);
    }

    @Test(description = "Clicking a product opens its detail page with the same name")
    public void openingProductShowsItsDetail() {
        ProductDetailPage detail = inventory.openProduct(BACKPACK);

        assertTrue(detail.isLoaded(), "Expected the product detail page");
        assertEquals(detail.name(), BACKPACK);
    }

    @Test(description = "Back to products returns to the inventory")
    public void backToProductsReturnsToInventory() {
        InventoryPage back = inventory.openProduct(BACKPACK).backToProducts();

        assertTrue(back.isLoaded(), "Expected to return to the inventory page");
    }
}
