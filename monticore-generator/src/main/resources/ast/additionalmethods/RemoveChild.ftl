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
<#--if isAttributeList??>
    if ( get${ast.getName()?cap_first}() == child) {
      set${ast.getName()?cap_first}(null);
    }
<#elseif isAttributeSon??>
    if ( get${ast.getName()?cap_first}() == child) {
      set${ast.getName()?cap_first}((${ast.getObjectType()})null);
    }
</#if-->
   <#-- TODO: attributes of super class - use symbol table -->
    <#list astType.getCDAttributes() as attribute> 
    <#assign attrName = genHelper.getNativeAttributeName(attribute.getName())>
      <#if genHelper.isAstNode(attribute)>
    if (get${attrName?cap_first}() == child) {
      set${attrName?cap_first}(null);
    }
      <#elseif genHelper.isOptionalAstNode(attribute)>
    if (get${attrName?cap_first}().isPresent() && get${attrName?cap_first}().get() == child) {
      set${attrName?cap_first}(null);
    }
      <#elseif genHelper.isListAstNode(attribute)>
    if (get${attrName?cap_first}().contains(child)) {
      get${attrName?cap_first}().remove(child);
    }
      </#if>
    </#list>
