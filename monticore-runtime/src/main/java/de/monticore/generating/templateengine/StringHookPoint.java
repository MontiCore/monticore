/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.source_mapping.SourceMapCalculator;
import de.monticore.generating.templateengine.source_mapping.SourceMapping;

import java.util.List;
import java.util.Optional;

/**
 * Represents a String hook point.
 * 
 * The string will be copied as result of the hook without further change.
 *
 */
public class StringHookPoint extends HookPoint {

  protected final String value;
  protected final Optional<String> sourceURL;

  public StringHookPoint(String value) {
    super();
    this.value = value;
    this.sourceURL = Optional.empty();
  }

  public StringHookPoint(String value, String sourceURL) {
    super();
    this.value = value;
    this.sourceURL = Optional.of(sourceURL);
  }
  
  @Override
  public String processValue(TemplateController controller, ASTNode ast) {
    return this.processValue(controller, ast, List.of());
  }

  @Override
  public String processValue(TemplateController controller, List<Object> args) {
    return this.processValue(controller, null, List.of());
  }
  
  /**
   * @see de.monticore.generating.templateengine.HookPoint#processValue(de.monticore.generating.templateengine.TemplateController, de.monticore.ast.ASTNode, java.util.List)
   */
  @Override
  public String processValue(TemplateController controller, ASTNode node, List<Object> args) {
    if(this.sourceURL.isPresent()) {
      System.out.println("String HookPoint changes Template Mappings");
      SourceMapCalculator.reportStringHP(value, sourceURL.get(), node);
    }
    return value;
  }
  
  public String getValue() {
	  return value;
  }

 
  
}
