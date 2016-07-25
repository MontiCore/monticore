${tc.params("String package", "String fqnTemplateName", "String classname", "java.util.List<freemarker.core.Parameter> parameters",
"Optional<String> result", "de.monticore.templateclassgenerator.codegen.TemplateClassHelper helper")}

<#-- Copyright -->
${tc.defineHookPoint("JavaCopyright")}

package ${package};

<#assign resultPostfix="">

<#if result.isPresent()>
  <#assign resultPostfix = helper.printSimpleName(result.get())>
</#if>


import java.nio.file.Path;
import de.monticore.ast.ASTNode;
import setup.GeneratorConfig;

/**
 * @date ${helper.getTimeNow()}<br>
 */
public class ${classname} <#t>
{

  static ${classname} ${classname?uncap_first};
  
  static ${classname} get${classname}() {
    if (${classname?uncap_first} == null){
      ${classname?uncap_first} = new ${classname}();
    }
    return ${classname?uncap_first};
  }
  
  protected static final void set${classname}(${classname} ${classname?uncap_first}){
    if (${classname?uncap_first} == null) {
      throw new IllegalArgumentException("Supplied ${classname?uncap_first} must not be null!");
    }
    ${classname}.${classname?uncap_first} = ${classname?uncap_first};
  }
  
  protected ${classname}() {
  
  }
  

  /**
  * Generates Template with given parameters to File filePath.
  *
  * @param generator
  * @param filePath
  * @param node
  <#if parameters?has_content>
  <#list parameters as parameter>
  * @param ${parameter.getName()};
  </#list>
  </#if>
  *
  */
  public static void generate${resultPostfix}(Path filePath, ASTNode node<#if parameters?has_content>, </#if>${helper.printParameters(parameters)}) {
    get${classname}().doGenerate${resultPostfix}(filePath, node<#if parameters?has_content>, </#if>${helper.printParameterNames(parameters)});
  }
  
  protected void doGenerate${resultPostfix}(Path filePath, ASTNode node<#if parameters?has_content>, </#if>${helper.printParameters(parameters)}) {
    GeneratorConfig.getGeneratorEngine().generate("${fqnTemplateName?replace("\\","/")}", filePath, node<#if parameters?has_content>, </#if>${helper.printParameterNames(parameters)});
  }
  
  /**
  * Returns generated Template with given parameters as String.
  *
  * @param generator
  * @param node
  <#if parameters?has_content>
  <#list parameters as parameter>
  * @param ${parameter.getName()};
  </#list>
  </#if>
  * @return String
  */
  public static String generate${resultPostfix}(${helper.printParameters(parameters)}) {
    return get${classname}().doGenerate${resultPostfix}(${helper.printParameterNames(parameters)});
  }
  
  protected String doGenerate${resultPostfix}(${helper.printParameters(parameters)}) {
    return GeneratorConfig.getGeneratorEngine().generateToString("${fqnTemplateName?replace("\\","/")}"<#if parameters?has_content>, </#if>${helper.printParameterNames(parameters)});
  }
  
  <#if result.isPresent()>
  /**
  * Executes the generator and returns the template result with help of the passed function.
  *
  * @param generator
  * @param node
  <#if parameters?has_content>
  <#list parameters as parameter>
  * @param ${parameter.getName()};
  </#list>
  * @param function
  </#if>
  * @result ${result.get()}
  */  
  <#assign simpleName = helper.printSimpleName(result.get())>
  public static ${result.get()} generate${resultPostfix}(${helper.printParameters(parameters)}, java.util.function.Function<String, ${result.get()}> function)
  {
    return get${classname}().doGenerate${resultPostfix}(${helper.printParameterNames(parameters)}, function);
  }
  
  protected ${result.get()} doGenerate${resultPostfix}(${helper.printParameters(parameters)}, java.util.function.Function<String, ${result.get()}> function) {
    return function.apply(generate${resultPostfix}(${helper.printParameterNames(parameters)}));
  }
  
  </#if>
  

}
