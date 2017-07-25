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

package de.monticore.generating.templateengine.freemarker;

/**
 * Represents a template that is automatically imported..
 * 
 * @see FreeMarkerConfigurationBuilder#autoImports(java.util.Map)
 * @see http://freemarker.org/docs/ref_directive_import.html
 * @author Robert Heim
 */
public class TemplateAutoImport {
  private String templatePath;
  
  private String namespaceHash;
  
  /**
   * Constructor for de.monticore.generating.templateengine.TempalateAutoImport
   * 
   * @param templatePath path to the template file.
   * @param namespaceHash The name of the hash variable by which the namespace of the included
   * template can be accessed. See http://freemarker.org/docs/ref_directive_import.html
   */
  public TemplateAutoImport(String templatePath, String namespaceHash) {
    this.templatePath = templatePath;
    this.namespaceHash = namespaceHash;
  }
  
  public String getTemplatePath() {
    return templatePath;
  }
  
  public void setTemplatePath(String templatePath) {
    this.templatePath = templatePath;
  }
  
  public String getNamespaceHash() {
    return namespaceHash;
  }
  
  public void setNamespaceHash(String namespaceHash) {
    this.namespaceHash = namespaceHash;
  }
}
