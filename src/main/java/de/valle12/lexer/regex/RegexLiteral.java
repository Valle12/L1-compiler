package de.valle12.lexer.regex;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RegexLiteral extends Regex {
  private final char character;

  @Override
  public Regex derive(char c) {
    return (c == character) ? EPSILON : EMPTY;
  }

  @Override
  public boolean isNullable() {
    return false;
  }

  public String toString() {
    return String.valueOf(character);
  }
}
