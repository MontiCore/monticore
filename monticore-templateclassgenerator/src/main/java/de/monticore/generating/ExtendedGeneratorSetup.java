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
package de.monticore.generating;

import de.monticore.generating.templateengine.ExtendedTemplateController;
import de.monticore.generating.templateengine.TemplateController;

public class ExtendedGeneratorSetup extends GeneratorSetup {

  /**
   * @see de.monticore.generating.GeneratorSetup#getNewTemplateController(java.lang.String)
   */
  @Override
  public TemplateController getNewTemplateController(String templateName) {
    return new ExtendedTemplateController(this, templateName);
  }
  
}
