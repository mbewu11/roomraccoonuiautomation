package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static io.qameta.allure.Allure.attachment;
import static io.qameta.allure.Allure.step;

public class BasePage extends BaseSeleniumPage {
    public final int DEFAULT_TIMEOUT_SECONDS_UNTIL_BUSY = 1;
    public final int DEFAULT_TIMEOUT_SECONDS_UNTIL_READY = 120;
    @FindBy(css = "[id$='Update']")
    public WebElement okButton;
    @FindBy(css = "[id$='gw-GuidewireLogoWidget']")
    private WebElement logoImage;

    public BasePage(WebDriver driver) {
        super(driver);
    }

    public void switchToIFrame(String iframeName) {
        driver.switchTo().frame(iframeName);
    }

    /**
     * Removes focus from the current element with focus.
     */
    public void removeFocus() {
        // Click the menu bar icon, because it is always available and it does not trigger any other actions.
        logoImage.click();
//        waitPageBusyPC();
    }

    public void longClick(WebElement element) {
        Actions builder = new Actions(driver);
        builder.moveToElement(element).clickAndHold().pause(300).release().build().perform();
    }

    @Step
    public void clickOkButtonIfDisplayed() {
        Boolean isVisible = false;
        try {
            isVisible = okButton.isDisplayed();
        } catch (NoSuchElementException e) {
            //no message
        }
        if (isVisible) {
            okButton.click();
        }
    }

    /**
     * Set an input field's value.
     * <p>
     * //     * @param element
     * //     * @param value
     */
    public void setValue(WebElement element, String value) {
        element.clear();
        if (value.contains(Keys.TAB)) {
            waitPageBusy();
        }
        element.sendKeys(value);
        if (value.contains(Keys.TAB)) {
            waitPageBusy();
        }
    }

    public void selectFromList(WebElement element, String value) {
        scrollTo(element);
        try {
            selectByVisibleText(element, value);
        } catch (NoSuchElementException e) {
            // Element not found
            step("Missing list item: " + value, () -> {
                attachment("Available values", String.join("\n", getSelectValues(element)));
                throw new NoSuchElementException("Missing list item: " + value, e);
            });
        }
    }

    @Step
    public void clickElement(WebElement element) {
        waitUntilVisible(element);
        scrollTo(element);
        element.click();
    }

    /**
     * Method to get text from element on the webpage
     * <p>
     * //     * @param locator
     */
    public String getTextByXPath(String xpath) {
        String val = "";
        try {
            WebElement element = driver.findElement(By.xpath(xpath));
            val = element.getAttribute("value");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Unable to get Text");
        }
        return val;
    }

    public void selectByText(By by, String text) {
        Select select = new Select(driver.findElement(by));
        select.selectByVisibleText(text);
    }

    public void waitPageBusy() {
        // Wait a short while for the page to become busy.
        // And then wait a long time for the page to become ready.
        // If the page becomes ready in less than the minimum time, then only that time will be wasted.
        waitPageBusy(DEFAULT_TIMEOUT_SECONDS_UNTIL_BUSY, DEFAULT_TIMEOUT_SECONDS_UNTIL_READY);
    }

    private void waitPageBusy(int default_timeout_seconds_until_busy, int default_timeout_seconds_until_ready) {
    }

    @Step
    public List<String> getSelectValues(WebElement element, WebElement frame) {
        driver.switchTo().frame(frame);
        Select select = new Select(element);
        List<String> options = new ArrayList<>();
        for (WebElement option : select.getOptions()) {
            options.add(option.getText());
        }
        driver.switchTo().defaultContent();
        return options;
    }

    public void selectFromListPartial(WebElement element, String value) {
        Select select = new Select(element);
        String fullValue = null;
        List<String> allValues = new ArrayList<>();
        for (WebElement option : select.getOptions()) {
            String text = option.getText();
            allValues.add(text);
            if (fullValue == null && text.startsWith(value)) {
                fullValue = text;
            }
        }
        if (fullValue == null) {
            step("Select from list (partial)", () -> {
                step("Expected value: " + value);
                attachment("Available values", String.join("\n", allValues));
                throw new NoSuchElementException("Missing list item starting with: " + value);
            });
        }
        select.selectByVisibleText(fullValue);
        waitPageBusy();
    }

    public void selectFromListContains(WebElement element, String value) {
        Select select = new Select(element);
        String fullValue = null;
        List<String> allValues = new ArrayList<>();
        for (WebElement option : select.getOptions()) {
            String text = option.getText();
            allValues.add(text);
            if (fullValue == null && text.contains(value)) {
                fullValue = text;
            }
        }
        if (fullValue == null) {
            step("Select from list (contains)", () -> {
                step("Expected value: " + value);
                attachment("Available values", String.join("\n", allValues));
                throw new NoSuchElementException("Missing list item containing: " + value);
            });
        }
        select.selectByVisibleText(fullValue);
        waitPageBusy();
    }

    public String getRadioSelectedValue(WebElement wrapper) {
        try {
            return wrapper.findElement(By.xpath(".//label[.//input[@checked]]/span[contains(@class, 'gw-label--inner')]")).getText();
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    @Step
    public void selectRadio(WebElement wrapper, String value) {
        scrollTo(wrapper);
        String xpath = ".//label[span/text()='" + value + "']//input";
        try {
            WebElement radioButton = wrapper.findElement(By.xpath(xpath));
            radioButton.click();
            waitPageBusy();
        } catch (NoSuchElementException e) {
            // Radio button not found.
            step("Missing radio button: " + value, () -> {
                attachment("Available values", String.join("\n", getRadioValues(wrapper)));
                throw new NoSuchElementException("Missing radio button: " + value, e);
            });
        }
    }

    public List<String> getRadioValues(WebElement wrapper) {
        try {
            List<WebElement> labels = wrapper.findElements(By.xpath(".//label/span[contains(@class, 'gw-label--inner')]"));
            return labels.stream().map(WebElement::getText).collect(Collectors.toList());
        } catch (NoSuchElementException e) {
            return Collections.emptyList();
        }
    }

    public boolean isElementVisible(WebElement element, String elementName) {
        boolean bFlag = false;
        try {
            if (element.isDisplayed()) {
                bFlag = true;
            } else {
                System.out.println("Element " + elementName + " is not visible on screen");
            }
        } catch (Exception e) {
            System.out.println("Unable to find element : " + elementName + "on screen");
        }
        return bFlag;
    }
}
