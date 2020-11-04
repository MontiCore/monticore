<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symTabMill", "symTabCreatorName", "simpleName", "cdName", "superSymTabCreators")}
  this.scopeStack.push(globalScope);
  this.globalScope = globalScope;
<#list superSymTabCreators?keys as name>
  ${superSymTabCreators[name]} ${name?uncap_first}ScopeSkeletonCreator = new ${superSymTabCreators[name]}(scopeStack);
  set${name}Visitor(${name?uncap_first}ScopeSkeletonCreator);

</#list>
  symbolTable = ${symTabMill}.${symTabCreatorName?uncap_first}();
  symbolTable.set${simpleName}ScopeStack(scopeStack);
  set${cdName}Visitor(symbolTable);