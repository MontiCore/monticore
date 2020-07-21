<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeFullName", "languageName", "attrList")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
  if(!printer.isInObject()){
    printer.beginObject();
  }
  printer.member(de.monticore.symboltable.serialization.JsonDeSers.IS_SHADOWING_SCOPE, node.isShadowing());
<#list attrList as attr>
  <#if genHelper.isOptional(attr.getMCType())>
  if (node.isPresent${attr.getName()?cap_first}()) {
    serialize${languageName}Scope${attr.getName()?cap_first}(Optional.of(node.${genHelper.getPlainGetter(attr)}()));
  }
  <#else>
  serialize${languageName}Scope${attr.getName()?cap_first}(node.${genHelper.getPlainGetter(attr)}());
  </#if>
</#list>
  serializeAdditionalScopeAttributes(node);
  printer.beginArray(de.monticore.symboltable.serialization.JsonDeSers.SYMBOLS);
