<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("artifactScopeFullName", "languageName", "attrList" )}
<#assign genHelper = glex.getGlobalVar("astHelper")>
  printer.beginObject();
  printer.member(de.monticore.symboltable.serialization.JsonDeSers.KIND, "${artifactScopeFullName}");
  printer.member(de.monticore.symboltable.serialization.JsonDeSers.NAME, node.getName());
  if(!node.getPackageName().isEmpty()) {
    printer.member(de.monticore.symboltable.serialization.JsonDeSers.PACKAGE, node.getPackageName());
  }
<#list attrList as attr>
  <#if genHelper.isOptional(attr.getMCType())>
  if (node.isPresent${attr.getName()?cap_first}()) {
    serialize${languageName}${attr.getName()?cap_first}(Optional.of(node.${genHelper.getPlainGetter(attr)}()));
  }
  <#else>
  serialize${languageName}${attr.getName()?cap_first}(node.${genHelper.getPlainGetter(attr)}());
  </#if>
</#list>
  serializeAdditionalArtifactScopeAttributes(node);
  serializeLocalSymbols(node);
