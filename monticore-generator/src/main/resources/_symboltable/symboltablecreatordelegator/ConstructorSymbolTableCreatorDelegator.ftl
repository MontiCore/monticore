<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symTabMill", "superSymTabCreators", "symTabCreatorName", "simpleName")}
  this.scopeStack.push(globalScope);
  this.globalScope = globalScope;
<#list superSymTabCreators?keys as name>
  ${superSymTabCreators[name]} ${name?uncap_first}SymbolTableCreator = new ${superSymTabCreators[name]}(scopeStack);
  set${name}Visitor(${name?uncap_first}SymbolTableCreator);

</#list>
  symbolTable = ${symTabMill}.${symTabCreatorName?uncap_first}Builder().setScopeStack(scopeStack).build();
  set${simpleName}Visitor(symbolTable);