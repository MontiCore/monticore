<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature()}
  de.monticore.symboltable.serialization.JsonPrinter printer = symbols2Json.getJsonPrinter();
  printer.member(de.monticore.symboltable.serialization.JsonDeSers.IS_SHADOWING_SCOPE, toSerialize.isShadowing());

  // serialize scoperule attributes
  return printer.toString();
