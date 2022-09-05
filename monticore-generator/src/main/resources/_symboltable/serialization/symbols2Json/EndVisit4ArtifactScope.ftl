<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("rteScope", "symbols2Json")}
  getJsonPrinter().endArray();
  scopeDeSer.serializeAddons(node, getRealThis());
  getJsonPrinter().endObject();

