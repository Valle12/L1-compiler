package de.valle12.lexer.regex;

public abstract class Regex {
  public static final Regex EMPTY = new RegexEmpty();
  public static final Regex EPSILON = new RegexEpsilon();

  public static Regex alt(Regex left, Regex right) {
    return new RegexAlt(left, right).simplify();
  }

  public static Regex seq(Regex first, Regex second) {
    return new RegexSeq(first, second).simplify();
  }

  public static Regex rep(Regex r) {
    return new RegexRep(r).simplify();
  }

  public static Regex literal(char c) {
    return new RegexLiteral(c);
  }

  public static Regex literal(String s) {
    if (s.isEmpty()) return EPSILON; // TODO maybe new epsilon
    Regex result = new RegexLiteral(s.charAt(0));
    for (int i = 1; i < s.length(); i++) {
      result = seq(result, new RegexLiteral(s.charAt(i)));
    }

    return result;
  }

  public abstract Regex derive(char c);

  public abstract boolean isNullable();

  public Regex simplify() {
    return this;
  }
}
