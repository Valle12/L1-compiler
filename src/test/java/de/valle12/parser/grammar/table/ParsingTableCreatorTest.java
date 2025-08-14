package de.valle12.parser.grammar.table;

import de.valle12.parser.grammar.FirstSetsCreator;
import de.valle12.parser.grammar.FollowSetsCreator;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.SneakyThrows;
import org.approvaltests.Approvals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ParsingTableCreatorTest {
  @Test
  @DisplayName("Test createParsingTable with valid input")
  @SneakyThrows
  void test1() {
    List<String> productions = Files.readAllLines(Path.of("src/main/resources/LL1.txt"));
    FirstSetsCreator firstSetsCreator = new FirstSetsCreator(productions);
    firstSetsCreator.createFirstSets();
    FollowSetsCreator followSetsCreator =
        new FollowSetsCreator(productions, firstSetsCreator.getGeneralFirstSets());
    followSetsCreator.createFollowSets();
    ParsingTableCreator parsingTableCreator =
        new ParsingTableCreator(
            productions,
            firstSetsCreator.getFineGrainedFirstSets(),
            followSetsCreator.getFollowSets());

    parsingTableCreator.createParsingTable();

    Approvals.verify(parsingTableCreator.getParsingTable());
  }
}
