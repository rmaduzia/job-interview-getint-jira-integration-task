package io.getint.recruitment_task;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Config {

    private static final Properties properties = new Properties();
    private static final Pattern pattern = Pattern.compile("\\$\\{([^}]+)\\}");

    static {
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new IOException("Unable to find application.properties");
            }
            properties.load(input);

            resolvePlaceholders();


        } catch (IOException ioException) {
            ioException.printStackTrace();
            throw new ExceptionInInitializerError(ioException);
        }
    }

    private static void resolvePlaceholders() {
        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            properties.setProperty(key, resolveSystemVariables(value));
        }
    }

    private static String resolveSystemVariables(String value) {
        Matcher matcher = pattern.matcher(value);
        StringBuilder buffer = new StringBuilder();
        while (matcher.find()) {
            String varName = matcher.group(1);
            String varValue = System.getenv(varName);
            if (varValue == null) {
                varValue = System.getProperty(varName, matcher.group(0));
            }
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(varValue));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }

}
