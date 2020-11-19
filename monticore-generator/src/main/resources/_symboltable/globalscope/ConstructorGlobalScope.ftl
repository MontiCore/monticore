<#-- (c) https://github.com/MontiCore/monticore */ -->
${tc.signature( "scopeDeSerName", "symbols2Json", "grammarName")}
  this.modelPath = Log.errorIfNull(modelPath);
  this.modelFileExtension = Log.errorIfNull(modelFileExtension);
  this.scopeDeSer = new ${scopeDeSerName}();
  this.symbols2Json = new ${symbols2Json}();
