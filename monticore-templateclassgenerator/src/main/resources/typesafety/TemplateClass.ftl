<#--
***************************************************************************************
Copyright (c) 2016, MontiCore
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors
may be used to endorse or promote products derived from this software
without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
SUCH DAMAGE.
***************************************************************************************
-->
${tc.params("String package", "String fqnTemplateName", "String classname", "java.util.List<freemarker.core.Parameter> parameters",
"Optional<String> result", "de.monticore.templateclassgenerator.codegen.TemplateClassHelper helper")}

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
  public static void generate(Path filePath, ASTNode node<#if parameters?has_content>, </#if>${helper.printParameters(parameters)}) {
    get${classname}().doGenerate(filePath, node<#if parameters?has_content>, </#if>${helper.printParameterNames(parameters)});
  }
  
  protected void doGenerate(Path filePath, ASTNode node<#if parameters?has_content>, </#if>${helper.printParameters(parameters)}) {
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
  public static String generate(${helper.printParameters(parameters)}) {
    return get${classname}().doGenerate(${helper.printParameterNames(parameters)});
  }
  
  protected String doGenerate(${helper.printParameters(parameters)}) {
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
  public static ${result.get()} generate(${helper.printParameters(parameters)}, java.util.function.Function<String, ${result.get()}> function)
  {
    return get${classname}().doGenerate(${helper.printParameterNames(parameters)}, function);
  }
  
  protected ${result.get()} doGenerate(${helper.printParameters(parameters)}, java.util.function.Function<String, ${result.get()}> function) {
    return function.apply(generate(${helper.printParameterNames(parameters)}));
  }
  
  </#if>
  

}
