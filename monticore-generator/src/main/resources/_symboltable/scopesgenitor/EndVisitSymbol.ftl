<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("simpleName", "simpleSymbolName", "removeScope")}
<#if removeScope>
  removeCurrentScope();
  initScopeHP2(node.getSpannedScope());
</#if>
  if(node.isPresentSymbol()){
    init${simpleSymbolName}HP2(node.getSymbol());
  }