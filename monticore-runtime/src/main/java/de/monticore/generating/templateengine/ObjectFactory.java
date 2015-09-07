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

package de.monticore.generating.templateengine;

import de.se_rwth.commons.logging.Log;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory to instantiate any object from Templates 
 * using op.instantiate(classname)
 * 
 * @author Martin Schindler
 */
public class ObjectFactory {
  
  protected static ObjectFactory factory = null;
  protected ClassLoader classLoader;
  
  protected ObjectFactory() {
    classLoader = getClass().getClassLoader();
  }
  
  public static Object createObject(String qualifiedName) {
    return createObject(qualifiedName, new ArrayList<>(), new ArrayList<>());
  }
  
  public static Object createObject(String qualifiedName, List<Object> params) {
    return createObject(qualifiedName, getTypes(params), params);
  }
  
  public static Object createObject(String qualifiedName,  List<Class<?>> paramTypes, List<Object> params) {
    if (factory == null) {
      factory = new ObjectFactory();
    }
    return factory.doCreateObject(qualifiedName, paramTypes, params);
  }
  
  protected Object doCreateObject(String qualifiedName, List<Class<?>> paramTypes, List<Object> params) {
    Class<?> referencedClass = null;
    
    // try to call constructor directly
    try {
      referencedClass = classLoader.loadClass(qualifiedName);
      return referencedClass
          .getConstructor(paramTypes.toArray(new Class<?>[] {}))
          .newInstance(params.toArray(new Object[] {}));
    }
    catch (ClassNotFoundException e) {
      Log.error("0xA0118 Template-execution: Could not find Class " + qualifiedName);
    }
    catch (Exception e) {
      // types of direct constructor-call are not identical
      // try to fix it by searching for a matching constructor (using supertypes of the given params)
      if (referencedClass != null) {
        for (Constructor<?> constr : referencedClass.getConstructors()) {
          if (constr.getParameterTypes().length == paramTypes.size()) {
            for (int i = 0; i < paramTypes.size(); i++) {
              if (!constr.getParameterTypes()[i].isAssignableFrom(paramTypes.get(i))) {
                continue;
              }
              try {
                return constr.newInstance(params.toArray(new Object[] {}));
              }
              catch (Exception e2) {
                Log.error("0xA0119 Template-execution: Could not instanciate Class " +
                    qualifiedName + " with parameters " + paramTypes);
              }
            }
          }
        }
      }
    }
    return null;
  }
  
  protected static List<Class<?>> getTypes(List<Object> params) {
    if (factory == null) {
      factory = new ObjectFactory();
    }
    return factory.doGetTypes(params);
  }
  
  protected List<Class<?>> doGetTypes(List<Object> params) {
    List<Class<?>> paramTypes = new ArrayList<>();
    for (Object obj : params) {
      paramTypes.add(obj.getClass());
    }
    return paramTypes;
  }
  
  static void setClassLoader(ClassLoader loader) {
    if (loader == null) {
      return;
    }
    if (factory == null) {
      factory = new ObjectFactory();
    }
    factory.doSetClassLoader(loader);
  }
  
  protected void doSetClassLoader(ClassLoader loader) {
    this.classLoader = loader;
  }
  
}
