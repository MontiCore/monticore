/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2016, MontiCore, All rights reserved.
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
package de.monticore.templateclassgenerator.codegen;

import de.monticore.generating.templateengine.ExtendedTemplateController;

/**
 * Sets some constants for the Generator and the generated template classes
 *
 * @author Jerome Pfeiffer
 */
public class TemplateClassGeneratorConstants {
  
  /**
   * Alias where the generated TemplateStorage is referencable from
   */
  public static final String TEMPLATES_ALIAS = "TemplatesClass";
  
  /**
   * The package where all template classes are generated to
   */
  public static final String TEMPLATE_CLASSES_PACKAGE = "_templates";
  
  /**
   * The package where the template classes' setup is generated to
   */
  public static final String TEMPLATE_CLASSES_SETUP_PACKAGE = "_setup";
    
  /**
   * Postfix for the name of templateclasses
   */
  public static final String TEMPLATE_CLASSES_POSTFIX = "";
  
  /**
   * Signature method to define template params. IMPORTANT: You also have to
   * change the method name in {@link ExtendedTemplateController}. Otherwise you
   * will loose the dynamic type check of template arguments.
   */
  public static final String PARAM_METHOD = "tc.params";
  
  /**
   * Signature methode to define template result.
   */
  public static final String RESULT_METHOD = "tc.result";
  
  public static final String DEFAULT_OUTPUT_FOLDER = "out";
  
}
