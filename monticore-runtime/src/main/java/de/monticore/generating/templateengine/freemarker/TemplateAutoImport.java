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
