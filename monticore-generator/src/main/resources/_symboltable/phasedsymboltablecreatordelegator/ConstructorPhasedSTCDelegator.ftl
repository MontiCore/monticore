<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeSkeletonCreatorDelegator")}
  this.globalScope = globalScope;
  this.scopeSkeletonCreator = new ${scopeSkeletonCreatorDelegator}(globalScope);
  this.priorityList = new java.util.ArrayList<>();