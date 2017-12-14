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
