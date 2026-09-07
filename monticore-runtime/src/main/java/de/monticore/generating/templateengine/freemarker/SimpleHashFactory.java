/* (c) https://github.com/MontiCore/monticore */

/**
 *
 */
package de.monticore.generating.templateengine.freemarker;

import de.monticore.generating.GeneratorSetup;
import freemarker.log.Logger;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;

import java.util.Map;

/**
 * Use this factory to instantiate SimpleHash objects.
 *
 */
// STATE SMELL PN
public class SimpleHashFactory {

  private static SimpleHashFactory theInstance;

  protected SimpleHashFactory() {
    theInstance = this;
    // use empty logger to suppress default free marker log behaviour
    System.setProperty(Logger.SYSTEM_PROPERTY_NAME_LOGGER_LIBRARY, Logger.LIBRARY_NAME_NONE);
  }
  
  public static SimpleHashFactory getInstance() {
    if (theInstance == null) {
      new SimpleHashFactory();
    }
    return theInstance;
  }
  
  public SimpleHash createSimpleHash() {
    return new SimpleHash(new DefaultObjectWrapperBuilder(GeneratorSetup.FREEMARKER_VERSION).build());
  }
  
  public SimpleHash createSimpleHash(Map<?, ?> map) {
    return new SimpleHash(map, new DefaultObjectWrapperBuilder(GeneratorSetup.FREEMARKER_VERSION).build());
  }
  
  public SimpleHash createSimpleHash(ObjectWrapper wrapper) {
    return new SimpleHash(wrapper);
  }
  
  public SimpleHash createSimpleHash(Map<?, ?> map, ObjectWrapper wrapper) {
    return new SimpleHash(map, wrapper);
  }
  
}
