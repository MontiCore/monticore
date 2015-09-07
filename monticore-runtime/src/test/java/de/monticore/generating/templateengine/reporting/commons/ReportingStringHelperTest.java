/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.generating.templateengine.reporting.commons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import de.monticore.generating.templateengine.reporting.artifacts.ReportingNameHelper;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
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
