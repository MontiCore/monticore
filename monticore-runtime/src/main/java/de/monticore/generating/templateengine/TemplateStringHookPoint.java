/*
 * ******************************************************************************
 * MontiCore Language Workbench
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

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import de.monticore.ast.ASTNode;

import com.google.common.collect.Lists;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class TemplateStringHookPoint extends HookPoint {
  private Template template;

  public TemplateStringHookPoint(String statement) throws IOException {
    super();
    template = new Template("template", new StringReader(statement),
        new Configuration());
  }
  
  @Override
  public String processValue(TemplateController controller, ASTNode ast) {
    return controller.runInEngine(Lists.newArrayList(), template, ast).toString();
  }

  @Override
  public String processValue(TemplateController controller, List<Object> args) {
    return controller.runInEngine(Lists.newArrayList(), template, null).toString();
  }

}
