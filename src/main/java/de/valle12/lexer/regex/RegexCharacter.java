package de.valle12.lexer.regex;

import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RegexCharacter implements IRegex {
  private final Object character; // TODO look for solution not involving Object

  @Override
  public IRegex derive(String character) {
    return this.character.equals(character) ? new RegexEpsilon() : new RegexEmpty();
  }

  @Override
  public boolean isNullable() {
    return false;
  }

  @Override
  public boolean match(List<Object> input) {
    return input.size() == 1 && input.getFirst().equals(character);
  }

  public String toString() {
    return character.toString();
  }
}
