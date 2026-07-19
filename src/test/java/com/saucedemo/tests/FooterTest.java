package com.saucedemo.tests;

import static org.testng.Assert.assertTrue;

import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * The footer social links should point at the right destinations. We assert on the href
 * rather than following the link, because the target sites are third parties we don't test.
 */
public class FooterTest extends BaseTest {

    private static final String PASSWORD = "secret_sauce";

    private InventoryPage inventory;

    @BeforeMethod(alwaysRun = true)
    public void logIn() {
        inventory = new LoginPage(driver).open().loginAs("standard_user", PASSWORD);
    }

    @Test(description = "The Twitter/X link points at the right profile")
    public void twitterLinkIsCorrect() {
        String href = inventory.twitterLink();
        assertTrue(href.contains("twitter.com") || href.contains("x.com"),
                "Unexpected Twitter link: " + href);
    }

    @Test(description = "The Facebook link points at the right page")
    public void facebookLinkIsCorrect() {
        assertTrue(inventory.facebookLink().contains("facebook.com"),
                "Unexpected Facebook link: " + inventory.facebookLink());
    }

    @Test(description = "The LinkedIn link points at the right page")
    public void linkedinLinkIsCorrect() {
        assertTrue(inventory.linkedinLink().contains("linkedin.com"),
                "Unexpected LinkedIn link: " + inventory.linkedinLink());
    }
}
