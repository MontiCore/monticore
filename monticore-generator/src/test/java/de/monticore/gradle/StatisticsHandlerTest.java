package de.monticore.gradle;

import org.junit.Test;

import static org.junit.Assert.*;

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