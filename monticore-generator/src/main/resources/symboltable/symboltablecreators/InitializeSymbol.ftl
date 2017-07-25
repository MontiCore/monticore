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
    
