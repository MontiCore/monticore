<#-- (c) https://github.com/MontiCore/monticore -->
if(node.getSpannedScope().isExportingSymbols() && node.getSpannedScope().getSymbolsSize() > 0) {
  isSpannedScope = true;
  node.getSpannedScope().accept(getRealThis());
}