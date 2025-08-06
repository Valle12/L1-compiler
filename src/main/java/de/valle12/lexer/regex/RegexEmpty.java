package de.valle12.lexer.regex;

import java.util.List;

public class RegexEmpty implements IRegex {
  @Override
  public IRegex derive(String character) {
    return this;
  }

  @Override
  public boolean isNullable() {
    return false;
  }

  @Override
  public boolean match(List<Object> input) {
    return false;
  }

  public String toString() {
    return "∅";
  }
}
