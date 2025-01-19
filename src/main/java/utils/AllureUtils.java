package utils;

import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import io.qameta.allure.util.ResultsUtils;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static io.qameta.allure.Allure.step;

public class AllureUtils {
    /**
     * Adds a Jira issue key to the Allure report.
     *
     * @param issueKey
     */
    public static void addIssue(String issueKey) {
        AtomicLong count = new AtomicLong();
        Allure.getLifecycle().updateTestCase(testResult -> {
            count.set(
                    Optional.ofNullable(testResult.getLinks())
                            .orElse(Collections.emptyList())
                            .stream().filter(link -> issueKey.equals(link.getName()))
                            .count()
            );
            // Don't add the same link more than once.
            if (count.get() == 0) {
                testResult.getLinks().add(ResultsUtils.createIssueLink(issueKey));
            }
        });
        step("Issue: " + issueKey + (count.get() > 0 ? " (duplicate)" : ""), Status.FAILED);
    }
}