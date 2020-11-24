<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("artifactScope")}
  ${artifactScope} as = scopesGenitorDelegator.createFromAST(rootNode);
  this.priorityList.forEach(rootNode::accept);
  return as;