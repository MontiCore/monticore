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

package de.monticore.generating.templateengine;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.monticore.generating.templateengine.ObjectFactory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ObjectFactoryTest {
  
  @Test
  public void testInstanciationWithDefaultConstructor() {
    Object obj = ObjectFactory.createObject("java.lang.String");
    assertNotNull(obj);
    assertEquals(String.class, obj.getClass());
  }
  
  @Test
  public void testInstanciationWithParams() {
    List<Object> params = new ArrayList<Object>();
    params.add("myContent");
    Object obj = ObjectFactory.createObject("java.lang.String", params);
    assertNotNull(obj);
    assertEquals(String.class, obj.getClass());
    assertEquals(obj, "myContent");
  }
  
  @Test
  public void testInstanciationWithTypesAndParams() {
    List<Class<?>> paramTypes = new ArrayList<Class<?>>();
    paramTypes.add(char[].class);
    paramTypes.add(Integer.TYPE);
    paramTypes.add(Integer.TYPE);
    
    List<Object> params = new ArrayList<Object>();
    params.add("Say yes!".toCharArray());
    params.add(4);
    params.add(3);
    
    Object obj = ObjectFactory.createObject("java.lang.String", paramTypes, params);
    assertNotNull(obj);
    assertEquals(obj.getClass(), (new String()).getClass());
    assertEquals(obj, "yes");
  }
  
}
