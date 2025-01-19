package testng;

import io.qameta.allure.Allure;
import io.qameta.allure.model.Parameter;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StepResult;
import io.qameta.allure.util.ResultsUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.asserts.IAssert;
import pages.BaseSeleniumPage;

import java.util.*;

import static org.testng.internal.EclipseInterface.*;
import static utils.StringUtils.exceptionToString;

public class SoftAssert extends org.testng.asserts.SoftAssert {
    private final WebDriver driver;

    public SoftAssert() {
        // API soft assertions won't have web drivers.
        this(null);
    }

    public SoftAssert(WebDriver driver) {
        this.driver = driver;
    }

    static String formatNotContains(Object actual, Object expected, String message) {
        String formatted = "";
        if (null != message) {
            formatted = message + " ";
        }

        return formatted + ASSERT_EQUAL_LEFT + expected + CLOSING_CHARACTER + " to be in " + OPENING_CHARACTER + actual + ASSERT_RIGHT;
    }

    @Override
    public void onBeforeAssert(IAssert<?> iAssert) {
        boolean isNot = false;
        boolean isFail = false;

        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        for (StackTraceElement ste : stElements) {
            // Check if the Assert is negative.
            if (ste.getMethodName().startsWith("assert")) {
                String[] methodName = ste.getMethodName().split("(?=[A-Z])");
                isNot = methodName.length > 2 && methodName[1].equals("Not");
                break;
            } else if (ste.getMethodName().equals("fail")) {
                isFail = true;
                break;
            }
        }

        final String uuid = UUID.randomUUID().toString();
        final String message = iAssert.getMessage();

        String name = (isFail ? "[Fail]" : "[Assert]") + (message == null ? "" : " " + message);

        StepResult result = new StepResult().setName(name);
        if (!isFail) {
            result.setParameters(getAssertParameters(iAssert, isNot));
        }
        Allure.getLifecycle().startStep(uuid, result);
        super.onBeforeAssert(iAssert);
    }

    @Override
    public void onAssertFailure(IAssert<?> iAssert, AssertionError assertionError) {
        super.onAssertFailure(iAssert, assertionError);
        if (driver != null) {
            new BaseSeleniumPage(driver).takeScreenshot("Assertion");
        }

        // Log the original exception when using SoftAssert.fail()
        Throwable cause = assertionError.getCause();
        if (cause != null) {
            Allure.attachment("Original Exception", exceptionToString(cause));
        }

        // Log the SoftAssert exception.
        Allure.attachment("Assertion Exception", exceptionToString(assertionError));

        // Update the current step status.
        Allure.getLifecycle().updateStep(s -> s
                .setStatus(ResultsUtils.getStatus(assertionError).orElse(Status.BROKEN))
                .setStatusDetails(ResultsUtils.getStatusDetails(assertionError).orElse(null)));
        Allure.getLifecycle().stopStep();
    }

    @Override
    public void onAssertSuccess(IAssert<?> iAssert) {
        super.onAssertSuccess(iAssert);
        Allure.getLifecycle().updateStep(s -> s.setStatus(Status.PASSED));
        Allure.getLifecycle().stopStep();
    }

    private List<Parameter> getAssertParameters(IAssert iAssert, boolean isNot) {
        List<Parameter> list = new ArrayList<>();

        Object actual = iAssert.getActual();
        String actualValue;
        if (actual != null && actual.getClass().isArray()) {
            actualValue = Arrays.toString((Object[]) actual);
        } else {
            actualValue = String.valueOf(actual);
        }

        Object expected = iAssert.getExpected();
        String expectedValue;
        if (expected != null && expected.getClass().isArray()) {
            expectedValue = Arrays.toString((Object[]) expected);
        } else {
            expectedValue = String.valueOf(expected);
        }

        String actualLength = "";
        String expectedLength = "";
        if (expected instanceof String) {
            actualLength = " (" + actualValue.length() + ")";
            expectedLength = " (" + expectedValue.length() + ")";
        } else if (expected instanceof Collection) {
            actualLength = " (" + ((Collection) actual).size() + ")";
            expectedLength = " (" + ((Collection) expected).size() + ")";
        }

        list.add(new Parameter()
                .setName("Actual" + actualLength)
                .setValue(actualValue)
        );

        list.add(new Parameter()
                .setName((isNot ? "Expected (Not)" : "Expected") + expectedLength)
                .setValue(expectedValue)
        );

        return list;
    }

    public void assertContains(final String actual, final String expected, final String message) {
        doAssert(new SimpleAssert<String>(actual, expected, message) {
            @Override
            public void doAssert() {
                Allure.getLifecycle().updateStep(stepResult -> {
                    stepResult.getParameters().stream()
                            .filter(p -> p.getName().equals("Expected")).findFirst()
                            .ifPresent(p -> p.setName(p.getName() + " (Partial)"));
                });

                if ((expected == null) && (actual == null)) {
                    return;
                }
                if (expected == null ^ actual == null) {
                    failNotContains(actual, expected, message);
                }
                if (actual.contains(expected)) {
                    return;
                }
                failNotContains(actual, expected, message);
            }
        });
    }

    private void failNotContains(Object actual, Object expected, String message) {
        fail(formatNotContains(actual, expected, message));
    }

