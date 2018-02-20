<#-- (c) https://github.com/MontiCore/monticore -->
${tc.params("String package", "java.util.List<String> templates", "String modelPath", "de.monticore.templateclassgenerator.codegen.TemplateClassHelper helper")}

package ${package};

public class TemplateAccessor {

  <#list templates as template>
    
  private ${glex.getGlobalVar("TemplateClassPackage")}.${template}${glex.getGlobalVar("TemplatePostfix")} ${helper.replaceDotsWithUnderscores(template)} = new ${helper.replaceDotsWithUnderscores(template)?cap_first}_Extended();
  
  public ${glex.getGlobalVar("TemplateClassPackage")}.${template}${glex.getGlobalVar("TemplatePostfix")} get${helper.replaceDotsWithUnderscores(template)?cap_first}() {
    return this.${helper.replaceDotsWithUnderscores(template)};
  }
  
  private class ${helper.replaceDotsWithUnderscores(template)?cap_first}_Extended extends ${glex.getGlobalVar("TemplateClassPackage")}.${template}${glex.getGlobalVar("TemplatePostfix")}<#if helper.isMainTemplate(template)>Impl</#if> {
    protected ${helper.replaceDotsWithUnderscores(template)?cap_first}_Extended () {}
  } 
  
  </#list>
  

}
