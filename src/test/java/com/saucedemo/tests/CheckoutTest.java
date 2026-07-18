package com.saucedemo.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import com.saucedemo.pages.CheckoutCompletePage;
import com.saucedemo.pages.CheckoutOverviewPage;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CheckoutTest extends BaseTest {

    private static final String PASSWORD = "secret_sauce";
    private static final String BACKPACK = "Sauce Labs Backpack";

    private InventoryPage inventory;

    @BeforeMethod(alwaysRun = true)
    public void logIn() {
        inventory = new LoginPage(driver).open().loginAs("standard_user", PASSWORD);
    }

    @Test(description = "A user can complete checkout and reach the order confirmation")
    public void completesCheckout() {
        CheckoutCompletePage complete = inventory
                .addToCart(BACKPACK)
                .openCart()
                .checkout()
                .enterInformation("Shrutina", "Prajapati", "12345")
                .continueToOverview()
                .finish();

        assertTrue(complete.isLoaded(), "Expected the order confirmation page");
        assertEquals(complete.title(), "Checkout: Complete!");
        assertEquals(complete.confirmationHeader(), "Thank you for your order!");
    }

    @Test(description = "The overview lists the products being purchased")
    public void overviewListsPurchasedItems() {
        CheckoutOverviewPage overview = inventory
                .addToCart(BACKPACK)
                .openCart()
                .checkout()
                .enterInformation("Shrutina", "Prajapati", "12345")
                .continueToOverview();

        assertTrue(overview.isLoaded(), "Expected the checkout overview page");
        assertEquals(overview.title(), "Checkout: Overview");
        assertTrue(overview.itemNames().contains(BACKPACK), "Overview should list " + BACKPACK);
    }

    @Test(description = "Back home from the confirmation returns to the inventory")
    public void backHomeReturnsToInventory() {
        InventoryPage back = inventory
                .addToCart(BACKPACK)
                .openCart()
                .checkout()
                .enterInformation("Shrutina", "Prajapati", "12345")
                .continueToOverview()
                .finish()
                .backHome();

        assertTrue(back.isLoaded(), "Expected to return to the inventory page");
    }
}
