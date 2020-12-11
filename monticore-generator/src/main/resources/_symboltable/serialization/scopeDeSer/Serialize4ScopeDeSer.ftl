<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeAttr")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
  de.monticore.symboltable.serialization.JsonPrinter printer = s2j.getJsonPrinter();
  printer.member(de.monticore.symboltable.serialization.JsonDeSers.IS_SHADOWING_SCOPE, toSerialize.isShadowing());
<#list scopeAttr as attr>
  serialize${attr.name?cap_first}(toSerialize.${genHelper.getPlainGetter(attr)}(), s2j);
</#list>
  return printer.toString();
