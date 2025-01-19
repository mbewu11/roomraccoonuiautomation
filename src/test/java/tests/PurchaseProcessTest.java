package tests;

import org.testng.annotations.Test;
import pages.*;
import testng.SoftAssert;

public class PurchaseProcessTest extends BaseTest {
    @Test
    public void PurchaseProcessTest() throws Exception {
        SoftAssert softAssert = new SoftAssert();
        new LoginPage(getDriver()).UserLogin("standard_user", "secret_sauce");
        // Checkout items
        new ProductsPage(getDriver()).addItemsToCart();
        new ProductsPage(getDriver()).goToCart();
        new CheckOutPage(getDriver()).fillInBillingDetails("Okuhle","Mbewu", "7100");
        new CheckOutPage(getDriver()).CompleteOrder();

        softAssert.assertEquals(new CheckOutPage(getDriver()).confirmationMessage.getText(),"Thank you for your order!","Confirmation validation");
    }
}
