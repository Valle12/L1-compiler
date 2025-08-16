package de.valle12.ir;

import de.valle12.lexer.tokens.IToken;
import de.valle12.lexer.tokens.Position;
import de.valle12.lexer.tokens.TokenIdentifier;
import de.valle12.lexer.tokens.TokenType;

public class TmpGenerator {
  private static final String BASE_NAME = "+t";
  private static int counter = 0;

  private TmpGenerator() {}

  public static IToken createTmp() {
    return new TokenIdentifier(TokenType.IDENTIFIER, new Position(1, 1), BASE_NAME + counter++);
  }
}
