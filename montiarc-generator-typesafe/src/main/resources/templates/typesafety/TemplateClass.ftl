${tc.signature("package", "fqnTemplateName", "classname", "parameters", "result", "helper")}

<#-- Copyright -->
${tc.defineHookPoint("JavaCopyright")}

package ${package};


import java.nio.file.Path;

import de.se_rwth.commons.logging.Log;
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
  private static ${classname} instance;
  
  private static boolean initialized;
  
  /**
  * Sets an instance of ${classname}. If not set before the method 
  * {@link #execute(MyGeneratorEngine generator, ASTNode node<#if parameters?has_content>, </#if>${helper.printParameters(parameters)}) works after calling this method.
  *
  * @param instance
  */
  public static void setInstance(${classname} instance){
    initialized = true;
    ${classname}.instance = instance;
  }
  
  /**
  * Initializes TemplateClass with instance. {@link #execute(MyGeneratorEngine generator, ASTNode node<#if parameters?has_content>, </#if>${helper.printParameters(parameters)}) works after calling this method.
  *
  * @param instance
  */  
  public static void init(${classname} instance){
    initialized = true;
    ${classname}.instance = instance;
  }
  
  /**
  * Static call of method {@link #execute(MyGeneratorEngine generator, ASTNode node<#if parameters?has_content>, </#if>${helper.printParameters(parameters)})}. 
  * Only works if either {@link #init(${classname} instance)} 
  * or {@link #setInstance(${classname} instance)} was called before.
  *
  * @param generator
  * @param node
  * <#if parameters?has_content>
  <#list parameters as parameter>
  * @param ${parameter.getName()};
  </#list>
  </#if>
  * @result ${result.get().getType()}
  */  
  <#assign simpleName = helper.printSimpleName(result.get().getType())>
  public static ${result.get().getType()} execute(MyGeneratorEngine generator, ASTNode node<#if parameters?has_content>, </#if>${helper.printParameters(parameters)})
  {
    if (!initialized) {
      Log.error("No instance set!");
    }
    return instance.create${simpleName}(generateToString(generator, node<#if parameters?has_content>, </#if>${helper.printParameterNames(parameters)}));
  }
  
  
  public abstract ${result.get().getType()} create${simpleName}(String fileContent);
  
  
  </#if>
  

}
