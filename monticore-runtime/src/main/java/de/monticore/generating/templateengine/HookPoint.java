/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine;

import java.util.List;
import java.util.Optional;

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

  /**
   * @return true, if this hook point evaluates a template through the template engine
   */
  public boolean isTemplate(){
    return false;
  }

  /**
   * @return the optional source file or URL associated with this hook point
   */
  public Optional<String> getSourceURL(){
    return Optional.empty();
  }
}
