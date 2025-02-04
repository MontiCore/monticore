/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine;

import de.monticore.generating.GeneratorSetup;

/**
 * This class helps to test internals of {@link TemplateController}.
 *
 */
public class TemplateControllerMock extends TemplateController {

  
  /**
   * Constructor for mc.codegen.ExtendedTemplateControllerForTesting
   * @param setup
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
