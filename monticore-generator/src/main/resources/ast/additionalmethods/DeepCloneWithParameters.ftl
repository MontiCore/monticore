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
  ${tc.signature("ast","astType")}
  <#assign genHelper = glex.getGlobalValue("astHelper")>
    super.deepClone(result);
    
    <#list astType.getCDAttributes() as attribute> 
      <#assign attributeName = genHelper.getJavaConformName(attribute.getName())>
      <#assign attrType = attribute.getType()>
      <#assign typeName = genHelper.printType(attribute.getType())>
      <#if genHelper.isAstNode(attribute)>
    if (this.${attributeName} != null) {
      result.set${genHelper.getNativeAttributeName(attribute.getName())?cap_first}((${typeName}) this.${attributeName}.deepClone());
    }
      <#elseif genHelper.isPrimitive(attribute.getType())> 
    result.${attributeName} = this.${attributeName};
      <#elseif genHelper.isOptional(attribute)>
        <#assign reference = genHelper.getSimpleReferenceTypeFromOptional(attrType)>
        <#assign referenceName = genHelper.getQualifiedReferenceNameFromOptional(attrType)>
        <#if genHelper.isString(reference) || genHelper.isAdditionalAttribute(attribute) || genHelper.isAttributeOfTypeEnum(attribute)>
          <#assign clone = "">
        <#elseif genHelper.isOptionalAstNode(attribute)>
           <#assign clone = ".deepClone()">
        <#else>
           <#assign clone = ".clone()">
        </#if>
    result.${attributeName} = this.${attributeName}.isPresent()? Optional.ofNullable((${referenceName})this.${attributeName}.get()${clone}) : Optional.empty();
      <#else>  
    if (this.${attributeName} != null) {
        <#if genHelper.isString(typeName) || genHelper.isAttributeOfTypeEnum(attribute)> 
      result.${attributeName} = this.${attributeName};
        <#elseif genHelper.isListAstNode(attribute)>
      result.${attributeName} = com.google.common.collect.Lists.newArrayList();
      this.${attributeName}.forEach(s -> result.${attributeName}.add(s.deepClone()));
        <#elseif genHelper.isListType(typeName)>
      result.${attributeName} = com.google.common.collect.Lists.newArrayList(this.${attributeName});
        <#else>
      result.${attributeName} = (${typeName}) this.${attributeName}.clone();
        </#if>
    }
      </#if>    
    </#list>
    return result;
