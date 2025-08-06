package de.valle12.lexer.regex;

import java.util.List;

public class Regex {
  private Regex() {}

  public static boolean match(IRegex regex, List<Object> input) {
    if (input.isEmpty()) return regex.isNullable();
    String firstChar = input.getFirst().toString();
    List<Object> rest = input.subList(1, input.size());
    return match(regex.derive(firstChar), rest);
  }
}
