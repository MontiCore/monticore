<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("artifactScope")}
  printer.beginObject();
  printer.member(de.monticore.symboltable.serialization.JsonDeSers.KIND, "${artifactScope}");
  printer.member(de.monticore.symboltable.serialization.JsonDeSers.NAME, node.getName());
  if(node.getPackageName()!=""){
    printer.member(de.monticore.symboltable.serialization.JsonDeSers.PACKAGE, node.getPackageName());
  }
  printer.member(de.monticore.symboltable.serialization.JsonDeSers.EXPORTS_SYMBOLS, node.isExportingSymbols());
  printer.beginArray(de.monticore.symboltable.serialization.JsonDeSers.IMPORTS);
  node.getImportList().forEach(x -> printer.value(x.toString()));
  printer.endArray();
  if (node.isPresentSpanningSymbol()) {
    addScopeSpanningSymbol(node.getSpanningSymbol());
  }
  serializeLocalSymbols(node);
  serializeAdditionalScopeAttributes(node);