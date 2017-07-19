/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2017, MontiCore, All rights reserved.
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

package de.monticore.symboltable.serializing;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import de.monticore.generating.templateengine.reporting.Reporting;

/**
 * @author  Pedram Mir Seyed Nazari
 *
 */
public class Serialization {
  
  public static Object deserialize(String fileName) throws IOException, ClassNotFoundException {
      FileInputStream fis = new FileInputStream(fileName);
      Reporting.reportOpenInputFile(fileName);
      ObjectInputStream ois = new ObjectInputStream(fis);
      Object obj = ois.readObject();
      fis.close();
      ois.close();
      return obj;
  }

  public static void serialize(Object object, String fileName) throws IOException {
      FileOutputStream fos = new FileOutputStream(fileName);
      Reporting.reportFileCreation(fileName);
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      oos.writeObject(object);
      fos.close();
      oos.close();
  }

}
