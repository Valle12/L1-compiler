package de.valle12.lexer;

import de.valle12.lexer.regex.Regex;
import de.valle12.lexer.tokens.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Getter
@Setter
public class Lexer {
  private static final int STANDARD_TOKEN_POSITION = 1;
  private static final int STANDARD_LONGEST_MATCH_LENGTH = 0;
  private final String input;
  private final Map<TokenType, Regex> regexes =
      Arrays.stream(TokenType.values())
          .collect(
              Collectors.toMap(
                  Function.identity(),
                  TokenType::getR,
                  (oldValue, newValue) -> oldValue,
                  () -> new EnumMap<>(TokenType.class)));
  private int tokenLine = 1;
  private int tokenPosition = STANDARD_TOKEN_POSITION;
  private int currentPosition = 0;
  private int longestMatchLength = STANDARD_LONGEST_MATCH_LENGTH;

  public List<IToken> start() {
    List<IToken> tokens = new ArrayList<>();
    IToken token;
    do {
      token = nextToken();
      tokens.add(token);
    } while (token.type() != TokenType.EOF);

    return tokens;
  }

  IToken nextToken() {
    skipWhitespace();
    if (currentPosition >= input.length())
      return new Token(TokenType.EOF, new Position(tokenLine, tokenPosition));
    IToken bestMatch = null;
    longestMatchLength = STANDARD_LONGEST_MATCH_LENGTH;
    Map<TokenType, Regex> currentDerivatives = new EnumMap<>(regexes);
    int lookaheadPosition = currentPosition;
    StringBuilder currentMatch = new StringBuilder();

    while (lookaheadPosition < input.length()) {
      char currentChar = input.charAt(lookaheadPosition);
      Map<TokenType, Regex> nextDerivatives = deriveRegexes(currentDerivatives, currentChar);
      if (nextDerivatives.isEmpty()) break;
      currentDerivatives = nextDerivatives;
      currentMatch.append(currentChar);
      lookaheadPosition++;
      Optional<IToken> optionalBestMatch =
          handleNullableRegex(currentDerivatives, currentMatch.toString());
      if (optionalBestMatch.isPresent()) bestMatch = optionalBestMatch.get();
    }

    if (bestMatch != null) {
      if (bestMatch.type() != TokenType.SINGLE_LINE_COMMENT
          && bestMatch.type() != TokenType.MULTI_LINE_COMMENT_BEGIN) {
        currentPosition += longestMatchLength;
        tokenPosition += longestMatchLength;
      }

      return bestMatch;
    }

    IToken errorToken =
        new TokenError(
            TokenType.UNKNOWN,
            new Position(tokenLine, tokenPosition),
            input.charAt(currentPosition));
    currentPosition++;
    tokenPosition++;
    return errorToken;
  }

  Map<TokenType, Regex> deriveRegexes(Map<TokenType, Regex> currentDerivatives, char currentChar) {
    Map<TokenType, Regex> nextDerivatives = new EnumMap<>(TokenType.class);

    for (Map.Entry<TokenType, Regex> entry : currentDerivatives.entrySet()) {
      TokenType type = entry.getKey();
      Regex regex = entry.getValue();
      Regex derivedRegex = regex.derive(currentChar).simplify();
      if (derivedRegex.equals(Regex.EMPTY)) continue;
      nextDerivatives.put(type, derivedRegex);
    }

    return nextDerivatives;
  }

  Optional<IToken> handleNullableRegex(
      Map<TokenType, Regex> currentDerivatives, String currentMatch) {
    IToken bestMatch = null;

    for (Map.Entry<TokenType, Regex> entry : currentDerivatives.entrySet()) {
      TokenType type = entry.getKey();
      Regex regex = entry.getValue();
      if (!regex.isNullable()) continue;
      int currentMatchLength = currentMatch.length();
      if (currentMatchLength > longestMatchLength) {
        longestMatchLength = currentMatchLength;
        bestMatch = determineToken(type, currentMatch);
      } else {
        IToken finalBestMatch = bestMatch;
        if (currentMatchLength == longestMatchLength
            && (bestMatch == null
                || regexes.keySet().stream()
                        .filter(t -> ((t == type) || (t == finalBestMatch.type())))
                        .findFirst()
                        .orElse(null)
                    == type)) {
          bestMatch = determineToken(type, currentMatch);
        }
      }
    }

    return Optional.ofNullable(bestMatch);
  }

  IToken determineToken(TokenType type, String currentMatch) {
    return switch (type) {
      case IDENTIFIER ->
          new TokenIdentifier(type, new Position(tokenLine, tokenPosition), currentMatch);
      case CLASS -> new TokenClass(type, new Position(tokenLine, tokenPosition), Integer.class);
      case DECIMAL ->
          new TokenDecimal(
              type, new Position(tokenLine, tokenPosition), Integer.parseInt(currentMatch));
      case HEXADECIMAL ->
          new TokenDecimal(
              type,
              new Position(tokenLine, tokenPosition),
              Integer.parseInt(currentMatch.substring(2), 16));
      case SINGLE_LINE_COMMENT -> {
        int currentTokenLine = tokenLine;
        int currentTokenPosition = tokenPosition;
        skipLine();
        yield new Token(type, new Position(currentTokenLine, currentTokenPosition));
      }
      case MULTI_LINE_COMMENT_BEGIN -> {
        int currentTokenLine = tokenLine;
        int currentTokenPosition = tokenPosition;
        skipLines();
        yield new Token(type, new Position(currentTokenLine, currentTokenPosition));
      }
      default -> new Token(type, new Position(tokenLine, tokenPosition));
    };
  }

  void skipWhitespace() {
    char currentChar;
    while (currentPosition < input.length()
        && Character.isWhitespace(currentChar = input.charAt(currentPosition))) {
      if (currentChar == '\r') {
        if (currentPosition + 1 < input.length() && input.charAt(currentPosition + 1) == '\n') {
          currentPosition++;
        }

        tokenLine++;
        tokenPosition = STANDARD_TOKEN_POSITION;
      } else if (currentChar == '\n') {
        tokenLine++;
        tokenPosition = STANDARD_TOKEN_POSITION;
      } else {
        tokenPosition++;
      }

      currentPosition++;
    }
  }

  void skipLine() {
    char currentChar = '0';
    while (currentPosition < input.length()
        && (currentChar = input.charAt(currentPosition)) != '\n'
        && currentChar != '\r') {
      currentPosition++;
    }

    if (currentChar == '\r') {
      if (currentPosition + 1 < input.length() && input.charAt(currentPosition + 1) == '\n') {
        currentPosition++;
      }

      tokenLine++;
      tokenPosition = STANDARD_TOKEN_POSITION;
    } else if (currentChar == '\n') {
      tokenLine++;
      tokenPosition = STANDARD_TOKEN_POSITION;
    }

    currentPosition++;
  }

  void skipLines() {
    while (currentPosition + 1 < input.length()
        && (input.charAt(currentPosition) != '*' || input.charAt(currentPosition + 1) != '/')) {
      char current = input.charAt(currentPosition);
      char next = input.charAt(currentPosition + 1);
      if (current == '\r') {
        if (next == '\n') currentPosition++;
        tokenLine++;
        tokenPosition = STANDARD_TOKEN_POSITION;
      } else if (current == '\n') {
        tokenLine++;
        tokenPosition = STANDARD_TOKEN_POSITION;
      } else {
        tokenPosition++;
      }

      currentPosition++;
    }
  }
}
