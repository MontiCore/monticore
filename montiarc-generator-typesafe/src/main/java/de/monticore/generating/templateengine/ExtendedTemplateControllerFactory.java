/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.generating.templateengine;

import de.monticore.generating.templateengine.ITemplateControllerFactory;
import de.monticore.generating.templateengine.TemplateControllerConfiguration;

/**
 * 
 * @author  Jerome Pfeiffer
 *
 */
public class ExtendedTemplateControllerFactory implements ITemplateControllerFactory{

  /**
   * @see de.monticore.generating.templateengine.ITemplateControllerFactory#create(de.monticore.generating.templateengine.TemplateControllerConfiguration, java.lang.String)
   */
  @Override
  public ExtendedTemplateController create(TemplateControllerConfiguration config, String templateName) {
    return new ExtendedTemplateController(config, templateName);  
  }
  
}
