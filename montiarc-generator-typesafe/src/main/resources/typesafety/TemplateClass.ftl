${tc.params("String package", "String fqnTemplateName", "String classname", "java.util.List<freemarker.core.Parameter> parameters",
"String result", "de.montiarc.generator.codegen.TemplateClassHelper helper")}

<#-- Copyright -->
${tc.defineHookPoint("JavaCopyright")}

package ${package};



import java.nio.file.Path;
import de.monticore.ast.ASTNode;
import setup.GeneratorConfig;

/**
 * @date ${helper.getTimeNow()}<br>
 */
public class ${classname} <#t>
{

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
  public static void generateToFile(Path filePath, ASTNode node<#if parameters?has_content>, </#if>${helper.printParameters(parameters)})
  {
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
  public static String generateToString(${helper.printParameters(parameters)})
  {
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
  public static ${result.get()} generateToResult(${helper.printParameters(parameters)}, java.util.function.Function<String, ${result.get()}> function)
  {
    return function.apply(generateToString(${helper.printParameterNames(parameters)}));
  }
  </#if>
  

}
