package com.saucedemo.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/** The hamburger menu's logout and reset actions. */
public class MenuTest extends BaseTest {

    private static final String PASSWORD = "secret_sauce";
    private static final String BACKPACK = "Sauce Labs Backpack";

    private InventoryPage inventory;

    @BeforeMethod(alwaysRun = true)
    public void logIn() {
        inventory = new LoginPage(driver).open().loginAs("standard_user", PASSWORD);
    }

    @Test(description = "Logout returns to the login screen")
    public void logoutReturnsToLogin() {
        LoginPage login = inventory.logout();

        assertTrue(login.isLoaded(), "Expected to be back on the login screen after logout");
    }

    @Test(description = "Reset App State clears the cart")
    public void resetClearsTheCart() {
        inventory.addToCart(BACKPACK);
        assertEquals(inventory.cartBadgeCount(), 1, "Cart should hold the item before reset");

        inventory.resetAppState();

        assertEquals(inventory.cartBadgeCount(), 0, "Reset App State should empty the cart");
    }
}
