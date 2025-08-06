package de.valle12.lexer.regex;

import java.util.List;

public interface IRegex {
  IRegex derive(String character);

  boolean isNullable();

  boolean match(List<Object> input);
}
