package de.monticore.generating.templateengine.reporting.commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StatisticsHandlerTest {
  @Test
  public void validSHash() {
    String content = "SomeString with many characters? + ?+._ü1^^";
    String SHASH = StatisticsHandler.getSHASH(content);

    assertTrue(StatisticsHandler.isValidSHASH(SHASH, content));
  }
  @Test
  public void invalidSHash() {
    String content = "SomeString with many characters? + ?+._ü1^^";
    String SHASH = StatisticsHandler.getSHASH(content);

    String differentContent = "AnotjherString";
    assertFalse(StatisticsHandler.isValidSHASH(SHASH, differentContent));
  }

}