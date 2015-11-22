package test.utils;

public class MalformedProjectionTest {
    private final String stories =
        "Feature: Malformed examples example\n"
          + "Scenario Outline: Malformed examples\n"
          + "Given A\n"
          + "When B is <foo>\n"
          + "Then C\n"
          + "\n"
          + "Examples:\n"
          + "| foo\n"
          + "baa\n"
          + "boo";
}