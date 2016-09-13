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
 ${tc.signature("ast", "grammarName", "fields")}
  <#assign genHelper = glex.getGlobalVar("astHelper")>
  <#assign nameHelper = glex.getGlobalVar("nameHelper")>
  <#assign packageName = grammarName + "Package">
    switch (featureID) {
    <#list fields as field>
      <#assign getter = astHelper.getPlainGetter(field)>
      case ${packageName}.${genHelper.getPlainName(ast)}_${genHelper.getNativeAttributeName(field.getName())?cap_first}:
      <#if genHelper.isListAstNode(field)>
        ${field.getName()}.clear();
      <#elseif genHelper.isOptional(field.getType())>
        ${astHelper.getPlainSetter(field)}(null);
      <#else>
        <#-- TODO GV: not optionals! -->
        ${astHelper.getPlainSetter(field)}(${genHelper.getDefaultValue(field)});
      </#if>
      return;
    </#list>
    }
    eDynamicUnset(featureID);