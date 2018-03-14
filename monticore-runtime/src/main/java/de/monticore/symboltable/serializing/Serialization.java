/* (c) https://github.com/MontiCore/monticore */

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
