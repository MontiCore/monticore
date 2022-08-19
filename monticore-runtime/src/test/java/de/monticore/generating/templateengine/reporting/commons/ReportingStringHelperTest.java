/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.commons;

import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import de.se_rwth.commons.logging.Log;

public class ReportingStringHelperTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testReportingStringHelper() {
    assertFalse(ReportingHelper
        .formatStringToReportingString(
            "{this.height                  \n\t= builder.getHeight();this.width = builder.getWidth();addAllPhotoMessages(builder.getPhotoMessages());addAllTags(builder.getTags());}",
            60).contains("\t"));
  
    assertEquals(60, ReportingHelper
        .formatStringToReportingString(
            "{this.height                  \n\t= builder.getHeight();this.width = builder.getWidth();addAllPhotoMessages(builder.getPhotoMessages());addAllTags(builder.getTags());}",
            60).length());
    
    assertEquals(6, ReportingHelper
        .formatStringToReportingString(
            "abcd",
            60).length());
    
    assertEquals(7, ReportingHelper
        .formatStringToReportingString(
            "{this.height                  \n\t= builder.getHeight();this.width = builder.getWidth();addAllPhotoMessages(builder.getPhotoMessages());addAllTags(builder.getTags());}",
            5).length());
    assertTrue(Log.getFindings().isEmpty());
  }
}
