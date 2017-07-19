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

package de.monticore.generating.templateengine.reporting.reporter;

import java.io.File;

import de.monticore.generating.templateengine.reporting.artifacts.ArtifactReporter;
import de.monticore.generating.templateengine.reporting.artifacts.formatter.GMLFormatter;
import de.monticore.generating.templateengine.reporting.commons.ReportingConstants;


/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 *          $Date$
 * @since   TODO: add version number
 *
 */
public class ArtifactGmlReporter extends ArtifactReporter {
  
  final static String SIMPLE_FILE_NAME = "15_ArtifactGml";  
  
  public ArtifactGmlReporter(String outputDir, String modelName) {
    super(outputDir + File.separator + ReportingConstants.REPORTING_DIR + File.separator + modelName,
        SIMPLE_FILE_NAME, "gml", new GMLFormatter());
  }  
}
