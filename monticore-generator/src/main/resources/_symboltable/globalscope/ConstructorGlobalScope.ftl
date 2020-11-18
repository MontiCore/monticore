<#-- (c) https://github.com/MontiCore/monticore */ -->
${tc.signature( "scopeDeSerName", "grammarName")}
  this.modelPath = Log.errorIfNull(modelPath);
  this.modelFileExtension = Log.errorIfNull(modelFileExtension);
  this.scopeDeSer = new ${scopeDeSerName}();
