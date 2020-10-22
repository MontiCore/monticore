<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symTabMill", "symTabCreatorName", "simpleName", "cdName")}
  this.scopeStack.push(globalScope);
  this.globalScope = globalScope;
  symbolTable = ${symTabMill}.${symTabCreatorName?uncap_first}();
  symbolTable.set${simpleName}ScopeStack(scopeStack);
  set${cdName}Visitor(symbolTable);