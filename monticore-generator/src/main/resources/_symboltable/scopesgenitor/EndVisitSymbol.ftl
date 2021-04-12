<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("simpleName", "simpleSymbolName", "removeScope")}
<#if removeScope>
  removeCurrentScope();
</#if>
  if(node.isPresentSymbol()){
    init${simpleSymbolName}HP2(node.getSymbol());
  }