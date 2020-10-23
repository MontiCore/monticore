<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("simpleName", "removeScope", "hasOptionalName")}
<#if hasOptionalName>
  if (node.isPresentName()) {
</#if>
    initialize_${simpleName}(node.getSymbol(), node);
<#if removeScope>
    removeCurrentScope();
</#if>
<#if hasOptionalName>
  }
</#if>