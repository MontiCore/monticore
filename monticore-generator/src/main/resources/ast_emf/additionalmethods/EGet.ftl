<#-- (c) https://github.com/MontiCore/monticore -->
  ${tc.signature("method", "ast", "grammarName", "fields")}
  <#assign genHelper = glex.getGlobalVar("astHelper")>
  <#assign nameHelper = glex.getGlobalVar("nameHelper")>
  <#assign packageName = grammarName + "Package">
    switch (featureID) {
    <#list fields as field>
      <#assign getter = astHelper.getPlainGetter(field)>
      case ${packageName}.${genHelper.getPlainName(ast)}_${genHelper.getNativeAttributeName(field.getName())?cap_first}:
      <#if genHelper.isOptional(field.getType())>
         return ${getter}().isPresent()? ${getter}().get() : null;
      <#else>
         return ${getter}();
      </#if>    
    </#list>
    }
    return eDynamicGet(featureID, resolve, coreType);
