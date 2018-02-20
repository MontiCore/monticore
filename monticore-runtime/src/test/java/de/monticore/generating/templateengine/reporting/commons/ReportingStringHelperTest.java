/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.commons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class ReportingStringHelperTest {
  
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
  }
}
