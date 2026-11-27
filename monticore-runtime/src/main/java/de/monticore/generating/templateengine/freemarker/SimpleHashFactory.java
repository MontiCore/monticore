/* (c) https://github.com/MontiCore/monticore */

/**
 *
 */
package de.monticore.generating.templateengine.freemarker;

import de.monticore.generating.GeneratorSetup;
import freemarker.log.Logger;
import freemarker.template.DefaultObjectWrapper;
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
  protected DefaultObjectWrapper defaultObjectWrapper;

  protected SimpleHashFactory() {
    theInstance = this;
    // use empty logger to suppress default free marker log behaviour
    System.setProperty(Logger.SYSTEM_PROPERTY_NAME_LOGGER_LIBRARY, Logger.LIBRARY_NAME_NONE);

    // Share the DefaultObjectWrapper (as it references the beans(available methods of a class)
    DefaultObjectWrapperBuilder defaultObjectWrapperBuilder = new DefaultObjectWrapperBuilder(
            GeneratorSetup.FREEMARKER_VERSION);
    defaultObjectWrapperBuilder.setUseModelCache(true);
    this.defaultObjectWrapper = defaultObjectWrapperBuilder.build();
  }
  
  public static SimpleHashFactory getInstance() {
    if (theInstance == null) {
      new SimpleHashFactory();
    }
    return theInstance;
  }
  
  public SimpleHash createSimpleHash() {
    return new SimpleHash(defaultObjectWrapper);
  }
  
  public SimpleHash createSimpleHash(Map<?, ?> map) {
    return new SimpleHash(map, defaultObjectWrapper);
  }

  @Deprecated
  public SimpleHash createSimpleHash(ObjectWrapper wrapper) {
    return new SimpleHash(wrapper);
  }

  @Deprecated
  public SimpleHash createSimpleHash(Map<?, ?> map, ObjectWrapper wrapper) {
    return new SimpleHash(map, wrapper);
  }
  
}
