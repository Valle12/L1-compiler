package de.valle12.lexer.regex;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RegexAlt extends Regex {
  private final Regex left;
  private final Regex right;

  @Override
  public Regex derive(char c) {
    return Regex.alt(left.derive(c), right.derive(c));
  }

  @Override
  public boolean isNullable() {
    return left.isNullable() || right.isNullable();
  }

  @Override
  public Regex simplify() {
    Regex s1 = left.simplify();
    Regex s2 = right.simplify();

    if (s1.equals(EMPTY)) return s2;
    if (s2.equals(EMPTY)) return s1;
    if (s1.equals(s2)) return s1;

    if (s1.toString().compareTo(s2.toString()) > 0) return new RegexAlt(s2, s1);
    return new RegexAlt(s1, s2);
  }

  public String toString() {
    return "(" + left + " | " + right + ")";
  }
}
