package com.saucedemo.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    private static final String PASSWORD = "secret_sauce";

    @DataProvider(name = "validUsers")
    public Object[][] validUsers() {
        return new Object[][] {
            {"standard_user"},
            {"problem_user"},
            {"performance_glitch_user"}
        };
    }

    @Test(dataProvider = "validUsers", description = "Accepted users land on the inventory page")
    public void validUserReachesInventory(String username) {
        InventoryPage inventory = new LoginPage(driver).open().loginAs(username, PASSWORD);

        assertTrue(inventory.isLoaded(), "Expected the inventory page after logging in as " + username);
        assertEquals(inventory.title(), "Products");
        assertEquals(inventory.itemCount(), 6, "Expected the full product catalogue to render");
    }

    @Test(description = "A locked out account is rejected with an explanatory error")
    public void lockedOutUserIsRejected() {
        LoginPage login = new LoginPage(driver).open()
                .loginExpectingFailure("locked_out_user", PASSWORD);

        assertEquals(login.errorMessage(), "Epic sadface: Sorry, this user has been locked out.");
    }

    @Test(description = "A wrong password is rejected without revealing which field was wrong")
    public void wrongPasswordIsRejected() {
        LoginPage login = new LoginPage(driver).open()
                .loginExpectingFailure("standard_user", "definitely_not_the_password");

        assertEquals(login.errorMessage(),
                "Epic sadface: Username and password do not match any user in this service");
    }

    @Test(description = "A blank username is rejected")
    public void blankUsernameIsRejected() {
        LoginPage login = new LoginPage(driver).open()
                .loginExpectingFailure("", PASSWORD);

        assertEquals(login.errorMessage(), "Epic sadface: Username is required");
    }

    @Test(description = "A blank password is rejected")
    public void blankPasswordIsRejected() {
        LoginPage login = new LoginPage(driver).open()
                .loginExpectingFailure("standard_user", "");

        assertEquals(login.errorMessage(), "Epic sadface: Password is required");
    }
}
