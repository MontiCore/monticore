<#-- (c) https://github.com/MontiCore/monticore -->
  ${tc.signature("method", "ast", "structuralMethod", "interfaces")}
  <#assign genHelper = glex.getGlobalVar("astHelper")>
  <#assign nameHelper = glex.getGlobalVar("nameHelper")>
  <#assign packageName = genHelper.getCdName() + "Package">
  <#list interfaces as interface>
    <#if genHelper.getVisibleFields(interface)?has_content>
  <#assign interfacePackageName = genHelper.getAstPackage(interface.getModelName()) + nameHelper.getSimpleName(interface.getModelName()) + "Package">
     if (baseClass == ${genHelper.getAstPackage(interface.getModelName())}${interface.getName()}.class) {
       switch (featureID) {
    <#list genHelper.getVisibleFields(interface) as cdAttribute>
         case ${packageName}.${genHelper.getPlainName(ast)}_${cdAttribute.getName()?cap_first}: return ${interfacePackageName}.${interface.getName()}_${cdAttribute.getName()?cap_first};
    </#list>
         default: return -1;
      }
    }
    </#if>  
  </#list>
    return super.${structuralMethod}(featureID, baseClass);
