<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("kinds")}
  printer.beginArray(de.monticore.symboltable.serialization.JsonDeSers.KIND_HIERARCHY);
  <#list kinds?keys as kind>printer.array(Lists.newArrayList( ${kind}, ${kinds[kind]}), Function.identity()); </#list>
  printer.endArray();
