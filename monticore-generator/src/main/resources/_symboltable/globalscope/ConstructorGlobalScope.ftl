<#-- (c) https://github.com/MontiCore/monticore */ -->
${tc.signature( "millFullName", "grammarName")}
  this.modelPath = Log.errorIfNull(modelPath);
  this.modelFileExtension = Log.errorIfNull(modelFileExtension);
  this.scopeDeSer = ${millFullName}
    .${grammarName?uncap_first}ScopeDeSer();
