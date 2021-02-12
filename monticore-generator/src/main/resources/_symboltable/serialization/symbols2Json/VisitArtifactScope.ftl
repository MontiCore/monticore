<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbols2Json")}
  getJsonPrinter().beginObject();
  scopeDeSer.serialize((de.monticore.symboltable.IArtifactScope) node, (${symbols2Json}) this);
  getJsonPrinter().beginArray(de.monticore.symboltable.serialization.JsonDeSers.SYMBOLS);

