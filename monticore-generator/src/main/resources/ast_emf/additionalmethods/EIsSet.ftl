<#-- (c) https://github.com/MontiCore/monticore -->
 ${tc.signature("method", "ast", "grammarName", "fields")}
  <#assign genHelper = glex.getGlobalVar("astHelper")>
  <#assign nameHelper = glex.getGlobalVar("nameHelper")>
  <#assign packageName = grammarName + "Package">
    switch (featureID) {
    <#list fields as field>
      <#assign getter = astHelper.getPlainGetter(field)>
      case ${packageName}.${genHelper.getPlainName(ast)}_${genHelper.getNativeAttributeName(field.getName())?cap_first}:
      <#if genHelper.isListAstNode(field.getType())>
        return !${getter}().isEmpty();
      <#elseif genHelper.isOptional(field.getType())>
        return ${getter}().isPresent();
      <#else>  
        return ${getter}() != ${genHelper.getDefaultValue(field)};
      </#if>
    </#list>
    }
    return eDynamicIsSet(featureID);
