<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolFullName", "symbolProdName", "attrList")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
  printer.beginObject();
  // Name and kind are part of every serialized symbol
  printer.member(de.monticore.symboltable.serialization.JsonDeSers.KIND, "${symbolFullName}");
  printer.member(de.monticore.symboltable.serialization.JsonDeSers.NAME, node.getName());
  // Serialize all relevant additional attributes (introduced by symbolRules)
<#list attrList as attr>
  <#if genHelper.isOptional(attr.getMCType())>
  if (node.isPresent${attr.getName()?cap_first}()) {
    serialize${symbolProdName}${attr.getName()?cap_first}(Optional.of(node.${genHelper.getPlainGetter(attr)}()));
  }
  <#else>
  serialize${symbolProdName}${attr.getName()?cap_first}(node.${genHelper.getPlainGetter(attr)}());
  </#if>
</#list>
serializeAdditional${symbolProdName}SymbolAttributes(node);