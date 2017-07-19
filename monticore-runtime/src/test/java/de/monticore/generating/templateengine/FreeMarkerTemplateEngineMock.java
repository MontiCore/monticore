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

import java.util.Set;

import com.google.common.collect.Sets;

import de.monticore.generating.templateengine.freemarker.FreeMarkerConfigurationBuilder;
import de.monticore.generating.templateengine.freemarker.FreeMarkerTemplateEngine;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * Mock for {@link FreeMarkerTemplateEngine}. Note that this mock CANNOT always substitute
 * {@link FreeMarkerTemplateEngine}, since it just keeps track of templates that need to be
 * processed, but does NOT process them. Consequently, expressions in templates, such as
 * <code>${tc.write(...)}</code>, are not invoked.
 *
 * @author (last commit) $Author$
 */
public class FreeMarkerTemplateEngineMock extends FreeMarkerTemplateEngine {
  
  private Set<FreeMarkerTemplateMock> processedTemplates = Sets.newLinkedHashSet();
  
  public FreeMarkerTemplateEngineMock() {
    super(new FreeMarkerConfigurationBuilder().build());
  }
  
  public FreeMarkerTemplateEngineMock(Configuration configuration) {
    super(configuration);
  }
  
  /**
   * @see de.monticore.generating.templateengine.freemarker.FreeMarkerTemplateEngine#loadTemplate(java.lang.String)
   */
  @Override
  public Template loadTemplate(String qualifiedTemplateName) {
    return FreeMarkerTemplateMock.of(qualifiedTemplateName);
  }
  
  /**
   * @see de.monticore.generating.templateengine.freemarker.FreeMarkerTemplateEngine#run(java.lang.StringBuilder,
   * java.lang.Object, freemarker.template.Template)
   */
  @Override
  public void run(StringBuilder buffer, Object data, Template template) {
    FreeMarkerTemplateMock processedTemplate = FreeMarkerTemplateMock.of(template.getName());
    processedTemplate.process(data, null);
    
    processedTemplates.add(processedTemplate);
    
    buffer.append("Content of template: " + template.getName());
  }
  
  public Set<FreeMarkerTemplateMock> getProcessedTemplates() {
    return this.processedTemplates;
  }
  
}
