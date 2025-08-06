package de.valle12.lexer.regex;

import java.util.List;

public class RegexEpsilon implements IRegex {
  @Override
  public IRegex derive(String character) {
    return new RegexEmpty();
  }

  @Override
  public boolean isNullable() {
    return true;
  }

  @Override
  public boolean match(List<Object> input) {
    return input.isEmpty();
  }

  public String toString() {
    return "ε";
  }
}
