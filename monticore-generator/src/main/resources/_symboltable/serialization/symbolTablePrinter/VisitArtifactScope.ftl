<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("artifactScope")}
  printer.beginObject();
  printer.member(de.monticore.symboltable.serialization.JsonConstants.KIND, "${artifactScope}");
  printer.member(de.monticore.symboltable.serialization.JsonConstants.NAME, node.getName());
  printer.member(de.monticore.symboltable.serialization.JsonConstants.PACKAGE, node.getPackageName());
  printer.member(de.monticore.symboltable.serialization.JsonConstants.EXPORTS_SYMBOLS, node.isExportingSymbols());
  printer.beginArray(de.monticore.symboltable.serialization.JsonConstants.IMPORTS);
  node.getImportList().forEach(x -> printer.value(x.toString()));
  printer.endArray();
  addScopeSpanningSymbol(node.getSpanningSymbolOpt());