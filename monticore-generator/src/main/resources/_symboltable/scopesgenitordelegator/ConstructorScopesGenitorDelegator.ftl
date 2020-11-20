<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symTabMill", "simpleName", "cdName", "superSymTabCreators")}
  this.scopeStack.push(globalScope);
  this.globalScope = globalScope;
<#list superSymTabCreators?keys as name>
  ${superSymTabCreators[name]} ${name?uncap_first}ScopesGenitor = new ${superSymTabCreators[name]}(scopeStack);
  set${name}Visitor(${name?uncap_first}ScopesGenitor);

</#list>
  symbolTable = ${symTabMill}.scopesGenitor();
  symbolTable.set${simpleName}ScopeStack(scopeStack);
  set${cdName}Visitor(symbolTable);