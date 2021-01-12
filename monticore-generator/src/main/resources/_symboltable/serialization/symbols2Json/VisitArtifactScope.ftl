<#-- (c) https://github.com/MontiCore/monticore -->
  getJsonPrinter().beginObject();
  scopeDeSer.serialize(node, this);
  getJsonPrinter().beginArray(de.monticore.symboltable.serialization.JsonDeSers.SYMBOLS);

