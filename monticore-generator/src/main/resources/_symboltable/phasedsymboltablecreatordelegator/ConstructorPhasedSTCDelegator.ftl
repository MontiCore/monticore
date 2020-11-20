<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopesGenitorDelegator")}
  this.globalScope = globalScope;
  this.scopesGenitorDelegator = new ${scopesGenitorDelegator}(globalScope);
  this.priorityList = new java.util.ArrayList<>();