package de.valle12.lexer.regex;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RegexRep extends Regex {
  private final Regex r;

  @Override
  public Regex derive(char c) {
    return Regex.seq(r.derive(c), this);
  }

  @Override
  public boolean isNullable() {
    return true;
  }

  @Override
  public Regex simplify() {
    Regex s = r.simplify();
    if (s.equals(EMPTY) || s.equals(EPSILON)) return EPSILON;
    return new RegexRep(s);
  }

  public String toString() {
    return "(" + r + ")*";
  }
}
