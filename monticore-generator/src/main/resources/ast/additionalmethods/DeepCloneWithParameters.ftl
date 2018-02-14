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
    result.set${genHelper.getNativeAttributeName(attribute.getName())?cap_first}(this.${attributeName});
      <#elseif genHelper.isOptional(attribute)>
        <#assign reference = genHelper.getSimpleReferenceTypeFromOptional(attrType)>
        <#assign referenceName = genHelper.getQualifiedReferenceNameFromOptional(attrType)>
        <#if genHelper.isString(reference) || genHelper.isAdditionalAttribute(attribute) || genHelper.isAttributeOfTypeEnum(attribute)>
    result.set${genHelper.getNativeAttributeName(attribute.getName())?cap_first}Opt(this.${attributeName});
        <#elseif genHelper.isOptionalAstNode(attribute)>
    if (isPresent${genHelper.getNativeAttributeName(attribute.getName())?cap_first}()){
      result.set${genHelper.getNativeAttributeName(attribute.getName())?cap_first}(this.${attributeName}.get().deepClone());
    } else {
      result.set${genHelper.getNativeAttributeName(attribute.getName())?cap_first}Absent();
    }
        <#else>
    if (isPresent${genHelper.getNativeAttributeName(attribute.getName())?cap_first}()){
      result.set${genHelper.getNativeAttributeName(attribute.getName())?cap_first}(this.${attributeName}.get().clone());
    } else {
      result.set${genHelper.getNativeAttributeName(attribute.getName())?cap_first}Absent();
    }
    </#if>
      <#else>
        <#if genHelper.isListAstNode(attribute)>
    for ( int i = 0; i < result.get${genHelper.getNativeAttributeName((attribute.getName())?cap_first)?remove_ending("s")}List().size() ; i++ ) {
      result.set${genHelper.getNativeAttributeName((attribute.getName())?cap_first)?remove_ending("s")}( i, this.${attributeName}.get(i));
    }
    this.${attributeName}.forEach(s -> result.${attributeName}.add(s.deepClone()));
        <#elseif genHelper.isListType(typeName)>
    for ( int i = 0; i < this.get${genHelper.getNativeAttributeName((attribute.getName())?cap_first)?remove_ending("s")}List().size() ; i++ ) {
        result.add${genHelper.getNativeAttributeName((attribute.getName())?cap_first)?remove_ending("s")}(this.get${genHelper.getNativeAttributeName((attribute.getName())?cap_first)?remove_ending("s")}(i));
    }
        <#elseif genHelper.isString(typeName) || genHelper.isAttributeOfTypeEnum(attribute)>
    result.set${genHelper.getNativeAttributeName(attribute.getName())?cap_first}(this.${attributeName});
        <#else>
    result.set${genHelper.getNativeAttributeName(attribute.getName())?cap_first}(this.${attributeName});
        </#if>
      </#if>    
    </#list>
    
    return result;
