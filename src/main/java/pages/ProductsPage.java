package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class ProductsPage extends BasePage {
    @FindBy(css = ".inventory_item button")
    WebElement addToCart;

    @FindBy(id = "shopping_cart_container")
    WebElement updateCartButton;


    public ProductsPage(WebDriver driver) {
        super(driver);
    }

    @Step
    public void addItemsToCart() {
        List<WebElement> items = driver.findElements((By) addToCart);
        for (WebElement item : items) {
            item.click();
        }
    }

    public void goToCart() {
        driver.findElement((By) updateCartButton).click();
    }
}
