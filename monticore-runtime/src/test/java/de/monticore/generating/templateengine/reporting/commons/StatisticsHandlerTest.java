package de.monticore.generating.templateengine.reporting.commons;

import de.monticore.generating.templateengine.reporting.commons.StatisticsHandler;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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