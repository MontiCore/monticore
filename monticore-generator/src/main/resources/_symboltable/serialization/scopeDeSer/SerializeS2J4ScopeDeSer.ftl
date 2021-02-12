<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeAttr")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
  de.monticore.symboltable.serialization.JsonPrinter printer = s2j.getJsonPrinter();
  if(toSerialize.isShadowing()) {
    printer.member(de.monticore.symboltable.serialization.JsonDeSers.IS_SHADOWING_SCOPE, toSerialize.isShadowing());
  }
<#list scopeAttr as attr>
  <#if astHelper.isOptional(attr.getMCType())>
  if (toSerialize.isPresent${attr.getName()?cap_first}()) {
    serialize${attr.getName()?cap_first}(Optional.of(toSerialize.${genHelper.getPlainGetter(attr)}()), s2j);
  }
  <#else>
  serialize${attr.getName()?cap_first}(toSerialize.${genHelper.getPlainGetter(attr)}(), s2j);
  </#if>
</#list>
  return printer.toString();
