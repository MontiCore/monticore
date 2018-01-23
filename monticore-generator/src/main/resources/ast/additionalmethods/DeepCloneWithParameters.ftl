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
  ${tc.signature("ast","astType")}
  <#assign genHelper = glex.getGlobalVar("astHelper")>
    super.deepClone(result);
    
    <#list astType.getCDAttributeList() as attribute> 
      <#assign attributeName = genHelper.getJavaConformName(attribute.getName())>
      <#assign attrType = attribute.getType()>
      <#assign typeName = genHelper.printType(attribute.getType())>
      <#if genHelper.isAstNode(attribute)>
    result.set${genHelper.getNativeAttributeName(attribute.getName())?cap_first}((${typeName}) this.${attributeName}.deepClone());
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
      </#if>    
    </#list>
    
    return result;
