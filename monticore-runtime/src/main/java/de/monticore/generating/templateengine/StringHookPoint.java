/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine;

import de.monticore.ast.ASTNode;

import java.util.List;

/**
 * Represents a String hook point.
 * 
 * The string will be copied as result of the hook without further change.
 *
 */
public class StringHookPoint extends HookPoint {

  private final String value;

  public StringHookPoint(String value) {
    super();
    this.value = value;
  }
  
  @Override
  public String processValue(TemplateController controller, ASTNode ast) {
    return value;
  }

  @Override
  public String processValue(TemplateController controller, List<Object> args) {
    return value;
  }
  
  /**
   * @see de.monticore.generating.templateengine.HookPoint#processValue(de.monticore.generating.templateengine.TemplateController, de.monticore.ast.ASTNode, java.util.List)
   */
  @Override
  public String processValue(TemplateController controller, ASTNode node, List<Object> args) {
    return value;
  }
  
  public String getValue() {
	  return value;
  }

 
  
}
