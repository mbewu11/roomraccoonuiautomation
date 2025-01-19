package testng;

import Environment.Environment;
import io.qameta.allure.Allure;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import pages.BaseSeleniumPage;
import selenium.WebDriverFactory;

import java.lang.reflect.Method;

import static io.qameta.allure.Allure.step;

public class BaseSeleniumTest extends BaseFrameworkTest {
    protected static final ThreadLocal<RemoteWebDriver> drivers = new ThreadLocal<>();

    protected RemoteWebDriver getDriver() {
        return drivers.get();
    }

    @Override
    @BeforeMethod
    public void beforeTest(Method method) {
        super.beforeTest(method);
        step("Open browser", (step) -> {
            RemoteWebDriver driver = (RemoteWebDriver) WebDriverFactory.createWebDriver();
            drivers.set(driver);
            driver.get(Environment.getInstance().getUrl());
        });
    }

    @Override
    @AfterMethod
    public void afterTest(ITestResult result, Method method) {
        super.afterTest(result, method);

        step("Close browser", () -> {
            RemoteWebDriver driver = getDriver();
            if (driver == null) {
                Allure.attachment("Test Failure", "WebDriver was not available for screenshot.");
            } else {
                BaseSeleniumPage page = new BaseSeleniumPage(driver);
                if (!result.isSuccess()) {
                    page.takeScreenshot("Test Failed");
                }

                // Keep the browser open if requested with "-Dkeep".
                boolean keep = System.getProperty("keep") != null;
                if (!keep) {
                    try {
                        driver.quit();
                    } catch (Exception e) {
                        step("Unable to close browser", () -> {
                            Allure.attachment("Exception", e.toString());
                        });
                    }
                }
            }
        });
    }
}
