${tc.signature("package", "fqnTemplateName", "classname", "parameters", "result", "helper")}

<#-- Copyright -->
${tc.defineHookPoint("JavaCopyright")}

package ${package};


import java.nio.file.Path;
import de.monticore.ast.ASTNode;
import de.montiarc.generator.codegen.MyGeneratorEngine;

/**
 * @date ${helper.getTimeNow()}<br>
 */
public abstract class ${classname} <#t>
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
  public static void generateToFile(MyGeneratorEngine generator, Path filePath, ASTNode node<#if parameters?has_content>, </#if>${helper.printParameters(parameters)})
  {
    generator.createTemplateController("");
    generator.signature(${helper.printParametersAsStringList(parameters)});
    generator.generate("${fqnTemplateName?replace("\\","/")}", filePath, node<#if parameters?has_content>, </#if>${helper.printParameterNames(parameters)});
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
  public static String generateToString(MyGeneratorEngine generator, ASTNode node<#if parameters?has_content>, </#if>${helper.printParameters(parameters)})
  {
    return generator.generateToString("${fqnTemplateName?replace("\\","/")}", node<#if parameters?has_content>, </#if>${helper.printParameterNames(parameters)});
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
  @param function
  </#if>
  * @result ${result.get().getResult()}
  */  
  <#assign simpleName = helper.printSimpleName(result.get().getResult())>
  public static ${result.get().getResult()} execute(MyGeneratorEngine generator, ASTNode node<#if parameters?has_content>, </#if>${helper.printParameters(parameters)}, java.util.function.Function<String, ${result.get().getResult()}> function)
  {
    return function.apply(generateToString(generator, node<#if parameters?has_content>, </#if>${helper.printParameterNames(parameters)}));
  }
  </#if>
  

}
