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
  private int tokenPosition = 1;
  private int currentPosition = 0;
  private int longestMatchLength = 0;

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
      tokenPosition += longestMatchLength;
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

  boolean createNewRegexes(
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

  Optional<IToken> handleNullableRegexes(
      TokenType type, Regex regex, int lookaheadPosition, String currentMatch, IToken bestMatch) {
    if (!regex.isNullable()) return Optional.empty();
    int currentMatchLength = lookaheadPosition - currentPosition;
    if (currentMatchLength > longestMatchLength) {
      longestMatchLength = currentMatchLength;
      return Optional.of(determineToken(type, currentMatch));
    } else if (currentMatchLength == longestMatchLength
        && (bestMatch == null
            || regexes.keySet().stream()
                    .filter(t -> t == type || t == bestMatch.type())
                    .findFirst()
                    .orElse(null)
                == type)) {
      return Optional.of(determineToken(type, currentMatch));
    }

    return Optional.empty();
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
    int lineEndCharacters = 0;
    while (currentPosition < input.length()
        && Character.isWhitespace(input.charAt(currentPosition))) {
      if (input.charAt(currentPosition) == '\n' || input.charAt(currentPosition) == '\r') {
        if (lineEndCharacters == 0) tokenPosition = 1;
        lineEndCharacters++;
      }

      currentPosition++;
      tokenPosition++;
    }

    if (lineEndCharacters > 0) {
      tokenLine++;
      tokenPosition -= lineEndCharacters;
    }
  }

  // TODO definitely test for only \n, only \r and \r\n
  void skipLine() {
    while (currentPosition < input.length()
        && input.charAt(currentPosition) != '\n'
        && input.charAt(currentPosition) != '\r') {
      currentPosition++;
      tokenPosition++;
    }

    currentPosition++;
    tokenPosition++;

    if (currentPosition < input.length()
        && (input.charAt(currentPosition) == '\n' || input.charAt(currentPosition) == '\r')) {
      currentPosition++;
      tokenPosition++;
    }

    tokenLine++;
    currentPosition -= longestMatchLength;
    tokenPosition = 1 - longestMatchLength;
  }

  void skipLines() {
    while ((currentPosition + 1) < input.length()
        && (input.charAt(currentPosition) != '*' || input.charAt(currentPosition + 1) != '/')) {
      char current = input.charAt(currentPosition);
      char next = input.charAt(currentPosition + 1);

      if (current == '\r') {
        if (next == '\n') {
          currentPosition++;
          tokenPosition++;
        }
        tokenLine++;
        tokenPosition = 1 - longestMatchLength;
      } else if (current == '\n') {
        tokenLine++;
        tokenPosition = 1 - longestMatchLength;
      }

      currentPosition++;
      tokenPosition++;
    }

    currentPosition -= longestMatchLength;
    tokenPosition--; // Maybe look at better way of counting positions
  }
}
