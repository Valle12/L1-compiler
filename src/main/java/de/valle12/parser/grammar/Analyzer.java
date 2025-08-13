package de.valle12.parser.grammar;

import de.valle12.parser.grammar.table.ParsingTableCreator;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Analyzer {
  @SneakyThrows
  public static void main(String[] args) {
    LOGGER.info("Calculate first and follow sets...");
    List<String> productions = Files.readAllLines(Path.of("src/main/resources/LL1.txt"));

    FirstSetsCreator firstSetsCreator = new FirstSetsCreator(productions);
    firstSetsCreator.createFirstSets();
    LOGGER.info("First sets created successfully.");

    FollowSetsCreator followSetsCreator =
        new FollowSetsCreator(productions, firstSetsCreator.getGeneralFirstSets());
    followSetsCreator.createFollowSets();
    LOGGER.info("Follow sets created successfully.");

    ParsingTableCreator parsingTableCreator =
        new ParsingTableCreator(
            productions,
            firstSetsCreator.getFineGrainedFirstSets(),
            followSetsCreator.getFollowSets());
    parsingTableCreator.createParsingTable();
    parsingTableCreator.exportParsingTableToCsv();
    LOGGER.info("Parsing table created successfully.");
  }
}
