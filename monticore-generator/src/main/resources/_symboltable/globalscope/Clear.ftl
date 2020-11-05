<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("resolverMethodList")}
  clearLoadedFiles();
<#list resolverMethodList as resolverMethod>
  ${resolverMethod}().clear();
</#list>
  this.modelPath = new de.monticore.io.paths.ModelPath();