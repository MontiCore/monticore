<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("mill", "simpleName", "cdName", "superSymTabCreators")}
  this.scopeStack.push(globalScope);
  this.globalScope = globalScope;
  this.traverser = ${mill}.traverser();
<#list superSymTabCreators?keys as name>
  ${superSymTabCreators[name]} ${name?uncap_first}ScopesGenitor = new ${superSymTabCreators[name]}(scopeStack);
  traverser.add${name}Visitor(${name?uncap_first}ScopesGenitor);
  traverser.set${name}Handler(${name?uncap_first}ScopesGenitor);

</#list>
  symbolTable = ${mill}.scopesGenitor();
  symbolTable.set${simpleName}ScopeStack(scopeStack);
  traverser.add${cdName}Visitor(symbolTable);
  traverser.set${cdName}Handler(symbolTable);