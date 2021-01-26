<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbols2Json")}
  getJsonPrinter().endArray();
  scopeDeSer.serializeAddons(node, (${symbols2Json}) this);
  getJsonPrinter().endObject();

