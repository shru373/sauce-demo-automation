package com.saucedemo.tests;

import static org.testng.Assert.assertEquals;

import com.saucedemo.pages.CheckoutInformationPage;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/** The checkout information form must reject missing fields with a clear message. */
public class CheckoutValidationTest extends BaseTest {

    private static final String PASSWORD = "secret_sauce";
    private static final String BACKPACK = "Sauce Labs Backpack";

    private InventoryPage inventory;

    @BeforeMethod(alwaysRun = true)
    public void logIn() {
        inventory = new LoginPage(driver).open().loginAs("standard_user", PASSWORD);
    }

    /** Every case first gets to the checkout form with a product in the cart. */
    private CheckoutInformationPage checkoutForm() {
        return inventory.addToCart(BACKPACK).openCart().checkout();
    }

    @Test(description = "A blank first name is rejected")
    public void blankFirstNameIsRejected() {
        CheckoutInformationPage form = checkoutForm()
                .enterInformation("", "Prajapati", "12345")
                .continueExpectingError();

        assertEquals(form.errorMessage(), "Error: First Name is required");
    }

    @Test(description = "A blank last name is rejected")
    public void blankLastNameIsRejected() {
        CheckoutInformationPage form = checkoutForm()
                .enterInformation("Shrutina", "", "12345")
                .continueExpectingError();

        assertEquals(form.errorMessage(), "Error: Last Name is required");
    }

    @Test(description = "A blank postal code is rejected")
    public void blankPostalCodeIsRejected() {
        CheckoutInformationPage form = checkoutForm()
                .enterInformation("Shrutina", "Prajapati", "")
                .continueExpectingError();

        assertEquals(form.errorMessage(), "Error: Postal Code is required");
    }
}
