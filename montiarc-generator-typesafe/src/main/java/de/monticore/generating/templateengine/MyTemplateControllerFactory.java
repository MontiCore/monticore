/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.generating.templateengine;

import de.monticore.generating.templateengine.ITemplateControllerFactory;
import de.monticore.generating.templateengine.TemplateControllerConfiguration;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 */
public class MyTemplateControllerFactory implements ITemplateControllerFactory{

  /**
   * @see de.monticore.generating.templateengine.ITemplateControllerFactory#create(de.monticore.generating.templateengine.TemplateControllerConfiguration, java.lang.String)
   */
  @Override
  public MyTemplateController create(TemplateControllerConfiguration config, String templateName) {
    return new MyTemplateController(config, templateName);  
  }
  
}
