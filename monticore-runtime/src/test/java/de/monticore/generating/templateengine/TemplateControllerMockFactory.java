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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import de.monticore.generating.templateengine.TemplateControllerFactory;
import de.monticore.generating.templateengine.TemplateControllerConfiguration;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 *          $Date$
 *
 */
public class TemplateControllerMockFactory extends TemplateControllerFactory {
  
  private List<TemplateControllerMock> createdTemplateControllers = Lists.newArrayList();
  
  public TemplateControllerMockFactory() {
  }
  
  /**
   * @see mc.codegen.TemplateControllerFactory#create(mc.codegen.TemplateControllerConfiguration, java.lang.String)
   */
  @Override
  public TemplateControllerMock create(TemplateControllerConfiguration config, String templateName) {
    TemplateControllerMock mock = new TemplateControllerMock(config, templateName);
    createdTemplateControllers.add(mock);
    
    return mock;
  }
  
  public List<TemplateControllerMock> getCreatedTemplateControllers() {
    return ImmutableList.copyOf(this.createdTemplateControllers);
  }
  
}