    /**
     * Asserts whether a WebElement is visible.
     *
     * @param selector    The selector to find the element.
     * @param visible     Whether the element is supposed to be visible.
     * @param elementName The element name used as assertion message.
     */
    public void assertWebElementVisible(By selector, Boolean visible, String elementName) {
        try {
            if (driver == null) {
                // If this is a non-UI SoftAssert.
                String formatted = "Cannot check visibility in non-UI SoftAssert";
                fail(formatted);
            } else {
                WebElement element = driver.findElement(selector);
                assertWebElementVisible(element, visible, elementName);
            }
        } catch (NoSuchElementException e) {
            // The element was not found but was supposed to be visible.
            if (visible) {
                String formatted = "Element not visible: " + selector.toString();
                fail(formatted);
            }
        }
    }

    /**
     * Asserts whether a WebElement is visible.
     *
     * @param element     The proxy or already found element.
     * @param visible     Whether the element is supposed to be visible.
     * @param elementName The element name used as assertion message.
     */
    public void assertWebElementVisible(WebElement element, Boolean visible, String elementName) {
        boolean actual = false;
        try {
            actual = element.isDisplayed();
        } catch (NoSuchElementException e) {
            // Element not found.
        }

        final Boolean condition = actual;
        final String message = elementName + (visible ? "" : " not") + " visible";
        doAssert(new SimpleAssert<Boolean>(condition, visible, message) {
            @Override
            public void doAssert() {
                Assert.assertEquals(condition, visible, message);
            }
        });
    }

    /**
     * Asserts whether a Collection is empty.
     *
     * @param actual
     * @param message
     * @param <T>
     */
    public <T> void assertEmpty(final Collection<T> actual, final String message) {
        assertContainsOnly(actual, Collections.emptyList(), message);
    }

    /**
     * Order-insensitive full Collection comparison.
     *
     * @param actual
     * @param expected
     * @param message
     * @param <T>
     */
    public <T> void assertContainsOnly(final Collection<T> actual, final Collection<T> expected, final String message) {
        doAssert(new SimpleAssert<Collection<T>>(actual, expected, message) {
            @Override
            public void doAssert() {
                if ((expected == null) && (actual == null)) {
                    return;
                } else if (expected == null ^ actual == null) {
                    failNotContainsOnly(actual, expected, true, message);
                } else if (actual.containsAll(expected) && expected.containsAll(actual)) {
                    return;
                }
                failNotContainsOnly(actual, expected, true, message);
            }
        });
    }

    /**
     * Order-insensitive partial Collection comparison.
     *
     * @param actual
     * @param expected
     * @param message
     * @param <T>
     */
    public <T> void assertContains(final Collection<T> actual, final Collection<T> expected, final String message) {
        doAssert(new SimpleAssert<Collection<T>>(actual, expected, message) {
            @Override
            public void doAssert() {
                if ((expected == null) && (actual == null)) {
                    return;
                } else if (expected == null ^ actual == null) {
                    failNotContainsOnly(actual, expected, false, message);
                } else if (actual.containsAll(expected)) {
                    return;
                }
                failNotContainsOnly(actual, expected, false, message);
            }
        });
    }

    private <T> void failNotContainsOnly(Collection<T> actual, Collection<T> expected, boolean checkAll, String message) {
        String formatted = "";
        if (null != message) {
            formatted = message + " ";
        }

        Collection<T> expectedNotInActual = new ArrayList<>(expected);
        expectedNotInActual.removeAll(actual);
        Collection<T> actualNotInExpected = new ArrayList<>(actual);
        actualNotInExpected.removeAll(expected);

        Allure.getLifecycle().updateStep(stepResult -> {
            stepResult.getParameters().add(new Parameter()
                    .setName("Missing (" + expectedNotInActual.size() + ")")
                    .setValue(expectedNotInActual.toString())
            );
            if (checkAll) {
                stepResult.getParameters().add(new Parameter()
                        .setName("Unexpected (" + actualNotInExpected.size() + ")")
                        .setValue(actualNotInExpected.toString())
                );
            }
        });

        if (expectedNotInActual.size() > 0) {
            if (expectedNotInActual.size() > 5) {
                formatted += "expected " + expectedNotInActual.size() + " items";
            } else {
                formatted += "expected " + expectedNotInActual;
            }
            if (checkAll && actualNotInExpected.size() > 0) {
                formatted += " and ";
            }
        }
        if (checkAll && actualNotInExpected.size() > 0) {
            if (actualNotInExpected.size() > 5) {
                formatted += "did not expect " + actualNotInExpected.size() + " items";
            } else {
                formatted += "did not expect " + actualNotInExpected;
            }
        }

        fail(formatted);
    }

    abstract private static class SimpleAssert<T> implements IAssert<T> {
        private final T actual;
        private final T expected;
        private final String m_message;

        public SimpleAssert(String message) {
            this(null, null, message);
        }

        public SimpleAssert(T actual, T expected) {
            this(actual, expected, null);
        }

        public SimpleAssert(T actual, T expected, String message) {
            this.actual = actual;
            this.expected = expected;
            m_message = message;
        }

        @Override
        public String getMessage() {
            return m_message;
        }

        @Override
        public T getActual() {
            return actual;
        }

        @Override
        public T getExpected() {
            return expected;
        }

        @Override
        abstract public void doAssert();
    }
}
