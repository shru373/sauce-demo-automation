package com.saucedemo.listeners;

import com.saucedemo.tests.BaseTest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Captures a screenshot of the browser whenever a test fails.
 *
 * TestNG runs this callback before {@code @AfterMethod}, so the driver is still
 * alive when the picture is taken.
 */
public class ScreenshotListener implements ITestListener {

    private static final Logger log = LoggerFactory.getLogger(ScreenshotListener.class);

    private static final Path OUTPUT_DIR = Path.of("target", "screenshots");
    private static final DateTimeFormatter TIMESTAMP =
            DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

    @Override
    public void onTestFailure(ITestResult result) {
        if (!(driverFor(result) instanceof TakesScreenshot camera)) {
            return;
        }

        try {
            Files.createDirectories(OUTPUT_DIR);
            Path target = OUTPUT_DIR.resolve(fileNameFor(result));
            Files.write(target, camera.getScreenshotAs(OutputType.BYTES));
            log.info("Screenshot of failure written to {}", target.toAbsolutePath());
        } catch (IOException | WebDriverException e) {
            // A failed screenshot must never mask the test failure that caused it.
            log.warn("Could not capture a screenshot for {}", result.getName(), e);
        }
    }

    /**
     * The driver lives on the test instance. A test that failed inside
     * {@code @BeforeMethod} may not have one yet, hence the null tolerance.
     */
    private static WebDriver driverFor(ITestResult result) {
        return result.getInstance() instanceof BaseTest test ? test.getDriver() : null;
    }

    /**
     * Data-driven tests fail once per data set, so the parameters are part of the
     * name to keep three failures of the same method from overwriting each other.
     */
    private static String fileNameFor(ITestResult result) {
        StringBuilder name = new StringBuilder()
                .append(result.getTestClass().getRealClass().getSimpleName())
                .append('.')
                .append(result.getName());

        for (Object parameter : result.getParameters()) {
            name.append('-').append(parameter);
        }

        name.append('-').append(LocalDateTime.now().format(TIMESTAMP)).append(".png");
        return name.toString().replaceAll("[^A-Za-z0-9._-]", "_");
    }
}
