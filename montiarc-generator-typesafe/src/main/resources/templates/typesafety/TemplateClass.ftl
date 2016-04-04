${tc.signature("package", "fqnTemplateName", "classname", "parameters", "result", "helper")}

<#-- Copyright -->
${tc.defineHookPoint("JavaCopyright")}

package ${package};


import java.nio.file.Path;


import de.monticore.ast.ASTNode;
import de.monticore.generating.GeneratorEngine;
import java.util.Optional;

/**
 * @date ${helper.getTimeNow()}<br>
 */
public abstract class ${classname} <#t>
{

  

  public static void generate(GeneratorEngine generator, Path filePath, ASTNode node<#if parameters?has_content>, </#if>${helper.printParameters(parameters)})
  {
    generator.generate("${fqnTemplateName?replace("\\","/")}", filePath, node<#if parameters?has_content>, </#if>${helper.printParameterNames(parameters)});
  }
  
    
  
  public static String printTemplateResult()
  {
    return "";
  }
  
  
  <#if result??>
  private static ${classname} instance;
  
  private static ${classname} getInstance(){
    if(null == instance) {
      instance = new ${classname}Default();
    }
    return instance;
  }
  
  public static void setInstance(${classname} instance){
    ${classname}.instance = instance;
  }
  
  <#assign simpleName = helper.printSimpleName(result.get().getResultType())>
  public static Optional<${result.get().getResultType()}> execute()
  {
    return getInstance().create${simpleName}("");
  }
  
  public abstract Optional<${result.get().getResultType()}> create${simpleName}(String fileContent);



  
  public static final class ${classname}Default extends ${classname} {
    
    @Override
    public Optional<${result.get().getResultType()}> create${simpleName}(String fileContent) {
      return Optional.empty();
    }
    
  }
  
  
  </#if>
  

}
