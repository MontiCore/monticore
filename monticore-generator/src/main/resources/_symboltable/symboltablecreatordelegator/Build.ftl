<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("sTCDelegator")}
  ${sTCDelegator} obj = new ${sTCDelegator}(globalScope);
  obj.scopeStack = this.scopeStack;
  return obj;