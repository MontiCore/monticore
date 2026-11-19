<#-- (c) https://github.com/MontiCore/monticore -->
<@compress single_line=true>
  ${modifier}
  <#if hasTypeParams>${typeParams}</#if>
  ${returnType}
  ${name}
  (${params})
<#if hasThrows> ${throwsDecl}</#if>
</@compress>
<#if isAbstract><#lt>;<#else> {
    // empty body
}
</#if>
