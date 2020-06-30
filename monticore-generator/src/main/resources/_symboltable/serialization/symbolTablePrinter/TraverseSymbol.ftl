<#-- (c) https://github.com/MontiCore/monticore -->
if(node.getSpannedScope().isExportingSymbols() && node.getSpannedScope().getSymbolsSize() > 0) {
  printer.beginArray(de.monticore.symboltable.serialization.JsonDeSers.SUBSCOPES);
  node.getSpannedScope().accept(getRealThis());
  printer.endArray();
}