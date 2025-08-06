package de.valle12.lexer.regex;

public class RegexPatterns {
  public static final Regex DIGIT = new RegexCharSet(Character::isDigit, "[0-9]");
  public static final Regex LETTER = new RegexCharSet(Character::isLetter, "[A-Za-z]");
  public static final Regex UNDERSCORE = new RegexLiteral('_');
  public static final Regex ALPHA_OR_UNDERSCORE = Regex.alt(LETTER, UNDERSCORE);
  public static final Regex ALPHANUMERIC_OR_UNDERSCORE = Regex.alt(ALPHA_OR_UNDERSCORE, DIGIT);
  public static final Regex IDENTIFIER_REGEX =
      Regex.seq(ALPHA_OR_UNDERSCORE, Regex.rep(ALPHANUMERIC_OR_UNDERSCORE));
  public static final Regex DECIMAL_REGEX = Regex.seq(DIGIT, Regex.rep(DIGIT));
  // TODO need to add hexadecimal regex and DECIMAL_REGEX might need to be changed
}
