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

package de.monticore.generating.templateengine;

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * Mock for FreeMarker templates (see {@link Template}).
 *
 * @author (last commit) $Author$
 */
public class FreeMarkerTemplateMock extends Template {
  
  private Object data;
  
  private boolean isProcessed = false;
  
  public static FreeMarkerTemplateMock of(String name) {
    try {
      return new FreeMarkerTemplateMock(name);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  /**
   * Constructor for de.monticore.generating.templateengine.controller.FreeMarkerTemplateMock
   * 
   * @param name
   * @throws IOException
   */
  FreeMarkerTemplateMock(String name) throws IOException {
    super(name, new StringReader(""), new Configuration());
  }
  
  public boolean isProcessed() {
    return isProcessed;
  }
  
  /**
   * @see freemarker.template.Template#process(java.lang.Object, java.io.Writer)
   */
  @Override
  public void process(Object rootMap, Writer out) {
    isProcessed = true;
    
    this.data = rootMap;
  }
  
  /** 
   * @return The data passed to the template. Should usually be SimpleHash
   */
  public Object getData() {
    return this.data;
  }
  
}
