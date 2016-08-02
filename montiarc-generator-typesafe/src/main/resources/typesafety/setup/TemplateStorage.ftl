${tc.params("String package", "java.util.List<String> templates", "String modelPath", "de.monticore.templateclassgenerator.codegen.TemplateClassHelper helper")}

package ${package};

public class TemplateStorage {

  <#list templates as template>
    
  private ${helper.replaceDotsWithUnderscores(template)?cap_first}_Extended ${helper.replaceDotsWithUnderscores(template)} = new ${helper.replaceDotsWithUnderscores(template)?cap_first}_Extended();
  
  public ${helper.replaceDotsWithUnderscores(template)?cap_first}_Extended get${helper.replaceDotsWithUnderscores(template)?cap_first}_Extended() {
    return this.${helper.replaceDotsWithUnderscores(template)};
  }
  
  public static class ${helper.replaceDotsWithUnderscores(template)?cap_first}_Extended {
    protected ${helper.replaceDotsWithUnderscores(template)?cap_first}_Extended () {}
    
    public static String generate(Object... args) {
      return GeneratorConfig.getGeneratorEngine().generateToString("${template?replace(".","/")}.ftl", args);
    }
  } 
  
  </#list>
  

}
