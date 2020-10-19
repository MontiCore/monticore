<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symTabMill", "symTabCreatorName", "simpleName")}
  this.scopeStack.push(globalScope);
  this.globalScope = globalScope;
  symbolTable = ${symTabMill}.${symTabCreatorName?uncap_first}Builder().setScopeStack(scopeStack).build();
  set${simpleName}Visitor(symbolTable);