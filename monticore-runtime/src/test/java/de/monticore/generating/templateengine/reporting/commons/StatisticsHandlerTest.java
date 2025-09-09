package de.monticore.generating.templateengine.reporting.commons;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StatisticsHandlerTest {
  @Test
  public void validSHash() {
    String content = "SomeString with many characters? + ?+._ü1^^";
    String SHASH = StatisticsHandler.getSHASH(content);

    Assertions.assertTrue(StatisticsHandler.isValidSHASH(SHASH, content));
  }
  @Test
  public void invalidSHash() {
    String content = "SomeString with many characters? + ?+._ü1^^";
    String SHASH = StatisticsHandler.getSHASH(content);

    String differentContent = "AnotjherString";
    Assertions.assertFalse(StatisticsHandler.isValidSHASH(SHASH, differentContent));
  }

}