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

/**
 * This class helps to test internals of {@link TemplateController}.
 *
 * @author  Pedram Nazari
 *
 */
public class TemplateControllerMock extends TemplateController {

  private TemplateControllerMock subController;
  private TemplateControllerConfiguration tcConfig;
  
  /**
   * Constructor for mc.codegen.ExtendedTemplateControllerForTesting
   * @param tcConfig
   * @param templatename
   */
  protected TemplateControllerMock(TemplateControllerConfiguration tcConfig, String templatename) {
    super(tcConfig, templatename);
    this.tcConfig = tcConfig;
    setTemplateControllerFactory(new TemplateControllerMockFactory());
  }
  
  /**
   * @see mc.codegen.TemplateController#createTemplateController(java.lang.String)
   */
  @Override
  protected TemplateControllerMock createTemplateController(String templateName) {
        subController = new TemplateControllerMock(tcConfig, templateName);
        
        return subController;
  }
  
  TemplateControllerMock getSubController() {
    return subController;
  }
  
}
