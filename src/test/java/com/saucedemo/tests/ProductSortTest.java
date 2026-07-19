package com.saucedemo.tests;

import static org.testng.Assert.assertEquals;

import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import java.util.Comparator;
import java.util.List;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/** The product sort dropdown must actually reorder the listing. */
public class ProductSortTest extends BaseTest {

    private static final String PASSWORD = "secret_sauce";

    private InventoryPage inventory;

    @BeforeMethod(alwaysRun = true)
    public void logIn() {
        inventory = new LoginPage(driver).open().loginAs("standard_user", PASSWORD);
    }

    @Test(description = "Name (A to Z) lists products alphabetically")
    public void sortsByNameAscending() {
        List<String> names = inventory.sortBy("az").productNames();

        assertEquals(names, names.stream().sorted().toList(),
                "Products should be in ascending name order");
    }

    @Test(description = "Name (Z to A) lists products reverse-alphabetically")
    public void sortsByNameDescending() {
        List<String> names = inventory.sortBy("za").productNames();

        assertEquals(names, names.stream().sorted(Comparator.reverseOrder()).toList(),
                "Products should be in descending name order");
    }

    @Test(description = "Price (low to high) lists products cheapest first")
    public void sortsByPriceAscending() {
        List<Double> prices = inventory.sortBy("lohi").productPrices();

        assertEquals(prices, prices.stream().sorted().toList(),
                "Products should be in ascending price order");
    }

    @Test(description = "Price (high to low) lists products most expensive first")
    public void sortsByPriceDescending() {
        List<Double> prices = inventory.sortBy("hilo").productPrices();

        assertEquals(prices, prices.stream().sorted(Comparator.reverseOrder()).toList(),
                "Products should be in descending price order");
    }
}
