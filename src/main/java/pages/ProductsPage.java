package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class ProductsPage extends BasePage {
    private final By addToCartButtons = By.cssSelector(".inventory_item button");
    private final By cartIcon = By.id("shopping_cart_container");

    public ProductsPage(WebDriver driver) {
        super(driver);
    }

    @Step
    public void addItemsToCart() {
        List<WebElement> items = driver.findElements(addToCartButtons);
        for (WebElement item : items) {
            item.click();
        }
    }

    public void goToCart() {
        driver.findElement(cartIcon).click();
    }
}
