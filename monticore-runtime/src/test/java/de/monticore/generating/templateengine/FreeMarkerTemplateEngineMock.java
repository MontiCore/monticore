/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine;

import java.util.Set;

import com.google.common.collect.Sets;

import de.monticore.generating.templateengine.freemarker.FreeMarkerTemplateEngine;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * Mock for {@link FreeMarkerTemplateEngine}. Note that this mock CANNOT always substitute
 * {@link FreeMarkerTemplateEngine}, since it just keeps track of templates that need to be
 * processed, but does NOT process them. Consequently, expressions in templates, such as
 * <code>${tc.write(...)}</code>, are not invoked.
 *
 */
public class FreeMarkerTemplateEngineMock extends FreeMarkerTemplateEngine {
  
  private Set<FreeMarkerTemplateMock> processedTemplates = Sets.newLinkedHashSet();
  
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
