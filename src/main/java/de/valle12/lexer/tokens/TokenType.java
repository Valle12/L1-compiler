package de.valle12.lexer.tokens;

import de.valle12.lexer.regex.Regex;
import de.valle12.lexer.regex.RegexPatterns;
import lombok.Getter;

@Getter
public enum TokenType {
  RETURN(Regex.literal("return")),
  SEMICOLON(Regex.literal(';')),
  CLASS(Regex.literal("int")),
  EQUALS(Regex.literal('=')),
  LEFT_PARENTHESIS(Regex.literal('(')),
  RIGHT_PARENTHESIS(Regex.literal(')')),
  PLUS(Regex.literal('+')),
  MINUS(Regex.literal('-')),
  STAR(Regex.literal('*')),
  SLASH(Regex.literal('/')),
  PERCENT(Regex.literal('%')),
  PLUS_EQUALS(Regex.literal("+=")),
  MINUS_EQUALS(Regex.literal("-=")),
  STAR_EQUALS(Regex.literal("*=")),
  SLASH_EQUALS(Regex.literal("/=")),
  PERCENT_EQUALS(Regex.literal("%=")),
  MAIN(Regex.literal("main")),
  IDENTIFIER(RegexPatterns.IDENTIFIER_REGEX),
  DECIMAL(RegexPatterns.DECIMAL_REGEX),
  HEXADECIMAL(RegexPatterns.HEXADECIMAL_REGEX),
  LEFT_BRACE(Regex.literal('{')),
  RIGHT_BRACE(Regex.literal('}')),
  UNKNOWN(Regex.EMPTY),
  SINGLE_LINE_COMMENT(Regex.literal("//")),
  MULTI_LINE_COMMENT_BEGIN(Regex.literal("/*")),
  MULTI_LINE_COMMENT_END(Regex.literal("*/")),
  EPSILON(Regex.EPSILON),
  EOF(Regex.EMPTY);

  private final Regex r;

  TokenType(Regex r) {
    this.r = r;
  }
}
