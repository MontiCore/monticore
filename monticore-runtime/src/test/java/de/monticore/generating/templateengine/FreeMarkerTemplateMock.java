/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine;

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * Mock for FreeMarker templates (see {@link Template}).
 *
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
