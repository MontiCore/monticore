<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("kinds")}
  printer.beginArray(de.monticore.symboltable.serialization.JsonDeSers.KIND_HIERARCHY);
<#list kinds?keys as kind>
  de.monticore.symboltable.serialization.JsonDeSers.printKindHierarchyEntry(printer,
      "${kind}",
      "${kinds[kind]}");
</#list>
  printer.endArray();
