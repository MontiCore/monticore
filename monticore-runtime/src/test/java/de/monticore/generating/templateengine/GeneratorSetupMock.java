/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/ 
 */
package de.monticore.generating.templateengine;

import de.monticore.generating.GeneratorSetup;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$, $Date$
 * @since   TODO: add version number
 *
 */
public class GeneratorSetupMock extends GeneratorSetup {

  /**
   * @see de.monticore.generating.GeneratorSetup#getNewTemplateController(java.lang.String)
   */
  @Override
  public TemplateControllerMock getNewTemplateController(String templateName) {
    return new TemplateControllerMock(this, templateName);
  }
  
}
