/* (c) https://github.com/MontiCore/monticore */
package de.monticore.generating;

import de.monticore.generating.templateengine.ExtendedTemplateController;
import de.monticore.generating.templateengine.TemplateController;

public class ExtendedGeneratorSetup extends GeneratorSetup {

  /**
   * @see de.monticore.generating.GeneratorSetup#getNewTemplateController(java.lang.String)
   */
  @Override
  public TemplateController getNewTemplateController(String templateName) {
    return new ExtendedTemplateController(this, templateName);
  }
  
}
