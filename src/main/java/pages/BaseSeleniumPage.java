package pages;

import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.apache.commons.lang3.ArrayUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static io.qameta.allure.Allure.step;

public class BaseSeleniumPage {
    protected static final Logger LOGGER = LoggerFactory.getLogger("Page");
    public final int DEFAULT_TIMEOUT_SECONDS = 30;
    public WebDriver driver;

    public BaseSeleniumPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public byte[] takeScreenshot() {
        // Use the Page Object's class name when taking a nameless screenshot.
        return takeScreenshot(this.getClass().getSimpleName());
    }

    @Attachment(value = "[Screenshot] {name}", type = "image/png")
    public byte[] takeScreenshot(String name) {
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            step("Unable to take screenshot", () -> {
                Allure.attachment("Exception", e.toString());
            });
            // If the screenshot is not possible, return a broken image.
            return new byte[]{1};
        }
    }

    public void takeElementScreenshot(WebElement element, String name) {
        try {
            Actions actions = new Actions(driver);
            actions.moveToElement(element, 0, 0);
            actions.perform();
            takeScreenshot(name);
        } catch (Exception e) {
            step("Unable to take element screenshot", () -> {
                Allure.attachment("Exception", e.toString());
            });
        }
    }

    public Select selectByVisibleText(WebElement element, String visibleText) {
        Select select = new Select(element);
        select.selectByVisibleText(visibleText);
        return select;
    }

    public void scrollTo(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    /**
     * Returns the key for the selected item in a list.
     *
     * @param element
     * @return
     */
    public String getListSelectedKey(WebElement element) {
        Select select = new Select(element);
        WebElement selected = select.getFirstSelectedOption();
        return selected.getAttribute("value");
    }

    /**
     * Returns the value for the selected item in a list.
     *
     * @param element
     * @return
     */
    public String getListSelectedValue(WebElement element) {
        Select select = new Select(element);
        WebElement selected = select.getFirstSelectedOption();
        return selected.getText();
    }

    public void waitUntilVisible(WebElement element) {
        waitUntilVisible(DEFAULT_TIMEOUT_SECONDS, element);
    }

    public void waitUntilVisible(int timeoutInSeconds, WebElement element) {
        Duration timeoutDuration = Duration.ofSeconds(timeoutInSeconds);
        new WebDriverWait(driver, timeoutDuration).until(ExpectedConditions.visibilityOf(element));
    }

    public void waitUntilInvisible(WebElement element) {
        waitUntilInvisible(DEFAULT_TIMEOUT_SECONDS, element);
    }

    public void waitUntilInvisible(int timeoutInSeconds, WebElement element) {
        Duration timeoutDuration = Duration.ofSeconds(timeoutInSeconds);
        new WebDriverWait(driver, timeoutDuration).until(ExpectedConditions.invisibilityOf(element));
    }

    public void waitUntilInputValue(WebElement element, String expectedValue) {
        waitUntilInputValue(DEFAULT_TIMEOUT_SECONDS, element, expectedValue);
    }

    public void waitUntilInputValue(int timeoutInSeconds, WebElement element, String value) {
        Duration timeoutDuration = Duration.ofSeconds(timeoutInSeconds);
        new WebDriverWait(driver, timeoutDuration).until(ExpectedConditions.attributeToBe(element, "value", value));
    }

    public void waitUntilElementHasClass(WebElement element, String className) {
        waitUntilElementHasClass(DEFAULT_TIMEOUT_SECONDS, element, className);
    }

    public void waitUntilElementHasClass(int timeoutInSeconds, WebElement element, String className) {
        Duration timeoutDuration = Duration.ofSeconds(timeoutInSeconds);
        new WebDriverWait(driver, timeoutDuration).until(ExpectedConditions.attributeContains(element, "class", className));
    }

    public void waitUntilElementDoesNotHaveClass(WebElement element, String className) {
        waitUntilElementDoesNotHaveClass(DEFAULT_TIMEOUT_SECONDS, element, className);
    }

    public void waitUntilElementDoesNotHaveClass(int timeoutInSeconds, WebElement element, String className) {
        Duration timeoutDuration = Duration.ofSeconds(timeoutInSeconds);
        new WebDriverWait(driver, timeoutDuration).until((ExpectedCondition<Boolean>) driver -> !hasClass(element, className));
    }

    /**
     * Checks whether an element has a specific class.
     *
     * @param element
     * @param className
     * @return
     */
    public boolean hasClass(WebElement element, String className) {
        String[] classes = element.getAttribute("class").split(" ");
        return ArrayUtils.contains(classes, className);
    }

    /**
     * Extracts the available options in a select element.
     *
     * @param element The select element.
     * @return A list of the options in the select.
     */
    public List<String> getSelectValues(WebElement element) {
        Select select = new Select(element);
        List<String> options = new ArrayList<>();
        for (WebElement option : select.getOptions()) {
            options.add(option.getText());
        }
        return options;
    }
}
