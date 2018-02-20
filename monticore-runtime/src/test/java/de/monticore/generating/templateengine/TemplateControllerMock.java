/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine;

import de.monticore.generating.GeneratorSetup;

/**
 * This class helps to test internals of {@link TemplateController}.
 *
 * @author  Pedram Nazari
 *
 */
public class TemplateControllerMock extends TemplateController {

  
  /**
   * Constructor for mc.codegen.ExtendedTemplateControllerForTesting
   * @param tcConfig
   * @param templatename
   */
  protected TemplateControllerMock(GeneratorSetup setup, String templatename) {
    super(setup, templatename);
  }

  /**
   * @return
   */
  public TemplateControllerMock getSubController() {
    return null;
  }
  
  
}
