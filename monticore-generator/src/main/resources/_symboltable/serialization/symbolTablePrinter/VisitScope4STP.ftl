<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeFullName")}
  printer.beginObject();
  printer.member(de.monticore.symboltable.serialization.JsonDeSers.KIND, "${scopeFullName}");
  printer.member(de.monticore.symboltable.serialization.JsonDeSers.NAME, node.getName());
  printer.member(de.monticore.symboltable.serialization.JsonDeSers.IS_SHADOWING_SCOPE, node.isShadowing());
  printer.member(de.monticore.symboltable.serialization.JsonDeSers.EXPORTS_SYMBOLS, node.isExportingSymbols());
  if (node.isPresentSpanningSymbol()) {
    addScopeSpanningSymbol(node.getSpanningSymbol());
  }
  serializeLocalSymbols(node);
  serializeAdditionalScopeAttributes(node);