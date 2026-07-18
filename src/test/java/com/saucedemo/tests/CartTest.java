package com.saucedemo.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import java.util.List;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CartTest extends BaseTest {

    private static final String PASSWORD = "secret_sauce";
    private static final String BACKPACK = "Sauce Labs Backpack";
    private static final String BIKE_LIGHT = "Sauce Labs Bike Light";

    private InventoryPage inventory;

    @BeforeMethod(alwaysRun = true)
    public void logIn() {
        inventory = new LoginPage(driver).open().loginAs("standard_user", PASSWORD);
    }

    @Test(description = "The cart badge is absent until an item is added")
    public void cartStartsEmpty() {
        assertEquals(inventory.cartBadgeCount(), 0);
    }

    @Test(description = "Adding items increments the cart badge")
    public void addingItemsUpdatesBadge() {
        inventory.addToCart(BACKPACK);
        assertEquals(inventory.cartBadgeCount(), 1);

        inventory.addToCart(BIKE_LIGHT);
        assertEquals(inventory.cartBadgeCount(), 2);
    }

    @Test(description = "Removing an item from the inventory page decrements the badge")
    public void removingItemUpdatesBadge() {
        inventory.addToCart(BACKPACK).addToCart(BIKE_LIGHT);

        inventory.removeFromCart(BACKPACK);

        assertEquals(inventory.cartBadgeCount(), 1);
    }

    @Test(description = "The cart page lists exactly the products that were added")
    public void cartListsAddedProducts() {
        inventory.addToCart(BACKPACK).addToCart(BIKE_LIGHT);

        CartPage cart = inventory.openCart();

        assertTrue(cart.isLoaded(), "Expected to be on the cart page");
        assertEquals(cart.title(), "Your Cart");
        assertEquals(cart.itemCount(), 2);

        List<String> names = cart.itemNames();
        assertTrue(names.contains(BACKPACK), "Cart should contain " + BACKPACK);
        assertTrue(names.contains(BIKE_LIGHT), "Cart should contain " + BIKE_LIGHT);
    }

    @Test(description = "Removing the last item from the cart empties it")
    public void removingFromCartEmptiesIt() {
        CartPage cart = inventory.addToCart(BACKPACK).openCart();
        assertEquals(cart.itemCount(), 1);

        cart.removeItem(BACKPACK);

        assertEquals(cart.itemCount(), 0, "Cart should be empty after removing the only item");
    }

    @Test(description = "Continue shopping returns to the inventory page")
    public void continueShoppingReturnsToInventory() {
        CartPage cart = inventory.openCart();

        InventoryPage back = cart.continueShopping();

        assertTrue(back.isLoaded(), "Expected to return to the inventory page");
    }
}
