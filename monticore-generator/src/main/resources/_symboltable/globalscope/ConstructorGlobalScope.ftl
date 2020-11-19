<#-- (c) https://github.com/MontiCore/monticore */ -->
${tc.signature( "scopeDeSerName", "grammarName")}
  this.modelPath = Log.errorIfNull(modelPath);
  this.fileExt = Log.errorIfNull(fileExt);
  this.scopeDeSer = new ${scopeDeSerName}();
