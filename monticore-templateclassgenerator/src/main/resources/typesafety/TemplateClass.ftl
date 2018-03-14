<#-- (c) https://github.com/MontiCore/monticore -->
${tc.params("String package", "String fqnTemplateName", "String classname", "java.util.List<freemarker.core.Parameter> parameters",
"Optional<String> result", "Boolean hasSignature", "Boolean isMainTemplate", "de.monticore.templateclassgenerator.codegen.TemplateClassHelper helper")}

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

package ${package};


import java.nio.file.Path;
import de.monticore.ast.ASTNode;
import ${glex.getGlobalVar("TemplateClassPackage")}.${glex.getGlobalVar("TemplateClassSetupPackage")}.GeneratorConfig;
import de.monticore.templateclassgenerator.util.GeneratorInterface;
import de.monticore.symboltable.CommonSymbol;


/**
 * @date ${helper.getTimeNow()}<br>
 */
public <#if isMainTemplate>abstract</#if> class ${classname} <#if isMainTemplate>implements GeneratorInterface </#if><#t>
{

  static ${classname} ${classname?uncap_first};
  
  static ${classname} get${classname}() {
    if (${classname?uncap_first} == null){
    <#if isMainTemplate>
      ${classname?uncap_first} = ${classname}Factory.create();
    <#else>
      ${classname?uncap_first} = new ${classname}();
    </#if>
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
  <#assign printedParams = helper.printParameters(parameters)>
  <#assign printedParamNames = helper.printParameterNames(parameters)>
  <#assign defaultParam = "Object... args">
  <#assign defaultParamName = "args">
  
  public static void generate(Path filePath, ASTNode node<#if parameters?has_content || !hasSignature>, </#if><#if !hasSignature>${defaultParam}<#else>${printedParams}</#if>) {
    get${classname}().doGenerate(filePath, node<#if parameters?has_content || !hasSignature>, </#if><#if !hasSignature>${defaultParamName}<#else>${printedParamNames}</#if>);
  }
  
  protected void doGenerate(Path filePath, ASTNode node<#if parameters?has_content || !hasSignature>, </#if><#if !hasSignature>${defaultParam}<#else>${printedParams}</#if>) {
    GeneratorConfig.getGeneratorEngine().generate("${fqnTemplateName?replace("\\","/")}", filePath, node<#if parameters?has_content || !hasSignature>, </#if><#if !hasSignature>${defaultParamName}<#else>${printedParamNames}</#if>);
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
  public static String generate(<#if !hasSignature>${defaultParam}<#else>${printedParams}</#if>) {
    return get${classname}().doGenerate(<#if !hasSignature>${defaultParamName}<#else>${printedParamNames}</#if>);
  }
  
  protected String doGenerate(<#if !hasSignature>${defaultParam}<#else>${printedParams}</#if>) {
    return GeneratorConfig.getGeneratorEngine().generate("${fqnTemplateName?replace("\\","/")}"<#if parameters?has_content || !hasSignature>, </#if><#if !hasSignature>${defaultParamName}<#else>${printedParamNames}</#if>);
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
  public static ${result.get()} generate(${helper.printParameters(parameters)}, java.util.function.Function<String, ${result.get()}> function)
  {
    return get${classname}().doGenerate(${helper.printParameterNames(parameters)}, function);
  }
  
  protected ${result.get()} doGenerate(${helper.printParameters(parameters)}, java.util.function.Function<String, ${result.get()}> function) {
    return function.apply(generate(${helper.printParameterNames(parameters)}));
  }
  
  </#if>
  

}
