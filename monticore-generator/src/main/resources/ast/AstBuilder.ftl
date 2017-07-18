<#--
***************************************************************************************
Copyright (c) 2015, MontiCore
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
${tc.signature("ast", "astType")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
 /**
   * Builder for {@link ${astType.getName()}}.
   */
 
  <#assign abstract = "">
  <#if (astType.getSuperclass().isPresent() && genHelper.isSuperClassExternal(astType))
   || (astType.getModifier().isPresent() && astType.getModifier().get().isAbstract() && !genHelper.isSupertypeOfHWType(astType.getName()))>
    <#assign abstract = "abstract">
  </#if>
  <#assign extends = "">
  <#if astType.getSuperclass().isPresent() && !genHelper.isSuperClassExternal(astType)>
    <#assign extends = "extends " + genHelper.getSuperClassForBuilder(astType) + ".Builder">
  </#if>
  public ${abstract} static class Builder ${extends} {
  <#list astType.getCDAttributes() as attribute>
    <#if !genHelper.isInherited(attribute) && !genHelper.isAdditionalAttribute(attribute)>
    ${tc.include("ast.BuilderAttribute", attribute)}
    </#if>
  </#list>
  <#assign typeName = genHelper.getPlainName(astType)>
  <#if abstract?has_content>
    protected Builder() {};
    
    public abstract ${typeName} build();
  <#else>
    public ${typeName} build() {
      return new ${typeName} (${tc.include("ast.ParametersDeclaration", ast)}
      );
    }
    </#if>
    
    ${tc.include("ast.AstBuilderAttributeSetter", genHelper.getNativeCDAttributes(astType))}
    
  }    
