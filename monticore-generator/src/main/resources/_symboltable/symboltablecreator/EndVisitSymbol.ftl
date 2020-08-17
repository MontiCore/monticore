<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("simpleName", "removeScope")}
initialize_${simpleName}(node.getSymbol(), node);
<#if removeScope>
removeCurrentScope();
</#if>