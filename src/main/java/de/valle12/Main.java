package de.valle12;

import de.valle12.lexer.Lexer;
import de.valle12.lexer.regex.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
  public static void main(String[] args) {
    LOGGER.info("Starting to extract tokens...");
    Lexer lexer = new Lexer();
    lexer.start();
  }
}
