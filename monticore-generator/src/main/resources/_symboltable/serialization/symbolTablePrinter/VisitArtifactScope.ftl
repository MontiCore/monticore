<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("artifactScope")}
  printer.beginObject();
  printer.member(de.monticore.symboltable.serialization.JsonDeSers.KIND, "${artifactScope}");
  printer.member(de.monticore.symboltable.serialization.JsonDeSers.NAME, node.getName());
  if(node.getPackageName()!=""){
    printer.member(de.monticore.symboltable.serialization.JsonDeSers.PACKAGE, node.getPackageName());
  }
  if (node.isPresentSpanningSymbol()) {
    addScopeSpanningSymbol(node.getSpanningSymbol());
  }
  serializeLocalSymbols(node);
  serializeAdditionalScopeAttributes(node);