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

package de.monticore.io.paths;

import de.monticore.AmbiguityException;
import org.junit.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ModelPathTest {
  
  ModelPath modelPath;
  
  Path parentPathOne = Paths.get("src/test/resources/modelpathtest/path1");
  
  Path parentPathTwo = Paths.get("src/test/resources/modelpathtest/path2");
  
  ModelCoordinate ambiguousModel = ModelCoordinates.createQualifiedCoordinate(Paths
      .get("ambiguousfile.txt"));
  
  ModelCoordinate unambiguousModel = ModelCoordinates.createQualifiedCoordinate(Paths
      .get("unambiguousfile.txt"));
  
  Path unambiguousModelLocation = parentPathOne.resolve("unambiguousfile.txt");
  
  public ModelPathTest() {
    modelPath = new ModelPath(parentPathOne, parentPathTwo);
  }
  
  @Test
  public void testResolveModel() throws URISyntaxException {
    assertTrue(modelPath.resolveModel(unambiguousModel).hasLocation());
    assertTrue(unambiguousModel.getLocation().endsWith(unambiguousModelLocation));
  }
  
  @Test(expected = AmbiguityException.class)
  public void testAmbiguityException() {
    modelPath.resolveModel(ambiguousModel);
  }
}
