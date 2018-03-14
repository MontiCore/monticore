/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import de.monticore.ast.ASTNode;

/**
 * Represents a template hook. It executes a template and injects the result at the hook point
 */
public class TemplateHookPoint extends HookPoint {
  
  private final String templateName;
  
  private List<Object> templateArguments = Lists.newArrayList();
  
  public TemplateHookPoint(String templateName) {
    super();
    this.templateName = templateName;
  }
  
  public TemplateHookPoint(String templateName, Object... templateArguments) {
    super();
    this.templateName = templateName;
    this.templateArguments = Lists.newArrayList(templateArguments);
  }
  
  /**
   * @return templateName
   */
  public String getTemplateName() {
    return this.templateName;
  }
  
  @Override
  public String processValue(TemplateController controller, ASTNode ast) {
    return controller.processTemplate(templateName, ast, this.templateArguments).toString();   
  }
  
  // Here we handle the case that arguments come from:
  // a) The Hookpoint itself and
  // b) the Template call
  // The Template-call arguments are includes first and the 
  // Hookpoint arguments are added second
  @Override
  public String processValue(TemplateController controller, List<Object> args) {
    if (this.templateArguments.size() > 0) {
      ArrayList<Object> l = Lists.newArrayList(args);
      l.addAll(this.templateArguments);
      return controller.processTemplate(templateName, controller.getAST(), l).toString();
    }
    return controller.processTemplate(templateName, controller.getAST(), args).toString();
  }
  
  @Override
  public String toString() {
    return Strings.isNullOrEmpty(templateName) ? super.toString() : templateName;
  }
  
  /**
   * @see de.monticore.generating.templateengine.HookPoint#processValue(de.monticore.generating.templateengine.TemplateController,
   * de.monticore.ast.ASTNode, java.util.List)
   */
  @Override
  public String processValue(TemplateController controller, ASTNode node, List<Object> args) {
  // Here we handle the case that arguments come from:
  // a) The Hookpoint itself and
  // b) the Template call
  // The Template-call arguments are includes first and the 
  // Hookpoint arguments are added second
    if (this.templateArguments.size() > 0) {
      ArrayList<Object> l = Lists.newArrayList(args);
      l.addAll(this.templateArguments);
      return controller.processTemplate(templateName, node, l).toString();
    }
    return controller.processTemplate(templateName, node, args).toString();
  }
}
