package de.monticore.gradle;

import org.junit.Test;

import static org.junit.Assert.*;

public class StatisticsHandlerFixTest {
  @Test
  public void validSHash() {
    String content = "SomeString with many characters? + ?+._ü1^^";
    String SHASH = StatisticsHandlerFix.getSHASH(content);

    assertTrue(StatisticsHandlerFix.isValidSHASH(SHASH, content));
  }
  @Test
  public void invalidSHash() {
    String content = "SomeString with many characters? + ?+._ü1^^";
    String SHASH = StatisticsHandlerFix.getSHASH(content);

    String differentContent = "AnotjherString";
    assertFalse(StatisticsHandlerFix.isValidSHASH(SHASH, differentContent));
  }

}