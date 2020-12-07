<#-- (c) https://github.com/MontiCore/monticore -->
if(node.getSpannedScope().isExportingSymbols() && node.getSpannedScope().getSymbolsSize() > 0) {
  printer.beginObject(de.monticore.symboltable.serialization.JsonDeSers.SPANNED_SCOPE);
  node.getSpannedScope().accept(getTraverser());
}