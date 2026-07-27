<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("allAttributes")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
<#list allAttributes as attribute>
  <#assign attributeName = attribute.getName()>
  <#assign methName = genHelper.getNativeAttributeName(attribute.getName())?cap_first>
  <#assign astChildTypeName = genHelper.getNativeTypeName(attribute.getMCType())>
  <#if genHelper.isSimpleAstNode(attribute)>
  // replacing ${attributeName}
  if (this.${attributeName} == currentChild) {
    set${methName}((${genHelper.getNativeTypeName(attribute.getMCType())}) replacement);
  }
  <#elseif genHelper.isOptionalAstNode(attribute)>
  // replacing ${attributeName}
  if (isPresent${methName}() && get${methName}() == currentChild) {
    <#assign astChildTypeNameNoOptional = astChildTypeName?replace("Optional<", "")?replace(">", "")>
    set${methName}((${astChildTypeName}) replacement);
  }
  <#elseif genHelper.isListAstNode(attribute)>
  // replacing ${attributeName}
  for (int i = 0; i < this.${attributeName}.size(); i++) {
    if (this.${attributeName}.get(i) == currentChild) {
      this.${attributeName}.set(i, (${astChildTypeName}) replacement);
    }
  }
  <#else>
  // not replacing attribute ${attributeName}
  </#if>
</#list>