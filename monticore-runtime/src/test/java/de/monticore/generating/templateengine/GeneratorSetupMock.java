/* (c) https://github.com/MontiCore/monticore */
package de.monticore.generating.templateengine;

import de.monticore.generating.GeneratorSetup;

public class GeneratorSetupMock extends GeneratorSetup {

  /**
   * @see de.monticore.generating.GeneratorSetup#getNewTemplateController(java.lang.String)
   */
  @Override
  public TemplateControllerMock getNewTemplateController(String templateName) {
    return new TemplateControllerMock(this, templateName);
  }
  
}
