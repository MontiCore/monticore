<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeAttr")}
  de.monticore.symboltable.serialization.JsonPrinter printer = s2j.getJsonPrinter();
  printer.member(de.monticore.symboltable.serialization.JsonDeSers.IS_SHADOWING_SCOPE, toSerialize.isShadowing());
<#list scopeAttr as attr>
  serialize${attr?cap_first}(toSerialize, s2j);
</#list>
  return printer.toString();
