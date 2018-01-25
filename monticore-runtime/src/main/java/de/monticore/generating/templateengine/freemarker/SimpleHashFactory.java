/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
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
