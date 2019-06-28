/* (c) https://github.com/MontiCore/monticore */
package de.monticore.generating;

import de.monticore.generating.templateengine.ExtendedTemplateController;
import de.monticore.generating.templateengine.TemplateController;
import freemarker.template.Configuration;

import java.util.HashMap;
import java.util.Map;

public class ExtendedGeneratorSetup extends GeneratorSetup {
  
  private Map<String, String> autoImports = new HashMap<>();
  
  /**
   * @param autoImports the autoImports to set
   */
  public void setAutoImports(Map<String, String> autoImports) {
    this.autoImports = autoImports;
  }
  
  /**
   * @see de.monticore.generating.GeneratorSetup#getNewTemplateController(java.lang.String)
   */
  @Override
  public TemplateController getNewTemplateController(String templateName) {
    return new ExtendedTemplateController(this, templateName);
  }
  
  /**
   * Constructor for de.monticore.generating.ExtendedGeneratorSetup
   */
  public ExtendedGeneratorSetup() {
    super();
  }
  
  /**
   * Constructor for de.monticore.generating.ExtendedGeneratorSetup
   */
  public ExtendedGeneratorSetup(GeneratorSetup s) {
    this.setAdditionalTemplatePaths(s.getAdditionalTemplatePaths());
    this.setAliases(s.getAliases());
    this.setCommentEnd(s.getCommentEnd());
    this.setCommentStart(s.getCommentStart());
    this.setDefaultFileExtension(s.getDefaultFileExtension());
    this.setFileHandler(s.getFileHandler());
    this.setFreeMarkerTemplateEngine(s.getFreeMarkerTemplateEngine());
    this.setGlex(s.getGlex());
    this.setHandcodedPath(s.getHandcodedPath());
    if (s.getModelName().isPresent()) {
      this.setModelName(s.getModelName().get());
    }
    this.setOutputDirectory(s.getOutputDirectory());
    this.setTracing(s.isTracing());
  }
  
  /**
   * @see de.monticore.generating.GeneratorSetup#getConfig()
   */
  @Override
  public Configuration getConfig() {
    Configuration config = super.getConfig();
    config.setAutoImports(this.autoImports);
    return config;
  }
  
}
