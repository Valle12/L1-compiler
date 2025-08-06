package de.valle12.lexer;

import de.valle12.lexer.regex.Regex;
import de.valle12.lexer.tokens.*;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
// TODO add support for comments and line numbers
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
  private int longestMatchLength = 0;

  public void start() {
    IToken token;
    do {
      token = nextToken();
      LOGGER.info(token.toString());
    } while (token.type() != TokenType.EOF);
  }

  private IToken nextToken() {
    skipWhitespace();
    if (currentPosition >= input.length()) return new Token(TokenType.EOF, currentPosition);
    IToken bestMatch = null;
    longestMatchLength = 0;

    Map<TokenType, Regex> currentDerivatives = new EnumMap<>(regexes);

    int lookaheadPosition = currentPosition;
    StringBuilder currentMatch = new StringBuilder();

    while (lookaheadPosition < input.length()) {
      char currentChar = input.charAt(lookaheadPosition);
      Map<TokenType, Regex> nextDerivatives = new EnumMap<>(TokenType.class);
      boolean progressMade = createNewRegexes(currentDerivatives, currentChar, nextDerivatives);
      if (!progressMade) break;
      currentDerivatives = nextDerivatives;
      currentMatch.append(currentChar);
      lookaheadPosition++;

      for (Map.Entry<TokenType, Regex> entry : currentDerivatives.entrySet()) {
        Optional<IToken> optionalBestMatch =
            handleNullableRegexes(
                entry.getKey(),
                entry.getValue(),
                lookaheadPosition,
                currentMatch.toString(),
                bestMatch);
        if (optionalBestMatch.isPresent()) bestMatch = optionalBestMatch.get();
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

  private boolean createNewRegexes(
      Map<TokenType, Regex> currentDerivatives,
      char currentChar,
      Map<TokenType, Regex> nextDerivatives) {
    boolean progressMade = false;
    for (Map.Entry<TokenType, Regex> entry : currentDerivatives.entrySet()) {
      TokenType type = entry.getKey();
      Regex currentRegex = entry.getValue();
      Regex derivedRegex = currentRegex.derive(currentChar).simplify();
      if (!derivedRegex.equals(Regex.EMPTY)) {
        nextDerivatives.put(type, derivedRegex);
        progressMade = true;
      }
    }

    return progressMade;
  }

  private Optional<IToken> handleNullableRegexes(
      TokenType type, Regex regex, int lookaheadPosition, String currentMatch, IToken bestMatch) {
    if (!regex.isNullable()) return Optional.empty();
    int currentMatchLength = lookaheadPosition - currentPosition;
    if (currentMatchLength > longestMatchLength) {
      longestMatchLength = currentMatchLength;
      return Optional.of(determineToken(type, currentPosition, currentMatch));
    } else if (currentMatchLength == longestMatchLength
        && (bestMatch == null
            || regexes.keySet().stream()
                    .filter(t -> t == type || t == bestMatch.type())
                    .findFirst()
                    .orElse(null)
                == type)) {
      return Optional.of(determineToken(type, currentPosition, currentMatch));
    }

    return Optional.empty();
  }

  private IToken determineToken(TokenType type, int currentPosition, String currentMatch) {
    return switch (type) {
      case IDENTIFIER -> new TokenIdentifier(type, currentPosition, currentMatch);
      case CLASS -> new TokenClass(type, currentPosition, Integer.class);
      case DECIMAL -> new TokenDecimal(type, currentPosition, Integer.parseInt(currentMatch));
      case HEXADECIMAL ->
          new TokenDecimal(type, currentPosition, Integer.parseInt(currentMatch.substring(2), 16));
      default -> new Token(type, currentPosition);
    };
  }

  // TODO give positions in line and row
  private void skipWhitespace() {
    while (currentPosition < input.length()
        && Character.isWhitespace(input.charAt(currentPosition))) {
      currentPosition++;
    }
  }
}
