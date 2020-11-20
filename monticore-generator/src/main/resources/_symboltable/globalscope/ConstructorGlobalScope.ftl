<#-- (c) https://github.com/MontiCore/monticore */ -->
${tc.signature( "scopeDeSerName", "symbols2Json", "grammarName")}
  this.modelPath = Log.errorIfNull(modelPath);
  this.fileExt = Log.errorIfNull(fileExt);
  this.scopeDeSer = new ${scopeDeSerName}();
  this.symbols2Json = new ${symbols2Json}();
