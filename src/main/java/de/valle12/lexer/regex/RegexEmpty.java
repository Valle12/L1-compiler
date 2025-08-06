package de.valle12.lexer.regex;

public class RegexEmpty extends Regex {
  @Override
  public Regex derive(char c) {
    return this;
  }

  @Override
  public boolean isNullable() {
    return false;
  }

  public String toString() {
    return "∅";
  }
}
