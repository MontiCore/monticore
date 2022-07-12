<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbols2Json")}
  getJsonPrinter().beginObject();
  scopeDeSer.serialize(node, getRealThis());
  getJsonPrinter().beginArray(de.monticore.symboltable.serialization.JsonDeSers.SYMBOLS);

