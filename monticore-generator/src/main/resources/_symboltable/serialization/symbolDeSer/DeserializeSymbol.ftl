<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolBuilderFullName","symbolBuilderSimpleName", "symTabMill", "symbolName")}
  ${symbolBuilderFullName} builder = ${symTabMill}.${symbolBuilderSimpleName?uncap_first}();
  builder.setName(symbolJson.get(de.monticore.symboltable.serialization.JsonConstants.NAME).getAsJsonString().getValue());
  ${symbolName} symbol = builder.build();
  deserializeAdditionalAttributes(symbol, symbolJson);
  return symbol;