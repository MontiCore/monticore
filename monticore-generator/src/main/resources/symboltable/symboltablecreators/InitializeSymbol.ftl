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
${signature("ruleSymbol")}

<#assign ruleName = ruleSymbol.getName()>
<#assign ruleNameLower = ruleName?uncap_first>
<#assign symbolName = ruleName + "Symbol">
<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign fqn = genHelper.getQualifiedGrammarName()?lower_case>
<#assign astPrefix = fqn + "._ast.AST">
    <#-- generates fields that are symbol references -->
    <#assign symbolFields = genHelper.symbolReferenceRuleComponents2JavaFields(ruleSymbol)>
    <#list symbolFields?keys as fname>
      <#assign nonReservedName = genHelper.nonReservedName(fname)>
      <#assign type = symbolFields[fname]>
      <#assign getterPrefix = genHelper.getterPrefix(type)>
      /* Possible fields for containinig symbols
      ${type}Reference ${fname} = new ${type}Reference(ast.${getterPrefix}${fname?cap_first}(), currentScope().get());
      ${ruleNameLower}.set${fname?cap_first}(${fname});
      */
    </#list>
    
