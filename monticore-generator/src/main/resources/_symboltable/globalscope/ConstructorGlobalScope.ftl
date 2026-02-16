<#-- (c) https://github.com/MontiCore/monticore */ -->
${tc.signature()}
  this.symbolPath = Preconditions.checkNotNull(symbolPath);
  this.fileExt = Preconditions.checkNotNull(fileExt);
  init();
