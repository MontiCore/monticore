/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarc.generator.codegen;

import java.util.List;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.TemplateController;
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
public class MyTemplateController extends TemplateController{

  /**
   * Constructor for de.montiarc.generator.codegen.MyTemplateController
   * @param tcConfig
   * @param templatename
   */
  public MyTemplateController(TemplateControllerConfiguration tcConfig, String templatename) {
    super(tcConfig, templatename);
  }
  
  /**
   * @see de.monticore.generating.templateengine.TemplateController#processTemplate(java.lang.String, de.monticore.ast.ASTNode, java.util.List)
   */
  @Override
  protected String processTemplate(String templateName, ASTNode astNode,
      List<Object> passedArguments) {
    return super.processTemplate(templateName, astNode, passedArguments);
  }
  
  
  
}
