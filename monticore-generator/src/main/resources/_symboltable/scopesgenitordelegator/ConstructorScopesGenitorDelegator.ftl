<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("mill", "cdName", "superSymTabCreators", "millNames")}
  this.globalScope = ${mill}.globalScope();
  this.traverser = ${mill}.traverser();
  this.scopeStack.push(this.globalScope);
<#list superSymTabCreators?keys as name>
  ${superSymTabCreators[name]} ${name?uncap_first}ScopesGenitor = ${millNames[name]}.scopesGenitor();
  ${name?uncap_first}ScopesGenitor.setScopeStack(scopeStack);
  traverser.add4${name}(${name?uncap_first}ScopesGenitor);
  traverser.set${name}Handler(${name?uncap_first}ScopesGenitor);

</#list>
  symbolTable = ${mill}.scopesGenitor();
  symbolTable.setScopeStack(scopeStack);
  traverser.add4${cdName}(symbolTable);
  traverser.set${cdName}Handler(symbolTable);