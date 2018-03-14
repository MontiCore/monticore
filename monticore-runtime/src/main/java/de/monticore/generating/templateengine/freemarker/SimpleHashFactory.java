/* (c) https://github.com/MontiCore/monticore */

/**
 * 
 */
package de.monticore.generating.templateengine.freemarker;

import java.util.Map;

import freemarker.log.Logger;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;

/**
 * Use this factory to instantiate SimpleHash objects.
 * 
 * @author Arne Haber
 * @date 10.02.2010
 * 
 */
// STATE SMELL PN 
public class SimpleHashFactory {
  
  private static SimpleHashFactory theInstance;
  
  private SimpleHashFactory() {
    // use empty logger to suppress default free marker log behaviour
    System.setProperty(Logger.SYSTEM_PROPERTY_NAME_LOGGER_LIBRARY, Logger.LIBRARY_NAME_NONE);
  }
  
  public static SimpleHashFactory getInstance() {
    if (theInstance == null) {
      synchronized (SimpleHashFactory.class) {
        theInstance = new SimpleHashFactory();
      }
    }
    return theInstance;
  }
  
  public SimpleHash createSimpleHash() {
    return new SimpleHash();
  }
  
  public SimpleHash createSimpleHash(Map<?, ?> map) {
    return new SimpleHash(map);
  }
  
  public SimpleHash createSimpleHash(ObjectWrapper wrapper) {
    return new SimpleHash(wrapper);
  }
  
  public SimpleHash createSimpleHash(Map<?, ?> map, ObjectWrapper wrapper) {
    return new SimpleHash(map, wrapper);
  }
  
}
