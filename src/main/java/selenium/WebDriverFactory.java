package selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import java.net.MalformedURLException;
import java.net.URL;

import static io.qameta.allure.Allure.step;

public class WebDriverFactory {
    private static final String REMOTEHUB_URL = "";

    public static WebDriver createWebDriver() {
        String driverName = System.getProperty("driver", "");

        if (driverName.equals("")) {
            throw new IllegalArgumentException("Web driver must be specified with -Ddriver=");
        }

        RemoteWebDriver driver;
        switch (driverName) {
            case "firefox":
                driver = createFirefoxDriver();
                break;
            case "chrome":
                WebDriverManager.chromedriver().clearDriverCache().setup();
                WebDriverManager.chromedriver().clearResolutionCache().setup();
                driver = createChromeDriver();
                break;
            case "edge":
                driver = createEdgeDriver();
                break;
            case "safari":
                driver = createSafariDriver();
                break;
            case "remote_chrome":
                driver = createRemoteChromeDriver();
                break;
            case "headless_chrome":
                driver = createHeadlessChromeDriver();
                break;
            case "remote_firefox":
                driver = createRemoteFirefoxDriver();
                break;
            case "remote_edge":
                driver = createRemoteEdgeDriver();
                break;
            default:
                throw new IllegalArgumentException("Unsupported web driver: " + driverName);
        }
        driver.manage().window().maximize();

        step("Web Driver Capabilities", (step) -> {
            driver.getCapabilities().asMap().forEach(step::parameter);
        });
        return driver;
    }

    protected static FirefoxDriver createFirefoxDriver() {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();
        setupProxy(options);
        return new FirefoxDriver(options);
    }

    protected static ChromeDriver createChromeDriver() {
        ChromeOptions options = new ChromeOptions();
        setupProxy(options);
        return new ChromeDriver(options);
    }

    protected static EdgeDriver createEdgeDriver() {
        WebDriverManager.edgedriver().setup();
        EdgeOptions options = new EdgeOptions();
        setupProxy(options);
        return new EdgeDriver(options);
    }

    protected static SafariDriver createSafariDriver() {
        WebDriverManager.safaridriver().setup();
        SafariOptions options = new SafariOptions();
        options.setAutomaticInspection(false);
        return new SafariDriver(options);
    }

    protected static ChromeDriver createHeadlessChromeDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        return new ChromeDriver(options);
    }

    protected static RemoteWebDriver createRemoteFirefoxDriver() {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setBrowserName("Firefox");
        setupProxy(capabilities);
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.merge(capabilities);
        try {
            return new RemoteWebDriver(new URL(REMOTEHUB_URL), firefoxOptions);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    protected static RemoteWebDriver createRemoteChromeDriver() {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setBrowserName("Chrome");
        setupProxy(capabilities);
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--disable-dev-shm-usage");
        chromeOptions.addArguments("--start-maximized");
        chromeOptions.addArguments("--disable-infobars");
        chromeOptions.addArguments("--disable-extensions");
        chromeOptions.addArguments("--disable-notifications");
        chromeOptions.merge(capabilities);
        try {
            return new RemoteWebDriver(new URL(REMOTEHUB_URL), chromeOptions);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    protected static RemoteWebDriver createRemoteEdgeDriver() {
        EdgeOptions options = new EdgeOptions();
        try {
            return new RemoteWebDriver(new URL(REMOTEHUB_URL), options);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    /**
     * Adds proxy settings to web driver options.
     * Command line usage:
     * (no value)              No proxy
     * -Dproxy                 Default proxy
     * -Dproxy=127.0.0.1:1234  Use the specified proxy
     *
     * @param options The Options to get proxy settings.
     * @return
     */
    protected static void setupProxy(MutableCapabilities options) {
        String proxyString = System.getProperty("proxy");
        if (proxyString != null) {
            Proxy proxy = new Proxy();
            if ("".equals(proxyString)) {// Default proxy
                proxy.setHttpProxy("127.0.0.1:8888");
            } else {// Custom proxy
                proxy.setHttpProxy(proxyString);
                proxy.setSslProxy(proxyString);
            }
            options.setCapability("proxy", proxy);
        }
    }

    protected static void setupCapabilities(MutableCapabilities options) {
        options.setCapability("browserName", "Chrome");
        options.setCapability("browserVersion", "latest");
    }
}