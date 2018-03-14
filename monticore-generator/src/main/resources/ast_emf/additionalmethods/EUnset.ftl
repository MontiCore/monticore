<#-- (c) https://github.com/MontiCore/monticore -->
 ${tc.signature("method", "ast", "grammarName", "fields")}
  <#assign genHelper = glex.getGlobalVar("astHelper")>
  <#assign nameHelper = glex.getGlobalVar("nameHelper")>
  <#assign packageName = grammarName + "Package">
    switch (featureID) {
    <#list fields as field>
      <#assign getter = astHelper.getPlainGetter(field)>
      case ${packageName}.${genHelper.getPlainName(ast)}_${genHelper.getNativeAttributeName(field.getName())?cap_first}:
      <#if genHelper.isListAstNode(field)>
        ${field.getName()}.clear();
      <#elseif genHelper.isOptional(field.getType())>
        ${astHelper.getPlainSetter(field)}(null);
      <#else>
        <#-- TODO GV: not optionals! -->
        ${astHelper.getPlainSetter(field)}(${genHelper.getDefaultValue(field)});
      </#if>
      return;
    </#list>
    }
    eDynamicUnset(featureID);
