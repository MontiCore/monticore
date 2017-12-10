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

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import de.monticore.ast.ASTNode;

import java.util.List;

/**
 * Represents a template hook.
 *
 * It executes a template and injects the result at the hook point
 *
 */
public class TemplateHookPoint extends HookPoint {

  private final String templateName;

// XXX BUG TODO, MB:  zu löschen
  private List<Object> templateArguments = Lists.newArrayList();

  public TemplateHookPoint(String templateName) {
    super();
    this.templateName = templateName;
  }

// XXX BUG TODO, MB:  zu löschen
// templateArguments werden grundsätzlich nicht beim HookPoint gesetzt sondern
// beim Include durchgeschleift !!!
// --> das mit dem Durchschleifen beim Include ist aber noch zu realisieren
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

// XXX BUG TODO, MB:  zu ändern
// templateArguments werden grundsätzlich nicht beim HookPoint gesetzt sondern
// beim Include durchgeschleift !!!
// --> das mit dem Durchschleifen beim Include ist aber noch zu realisieren
// ( die include Argumente müssten daher hier wohl als Argumente mitgereicht werden
  @Override
  public String processValue(TemplateController controller, ASTNode ast) {
    if (this.templateArguments.size() > 0) {
      return processValue(controller, this.templateArguments);
    }
    return controller.includeWithoutForwarding(templateName, ast);
  }

// XXX BUG TODO, MB:  zu ändern
// templateArguments werden grundsätzlich nicht beim HookPoint gesetzt sondern
// beim Include durchgeschleift !!!
// --> das mit dem Durchschleifen beim Include ist aber noch zu realisieren
// ( die include Argumente müssten daher hier wohl als Argumente mitgereicht werden
  @Override
  public String processValue(TemplateController controller, List<Object> args) {
    if (this.templateArguments.size() > 0) {
      return controller.includeArgsWithoutForwarding(templateName,
          this.templateArguments);
    }
    return controller.includeArgsWithoutForwarding(templateName, args);
  }
  
  @Override
  public String toString() {
    return Strings.isNullOrEmpty(templateName)? super.toString() : templateName;
  }
}
