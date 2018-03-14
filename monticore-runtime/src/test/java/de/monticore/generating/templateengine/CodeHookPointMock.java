/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine;

import java.util.List;

import de.monticore.generating.templateengine.CodeHookPoint;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.ast.ASTNode;

/**
 * Mock for {@link CodeHookPoint}
 *
 * @author  (last commit) $Author$
 *          $Date$
 *
 */
public class CodeHookPointMock extends CodeHookPoint {
  
  private String returnValue;

  /**
   * Constructor for mc.codegen.CodeHookPointMock
   * @param returnValue
   */
  CodeHookPointMock(String returnValue) {
    super();
    this.returnValue = returnValue;
  }

  /**
   * @see mc.codegen.CodeHookPoint#processValue(mc.codegen.TemplateController, de.monticore.ast.ASTNode)
   */
  @Override
  public String processValue(TemplateController controller, ASTNode ast) {
    return returnValue;
  }

  @Override
  public String processValue(TemplateController controller, List<Object> args) {
    return returnValue;
  }

  /**
   * @see de.monticore.generating.templateengine.HookPoint#processValue(de.monticore.generating.templateengine.TemplateController, de.monticore.ast.ASTNode, java.util.List)
   */
  @Override
  public String processValue(TemplateController controller, ASTNode node, List<Object> args) {
    return returnValue;
  }
  
}
