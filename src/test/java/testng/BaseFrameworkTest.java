package testng;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;
import utils.TestParameters;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Comparator;

public class BaseFrameworkTest {
    protected static final Logger LOGGER = LoggerFactory.getLogger("Test");
    protected String testName;

    protected static String resultToString(int result) {
        switch (result) {
            case ITestResult.SUCCESS:
            case ITestResult.SUCCESS_PERCENTAGE_FAILURE:
                return "PASS";
            case ITestResult.FAILURE:
                return "FAIL";
            case ITestResult.SKIP:
                return "SKIP";
            case ITestResult.CREATED:
            case ITestResult.STARTED:
            default:
                return "UNKNOWN";
        }
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    @AfterMethod
    public void afterTest(ITestResult result, Method method) {
    }

    @BeforeSuite
    public void setupThreadCount(ITestContext context) {
        XmlTest test = context.getCurrentXmlTest();
        XmlSuite suite = test.getSuite();

        int threadsParam = Integer.parseInt(TestParameters.getParameter("threads", "0"));
        int suiteCount = suite.getThreadCount();
        int threadCount;

        if (threadsParam > 0) {
            // If a command line Test Parameter is specified, override everything
            threadCount = threadsParam;
        } else {
            // Use the suite default, or the TestNG command line default.
            threadCount = suiteCount;
        }

        // Update thread count for normal tests and data provider tests.
        suite.setThreadCount(threadCount);
        suite.setDataProviderThreadCount(threadCount);

        // Workaround for IntelliJ apparently not setting Test thread count to the same as the suite.
        if (suite.getName().equals("Default Suite")) {
            test.setThreadCount(threadCount);
        }

        // Update thread count for invocation count tests without an overridden thread pool size.
        for (ITestNGMethod method : context.getAllTestMethods()) {
            if (method.getInvocationCount() > 1 && method.getThreadPoolSize() == 0) {
                method.setThreadPoolSize(threadCount);
            }
        }
    }

    @BeforeSuite
    public void exportCleanup(ITestContext context) throws Exception {
        try {
            Files.walk(new File("target/export").toPath())
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (NoSuchFileException e) {
        }
    }

    @BeforeSuite
    public void setupAPIs(ITestContext context) {
        String proxyString = System.getProperty("proxy");
        if (proxyString != null) {
            String host;
            int port;
            switch (proxyString) {
                case "zap":
                    break;
                case "":
                    host = "127.0.0.1";
                    port = 8888;
                    RestAssured.proxy(host, port);
                    break;
                default:
                    String[] parts = proxyString.split(":");
                    host = parts[0];
                    port = Integer.parseInt(parts[1]);
                    RestAssured.proxy(host, port);
            }
        }
    }

    @BeforeMethod
    public void beforeTest(Method method) {
        setTestName(method.getName());
    }

    public void skip(String reason) {
        throw new SkipException(reason, null);
    }

    /**
     * Skip a test if there are no earlier failures.
     *
     * @param reason
     * @param softAssert
     */
    @Step("Skip Test")
    public void skip(String reason, SoftAssert softAssert) {
        // First check if anything failed up to this point.
        if (softAssert != null) {
            softAssert.assertAll();
        }
        throw new SkipException(reason);
    }
}
