<#--
****************************************************************************
MontiCore Language Workbench, www.monticore.de
Copyright (c) 2017, MontiCore, All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its
contributors may be used to endorse or promote products derived from this
software without specific prior written permission.

This software is provided by the copyright holders and contributors
"as is" and any express or implied warranties, including, but not limited
to, the implied warranties of merchantability and fitness for a particular
purpose are disclaimed. In no event shall the copyright holder or
contributors be liable for any direct, indirect, incidental, special,
exemplary, or consequential damages (including, but not limited to,
procurement of substitute goods or services; loss of use, data, or
profits; or business interruption) however caused and on any theory of
liability, whether in contract, strict liability, or tort (including
negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.
****************************************************************************
-->
${tc.params("String package", "String fqnTemplateName", "String classname", "java.util.List<freemarker.core.Parameter> parameters",
"Optional<String> result", "Boolean hasSignature", "Boolean isMainTemplate", "de.monticore.templateclassgenerator.codegen.TemplateClassHelper helper")}

<#-- Copyright -->
${tc.defineHookPoint("JavaCopyright")}

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
