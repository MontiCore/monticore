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

  

  public static void generateToFile(MyGeneratorEngine generator, Path filePath, ASTNode node<#if parameters?has_content>, </#if>${helper.printParameters(parameters)})
  {
    generator.generate("${fqnTemplateName?replace("\\","/")}", filePath, node<#if parameters?has_content>, </#if>${helper.printParameterNames(parameters)});
  }
  
    
  
  public static String generateToString(MyGeneratorEngine generator, ASTNode node<#if parameters?has_content>, </#if>${helper.printParameters(parameters)})
  {
    return generator.generateToString("${fqnTemplateName?replace("\\","/")}", node<#if parameters?has_content>, </#if>${helper.printParameterNames(parameters)});
  }
  
  
  <#if result.isPresent()>
  private static ${classname} instance;
  
  private static boolean initialized;
  
  public static void setInstance(${classname} instance){
    initialized = true;
    ${classname}.instance = instance;
  }
  
  public static void init(${classname} instance){
    initialized = true;
    ${classname}.instance = instance;
  }
  
  <#assign simpleName = helper.printSimpleName(result.get().getResultType())>
  public static ${result.get().getResultType()} execute(MyGeneratorEngine generator, ASTNode node<#if parameters?has_content>, </#if>${helper.printParameters(parameters)})
  {
    if (!initialized) {
      Log.error("No instance set!");
    }
    return instance.create${simpleName}(generateToString(generator, node<#if parameters?has_content>, </#if>${helper.printParameterNames(parameters)}));
  }
  
  public abstract ${result.get().getResultType()} create${simpleName}(String fileContent);
  
  
  </#if>
  

}
