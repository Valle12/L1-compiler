package de.valle12.lexer;

import de.valle12.lexer.regex.Regex;
import de.valle12.lexer.tokens.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Lexer {
  private final String input;
  private final Map<TokenType, Regex> regexes =
      Arrays.stream(TokenType.values())
          .collect(
              Collectors.toMap(
                  Function.identity(),
                  TokenType::getR,
                  (oldValue, newValue) -> oldValue,
                  () -> new EnumMap<>(TokenType.class)));
  private int currentPosition = 0;

  @SneakyThrows
  public Lexer() {
    this.input =
        Files.readString(Path.of(getClass().getClassLoader().getResource("test.l1").toURI()));
  }

  public void start() {
    LOGGER.info("Lexing input: \"{}\"", input);
    IToken token;
    do {
      token = nextToken();
      LOGGER.info(token.toString());
    } while (token.type() != TokenType.EOF);
  }

  // TODO split method
  private IToken nextToken() {
    skipWhitespace();
    if (currentPosition >= input.length()) return new Token(TokenType.EOF, currentPosition);
    IToken bestMatch = null;
    int longestMatchLength = 0;

    Map<TokenType, Regex> currentDerivatives = new EnumMap<>(regexes);

    int lookaheadPosition = currentPosition;
    String currentMatch = "";

    while (lookaheadPosition < input.length()) {
      char currentChar = input.charAt(lookaheadPosition);
      boolean progressMade = false;

      Map<TokenType, Regex> nextDerivatives = new EnumMap<>(TokenType.class);
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
          bestMatch =
              switch (type) {
                case IDENTIFIER -> new TokenIdentifier(type, currentPosition, currentMatch);
                case CLASS -> new TokenClass(type, currentPosition, Integer.class);
                case DECIMAL ->
                    new TokenDecimal(type, currentPosition, Integer.parseInt(currentMatch));
                case HEXADECIMAL ->
                    new TokenDecimal(
                        type, currentPosition, Integer.parseInt(currentMatch.substring(2), 16));
                default -> new Token(type, currentPosition);
              };
        } else if (currentMatchLength == longestMatchLength) {
          IToken finalBestMatch = bestMatch;
          if (bestMatch == null
              || regexes.keySet().stream()
                      .filter(t -> t == type || t == finalBestMatch.type())
                      .findFirst()
                      .orElse(null)
                  == type) {
            bestMatch =
                switch (type) {
                  case IDENTIFIER -> new TokenIdentifier(type, currentPosition, currentMatch);
                  case CLASS -> new TokenClass(type, currentPosition, Integer.class);
                  case DECIMAL, HEXADECIMAL ->
                      new TokenDecimal(type, currentPosition, Integer.parseInt(currentMatch));
                  default -> new Token(type, currentPosition);
                };
          }
        }
      }
    }

    if (bestMatch != null) {
      currentPosition += longestMatchLength;
      return bestMatch;
    }

    IToken errorToken =
        new TokenError(TokenType.UNKNOWN, currentPosition, input.charAt(currentPosition));
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
