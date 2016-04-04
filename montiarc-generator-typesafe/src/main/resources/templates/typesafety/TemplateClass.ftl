${tc.signature("package", "fqnTemplateName", "classname", "parameters", "result", "helper")}

<#-- Copyright -->
${tc.defineHookPoint("JavaCopyright")}

package ${package};


import java.nio.file.Path;


import de.monticore.ast.ASTNode;
import de.monticore.generating.GeneratorEngine;

/**
 * @date ${helper.getTimeNow()}<br>
 */
public abstract class ${classname} <#t>
{

  public static void generate(GeneratorEngine generator, Path filePath, ASTNode node<#if parameters?has_content>, </#if>${helper.printParameters(parameters)})
  {
    generator.generate("${fqnTemplateName?replace("\\","/")}", filePath, node<#if parameters?has_content>, </#if>${helper.printParameterNames(parameters)});
  }
  
  
  <#if result??>
  <#assign simpleName = helper.printSimpleName(result.get().getResultType())>
  /**public static ${result.get().getResultType()} execute()
  {
    return create${simpleName}("");
  }
  
  public static abstract ${result.get().getResultType()} create${simpleName}(String fileContent);*/
  </#if>
  
  
  public static String execute()
  {
    return "";
  }

  
  
  

}
