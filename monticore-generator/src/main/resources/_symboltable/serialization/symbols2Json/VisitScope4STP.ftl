<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbols2Json")}
if (node.isPresentSpanningSymbol() && node.isExportingSymbols()) {
  if(getJsonPrinter().toString().isEmpty()){
    getJsonPrinter().beginObject();
  } else{
    getJsonPrinter().beginObject(de.monticore.symboltable.serialization.JsonDeSers.SPANNED_SCOPE);
  }
  scopeDeSer.serialize(node, getRealThis());
  getJsonPrinter().beginArray(de.monticore.symboltable.serialization.JsonDeSers.SYMBOLS);
}
