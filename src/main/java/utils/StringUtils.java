package utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StringUtils {
    /**
     * Converts an exception and stacktrace to a string.
     *
     * @param e
     * @return
     */
    public static String exceptionToString(Throwable e) {
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);
        return writer.toString();
    }
}
