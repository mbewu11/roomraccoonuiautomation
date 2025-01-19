package tests;

import org.testng.annotations.Test;
import pages.LoginPage;

public class LoginTest extends BaseTest {
    @Test
    public void LoginTest() {
        new LoginPage(getDriver()).UserLogin("standard_user", "secret_sauce");
    }
}
