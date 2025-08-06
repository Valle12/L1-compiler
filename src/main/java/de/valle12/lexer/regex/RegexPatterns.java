package de.valle12.lexer.regex;

public class RegexPatterns {
  public static final Regex DIGIT = new RegexCharSet(Character::isDigit, "[0-9]");
  public static final Regex DIGITS_ONE_TO_NINE =
      new RegexCharSet(RegexPatterns::getDigitsOneToNine, "[1-9]");
  public static final Regex LETTER = new RegexCharSet(Character::isLetter, "[A-Za-z]");
  public static final Regex X = new RegexCharSet(RegexPatterns::getX, "[xX]");
  public static final Regex UNDERSCORE = new RegexLiteral('_');
  public static final Regex ALPHA_OR_UNDERSCORE = Regex.alt(LETTER, UNDERSCORE);
  public static final Regex ALPHANUMERIC_OR_UNDERSCORE = Regex.alt(ALPHA_OR_UNDERSCORE, DIGIT);
  public static final Regex IDENTIFIER_REGEX =
      Regex.seq(ALPHA_OR_UNDERSCORE, Regex.rep(ALPHANUMERIC_OR_UNDERSCORE));
  public static final Regex HEX_DIGIT =
      new RegexCharSet(RegexPatterns::getHexDigits, "[0-9a-fA-F]");
  public static final Regex DECIMAL_REGEX =
      Regex.alt(Regex.literal('0'), Regex.seq(DIGITS_ONE_TO_NINE, Regex.rep(DIGIT)));
  public static final Regex HEXADECIMAL_REGEX =
      Regex.seq(Regex.seq(Regex.seq(Regex.literal('0'), X), HEX_DIGIT), Regex.rep(HEX_DIGIT));

  private RegexPatterns() {}

  public static boolean getDigitsOneToNine(char c) {
    return c >= '1' && c <= '9';
  }

  public static boolean getX(char c) {
    return c == 'x' || c == 'X';
  }

  public static boolean getHexDigits(char c) {
    return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
  }
}
