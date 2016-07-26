${tc.params("String package", "java.util.List<String> templates", "String modelPath", "de.monticore.templateclassgenerator.codegen.TemplateClassHelper helper")}

package ${package};

public class Templates {

  <#list templates as template>
    
  private ${glex.getGlobalValue("TemplateClassPackage")}.${template}${glex.getGlobalValue("TemplatePostfix")} ${helper.replaceDotsWithUnderscores(template)} = new ${helper.replaceDotsWithUnderscores(template)?cap_first}_Extended();
  
  public ${glex.getGlobalValue("TemplateClassPackage")}.${template}${glex.getGlobalValue("TemplatePostfix")} get${helper.replaceDotsWithUnderscores(template)?cap_first}() {
    return this.${helper.replaceDotsWithUnderscores(template)};
  }
  
  private class ${helper.replaceDotsWithUnderscores(template)?cap_first}_Extended extends ${glex.getGlobalValue("TemplateClassPackage")}.${template}${glex.getGlobalValue("TemplatePostfix")} {
    protected ${helper.replaceDotsWithUnderscores(template)?cap_first}_Extended () {}
  } 
  
  </#list>
  

}
