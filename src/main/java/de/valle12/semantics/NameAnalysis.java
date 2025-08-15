package de.valle12.semantics;

import de.valle12.lexer.tokens.TokenIdentifier;
import de.valle12.parser.node.Node;
import de.valle12.parser.node.NodeDecl;
import de.valle12.parser.node.NodeTerminal;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Getter
public class NameAnalysis {
  private final Node ast;
  private final Set<String> identifiers = new HashSet<>();

  public boolean start() {
    return recursiveDecent(ast, false);
  }

  private boolean recursiveDecent(Node node, boolean hasError) {
    if (node instanceof NodeTerminal terminal
        && terminal.getToken() instanceof TokenIdentifier token
        && !identifiers.contains(token.identifier())) {
      LOGGER.error(
          "Variable \"{}\" at {} used before declaration.", token.identifier(), token.position());
      return true;
    }

    for (Node child : node.getChildren()) {
      if (node instanceof NodeDecl
          && child instanceof NodeTerminal terminal
          && terminal.getToken() instanceof TokenIdentifier token) {
        String identifier = token.identifier();
        if (identifiers.contains(identifier)) {
          LOGGER.error("Variable \"{}\" at {} already declared.", identifier, token.position());
          hasError = true;
          continue;
        }

        identifiers.add(token.identifier());
      } else {
        hasError |= recursiveDecent(child, hasError);
      }
    }

    return hasError;
  }
}
