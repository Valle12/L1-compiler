package de.valle12.lexer.regex;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class RegexEpsilon extends Regex {
  @Override
  public Regex derive(char c) {
    return EMPTY;
  }

  @Override
  public boolean isNullable() {
    return true;
  }

  public String toString() {
    return "ε";
  }
}
