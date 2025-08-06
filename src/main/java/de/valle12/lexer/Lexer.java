package de.valle12.lexer;

import de.valle12.lexer.regex.Regex;
import de.valle12.lexer.regex.RegexPatterns;
import de.valle12.lexer.tokens.Token;
import de.valle12.lexer.tokens.TokenType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Lexer {
  private final String input;
  private final Map<TokenType, Regex> regexes = new LinkedHashMap<>();
  private int currentPosition = 0;

  @SneakyThrows
  public Lexer() {
    this.input =
        Files.readString(Path.of(getClass().getClassLoader().getResource("test.l1").toURI()));
    this.currentPosition = 0;
    initializeRegexes();
  }

  // TODO embed those into the tokentype itself
  private void initializeRegexes() {
    regexes.put(TokenType.RETURN, Regex.literal("return"));
    regexes.put(TokenType.SEMICOLON, Regex.literal(';'));
    regexes.put(TokenType.CLASS, Regex.literal("int"));
    regexes.put(TokenType.EQUALS, Regex.literal('='));
    regexes.put(TokenType.LEFT_PARENTHESIS, Regex.literal('('));
    regexes.put(TokenType.RIGHT_PARENTHESIS, Regex.literal(')'));
    regexes.put(TokenType.PLUS, Regex.literal('+'));
    regexes.put(TokenType.MINUS, Regex.literal('-'));
    regexes.put(TokenType.STAR, Regex.literal('*'));
    regexes.put(TokenType.SLASH, Regex.literal('/'));
    regexes.put(TokenType.PERCENT, Regex.literal('%'));
    regexes.put(TokenType.PLUS_EQUALS, Regex.literal("+="));
    regexes.put(TokenType.MINUS_EQUALS, Regex.literal("-="));
    regexes.put(TokenType.STAR_EQUALS, Regex.literal("*="));
    regexes.put(TokenType.SLASH_EQUALS, Regex.literal("/="));
    regexes.put(TokenType.PERCENT_EQUALS, Regex.literal("%="));
    regexes.put(TokenType.IDENTIFIER, RegexPatterns.IDENTIFIER_REGEX);
    regexes.put(TokenType.DECIMAL, RegexPatterns.DECIMAL_REGEX);
    // TODO skip for now regexes.put(TokenType.HEXADECIMAL, )
    regexes.put(TokenType.LEFT_BRACE, Regex.literal('{'));
    regexes.put(TokenType.RIGHT_BRACE, Regex.literal('}'));
    regexes.put(TokenType.SINGLE_LINE_COMMENT, Regex.literal("//"));
    regexes.put(TokenType.MULTI_LINE_COMMENT_BEGIN, Regex.literal("/*"));
    regexes.put(TokenType.MULTI_LINE_COMMENT_END, Regex.literal("*/"));
  }

  public void start() {
    LOGGER.info("Lexing input: \"{}\"", input);
    Token token;
    do {
      token = nextToken();
      LOGGER.info(token.toString());
    } while (token.type() != TokenType.EOF);
  }

  // TODO split method
  private Token nextToken() {
    skipWhitespace();
    if (currentPosition >= input.length()) return new Token(TokenType.EOF, "", currentPosition);
    Token bestMatch = null;
    int longestMatchLength = 0;

    Map<TokenType, Regex> currentDerivatives = new EnumMap<>(regexes);

    int lookaheadPosition = currentPosition;
    String currentMatch = "";

    while (lookaheadPosition < input.length()) {
      char currentChar = input.charAt(lookaheadPosition);
      boolean progressMade = false;

      Map<TokenType, Regex> nextDerivatives = new HashMap<>();
      for (Map.Entry<TokenType, Regex> entry : currentDerivatives.entrySet()) {
        TokenType type = entry.getKey();
        Regex currentRegex = entry.getValue();
        Regex derivedRegex = currentRegex.derive(currentChar).simplify();
        if (!derivedRegex.equals(Regex.EMPTY)) {
          nextDerivatives.put(type, derivedRegex);
          progressMade = true;
        }
      }

      if (!progressMade) break;
      currentDerivatives = nextDerivatives;
      currentMatch += currentChar;
      lookaheadPosition++;

      for (Map.Entry<TokenType, Regex> entry : currentDerivatives.entrySet()) {
        TokenType type = entry.getKey();
        Regex derivedRegex = entry.getValue();
        if (!derivedRegex.isNullable()) continue;
        int currentMatchLength = lookaheadPosition - currentPosition;
        if (currentMatchLength > longestMatchLength) {
          longestMatchLength = currentMatchLength;
          bestMatch = new Token(type, currentMatch, currentPosition);
        } else if (currentMatchLength == longestMatchLength) {
          Token finalBestMatch = bestMatch;
          if (bestMatch == null
              || regexes.keySet().stream()
                      .filter(t -> t == type || t == finalBestMatch.type())
                      .findFirst()
                      .orElse(null)
                  == type) {
            bestMatch = new Token(type, currentMatch, currentPosition);
          }
        }
      }
    }

    if (bestMatch != null) {
      currentPosition += longestMatchLength;
      return bestMatch;
    }

    String errorChar = String.valueOf(input.charAt(currentPosition));
    Token errorToken = new Token(TokenType.UNKNOWN, errorChar, currentPosition);
    currentPosition++;
    return errorToken;
  }

  // TODO give positions in line and row
  private void skipWhitespace() {
    while (currentPosition < input.length()
        && Character.isWhitespace(input.charAt(currentPosition))) {
      currentPosition++;
    }
  }
}
