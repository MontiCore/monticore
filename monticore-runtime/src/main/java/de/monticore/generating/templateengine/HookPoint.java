/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine;

import java.util.List;

import de.monticore.ast.ASTNode;

/**
 * Represents a hook point in templates
 *
 */
public abstract class HookPoint {
  
  public abstract
  String processValue(TemplateController controller,
         	      ASTNode ast);
  
  public abstract
  String processValue(TemplateController controller,
                      List<Object> args);

  public abstract
  String processValue(TemplateController controller,
                      ASTNode node,
                      List<Object> args);

}
