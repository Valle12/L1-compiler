package de.valle12.lexer.regex;

import java.util.function.Predicate;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RegexCharSet extends Regex {
  private final Predicate<Character> charPredicate;
  private final String description;

  @Override
  public Regex derive(char c) {
    return charPredicate.test(c) ? EPSILON : EMPTY;
  }

  @Override
  public boolean isNullable() {
    return false;
  }

  public String toString() {
    return description;
  }
}
