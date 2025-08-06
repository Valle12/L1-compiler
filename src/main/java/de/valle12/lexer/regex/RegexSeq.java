package de.valle12.lexer.regex;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RegexSeq extends Regex {
  private final Regex first;
  private final Regex second;

  @Override
  public Regex derive(char c) {
    if (first.isNullable()) return Regex.alt(Regex.seq(first.derive(c), second), second.derive(c));
    return Regex.seq(first.derive(c), second);
  }

  @Override
  public boolean isNullable() {
    return first.isNullable() && second.isNullable();
  }

  @Override
  public Regex simplify() {
    Regex s1 = first.simplify();
    Regex s2 = second.simplify();

    if (s1.equals(EMPTY) || s2.equals(EMPTY)) return EMPTY;
    if (s1.equals(EPSILON)) return s2;
    if (s2.equals(EPSILON)) return s1;

    return new RegexSeq(s1, s2);
  }

  public String toString() {
    return "(" + first + second + ")";
  }
}
