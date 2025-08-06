package de.valle12;

import de.valle12.lexer.regex.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
  public static void main(String[] args) {
    IRegex foo = new RegexCharacter("foo");
    IRegex bar = new RegexCharacter("bar");
    IRegex baz = new RegexCharacter("baz");

    IRegex result = foo.derive("f");
    LOGGER.info(result.toString());

    IRegex regex = new RegexRep(new RegexAlt(new RegexCharacter("ab"), new RegexCharacter("c")));
    IRegex a = regex.derive("a");
    IRegex b = regex.derive("b");
    IRegex c = regex.derive("c");
    LOGGER.info("a: " + a);
    LOGGER.info("b: " + b);
    LOGGER.info("c: " + c);
  }
}
