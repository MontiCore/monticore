<#--
***************************************************************************************
Copyright (c) 2017, MontiCore
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
${tc.params("String package", "java.util.List<String> templates", "String modelPath", "de.monticore.templateclassgenerator.codegen.TemplateClassHelper helper")}

package ${package};

public class TemplateAccessor {

  <#list templates as template>
    
  private ${glex.getGlobalVar("TemplateClassPackage")}.${template}${glex.getGlobalVar("TemplatePostfix")} ${helper.replaceDotsWithUnderscores(template)} = new ${helper.replaceDotsWithUnderscores(template)?cap_first}_Extended();
  
  public ${glex.getGlobalVar("TemplateClassPackage")}.${template}${glex.getGlobalVar("TemplatePostfix")} get${helper.replaceDotsWithUnderscores(template)?cap_first}() {
    return this.${helper.replaceDotsWithUnderscores(template)};
  }
  
  private class ${helper.replaceDotsWithUnderscores(template)?cap_first}_Extended extends ${glex.getGlobalVar("TemplateClassPackage")}.${template}${glex.getGlobalVar("TemplatePostfix")}<#if helper.isMainTemplate(template)>Impl</#if> {
    protected ${helper.replaceDotsWithUnderscores(template)?cap_first}_Extended () {}
  } 
  
  </#list>
  

}
