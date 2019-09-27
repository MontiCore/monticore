<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("superSymTabCreators", "symTabCreatorName", "simpleName")}
  this.scopeStack.push(globalScope);
  this.globalScope = globalScope;
<#list superSymTabCreators?keys as name>
  ${superSymTabCreators[name]} ${name?uncap_first}SymbolTableCreator = new ${superSymTabCreators[name]}(scopeStack);
  set${name}Visitor(${name?uncap_first}SymbolTableCreator);

</#list>
  symbolTable = new ${symTabCreatorName}(scopeStack);
  set${simpleName}Visitor(symbolTable);