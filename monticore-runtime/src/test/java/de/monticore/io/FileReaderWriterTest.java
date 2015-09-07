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

package de.monticore.io;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Test;

/**
 * @author Sebastian Oberhoff
 */
public class FileReaderWriterTest {
  
  FileReaderWriter fileHandler = new FileReaderWriter();
  
  Path testPath = Paths.get("target/test/FileHandlertest.txt");
  
  String testContent = "Hello World";
  
  @After
  public void tearDown() {
    try {
      Files.deleteIfExists(testPath);
    }
    catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }
  
  /**
   * Implicitly tests read and write in one assert.
   */
  @Test
  public void testFileHandler() {
    fileHandler.storeInFile(testPath, testContent);
    assertEquals(testContent, fileHandler.readFromFile(testPath));
  }
  
}
