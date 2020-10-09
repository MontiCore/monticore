<#-- (c) https://github.com/MontiCore/monticore */ -->
${tc.signature("component", "millFullName", "grammarName")}
  this.modelPath = new de.monticore.io.paths.ModelPath();
  this.modelFileExtension = "";
<#if component>
  this.modelLoader = Optional.empty();
<#else>
  this.enableModelLoader();
</#if>
  this.scopeDeSer = ${millFullName}
    .${grammarName?uncap_first}ScopeDeSerBuilder()
    .build();