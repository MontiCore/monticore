<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("rteScope", "symbols2Json")}
  getJsonPrinter().endArray();
  scopeDeSer.serializeAddons((${rteScope}) node, (${symbols2Json}) this);
  getJsonPrinter().endObject();

