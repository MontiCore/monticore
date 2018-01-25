/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

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
    if (this.templateArguments.size() > 0) {
      ArrayList<Object> l = Lists.newArrayList(args);
      l.addAll(this.templateArguments);
      return controller.processTemplate(templateName, node, l).toString();
    }
    return controller.processTemplate(templateName, node, args).toString();
  }
}
