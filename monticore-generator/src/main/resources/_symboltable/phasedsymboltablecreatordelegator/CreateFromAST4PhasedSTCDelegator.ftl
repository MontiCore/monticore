<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("artifactScope")}
  ${artifactScope} as = scopeSkeletonCreator.createFromAST(rootNode);
  this.priorityList.forEach(rootNode::accept);
  return as;