<#-- (c) https://github.com/MontiCore/monticore -->
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
    
