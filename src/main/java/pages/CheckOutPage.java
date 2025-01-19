package pages;

import io.qameta.allure.Step;
import junit.framework.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;


public class CheckOutPage extends BasePage {

    @FindBy(id = "checkout")
    WebElement checkoutButton;

    @FindBy(name = "firstName")
    WebElement firstName;

    @FindBy(name = "lastName")
    WebElement lastName;

    @FindBy(name = "postalCode")
    WebElement postalCode;

    @FindBy(id = "continue")
    WebElement continueButton;

    @FindBy(id = "finish")
    WebElement finish;

    @FindBy(xpath = "//*[@id=\"checkout_complete_container\"]/h2")
    public WebElement confirmationMessage;

    public CheckOutPage(WebDriver driver) {
        super(driver);
    }

    @Step
    public void fillInBillingDetails(String fName, String lName, String postal) {
        setValue(firstName, fName);
        setValue(lastName, lName);
        setValue(postalCode, postal);
        continueButton.click();
    }

    @Step
    public void CompleteOrder() {
        finish.click();
    }
}