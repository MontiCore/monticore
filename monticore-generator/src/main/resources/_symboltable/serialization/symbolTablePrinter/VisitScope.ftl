<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeFullName")}
  printer.beginObject();
  printer.member(de.monticore.symboltable.serialization.JsonConstants.KIND, "${scopeFullName}");
  printer.member(de.monticore.symboltable.serialization.JsonConstants.NAME, node.getName());
  printer.member(de.monticore.symboltable.serialization.JsonConstants.IS_SHADOWING_SCOPE, node.isShadowing());
  printer.member(de.monticore.symboltable.serialization.JsonConstants.EXPORTS_SYMBOLS, node.isExportingSymbols());
  addScopeSpanningSymbol(node.getSpanningSymbolOpt());
  serializeLocalSymbols(node);