<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeInterface")}
  List<${scopeInterface}> subScopes = filterRelevantSubScopes(node.getSubScopes());
  if (!subScopes.isEmpty()) {
    printer.beginArray(de.monticore.symboltable.serialization.JsonDeSers.SUBSCOPES);
    subScopes.stream().forEach(s -> s.accept(getRealThis()));
    printer.endArray();
  }