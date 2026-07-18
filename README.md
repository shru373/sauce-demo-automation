# sauce-demo-automation

UI test automation suite for [saucedemo.com](https://www.saucedemo.com/), built with Selenium WebDriver and TestNG using the Page Object Model.

## Stack

| | |
|---|---|
| Language | Java 21 |
| Build | Maven 3.9 |
| Browser automation | Selenium WebDriver 4.33 |
| Test framework | TestNG 7.11 |
| Design pattern | Page Object Model |

Driver binaries are resolved automatically by [Selenium Manager](https://www.selenium.dev/documentation/selenium_manager/), so there is no `chromedriver.exe` to download, check in, or keep in sync with your browser.

## Running the tests

```bash
mvn test
```

Tests run headless by default. Useful overrides:

```bash
mvn test -Dheadless=false        # watch the browser drive itself
mvn test -Dbrowser=firefox       # chrome (default) or firefox
```

## Project layout

```
src/test/java/com/saucedemo/
├── pages/
│   ├── BasePage.java        # shared waits and element helpers
│   ├── LoginPage.java       # the login form
│   └── InventoryPage.java   # the product listing
├── listeners/
│   └── ScreenshotListener.java  # screenshots failures
└── tests/
    ├── BaseTest.java        # browser lifecycle (fresh driver per test)
    └── LoginTest.java       # login scenarios
src/test/resources/
└── testng.xml               # suite definition
```

## Design notes

**Page objects expose intent, not mechanics.** A test says `loginAs("standard_user", PASSWORD)`, not "type into `#user-name`, type into `#password`, click `#login-button`". Locators live in exactly one place, so a markup change is a one-line fix rather than a suite-wide rewrite.

**Two login methods, on purpose.** `loginAs` returns an `InventoryPage`, `loginExpectingFailure` returns a `LoginPage`. The return type encodes where you end up, so a test that expects a rejection can't accidentally assert against a page it never reached.

**Explicit waits only.** Every lookup goes through `WebDriverWait` in `BasePage`. There are no `Thread.sleep` calls and no implicit wait, which is what keeps the suite from being flaky against `performance_glitch_user` (a deliberately slow account) without padding every test with dead time.

**A fresh browser per test method.** Slower than sharing one session, but each test starts from a known-clean state and no test can leak state into another. This is the precondition for running the suite in parallel.

**Failures screenshot themselves.** `ScreenshotListener` writes a PNG to `target/screenshots/` whenever a test fails, named after the test and its data set (`LoginTest.validUserReachesInventory-problem_user-<timestamp>.png`). TestNG fires the listener before `@AfterMethod`, so the browser is still alive when the picture is taken. A screenshot that fails to save is logged and swallowed, never masking the test failure that triggered it.

**Interactions degrade gracefully.** The checkout form is React-controlled, and in some browser/driver combinations it silently ignores synthesized keystrokes and clicks — the field stays empty, or Continue does nothing but reload. `BasePage.type` and `BasePage.navigate` try the real user interaction first and, only when it demonstrably didn't take (the value didn't stick, or the URL didn't change), fall back to a JavaScript path that dispatches the event React listens for. Pages where normal interaction works (e.g. login) never hit the fallback, so the suite still exercises genuine typing and clicking wherever it can.

## Coverage

**Login** exercises both the happy path and the failure modes SauceDemo deliberately ships:

- `standard_user`, `problem_user`, `performance_glitch_user` all reach the inventory page with all 6 products rendered (data-driven via `@DataProvider`)
- `locked_out_user` is rejected with the lockout message
- A wrong password is rejected without disclosing which field was wrong
- A blank username is rejected
- A blank password is rejected

**Cart** covers adding and removing products from both the inventory listing and the cart page:

- The cart badge is absent on an empty cart, and counts up and down as items are added and removed
- The cart page lists exactly the products that were added
- Removing the last item empties the cart
- Continue shopping returns to the inventory page

**Checkout** walks the full purchase journey across all three checkout pages:

- A user can complete checkout and reach the "Thank you for your order!" confirmation
- The overview page lists the products being purchased
- Back home from the confirmation returns to the inventory page

## Roadmap

- [ ] Cart and checkout flows
- [ ] Product sorting assertions
- [x] Screenshot capture on failure via a TestNG listener
- [ ] Parallel execution
- [ ] Allure reporting
- [ ] CI on GitHub Actions
