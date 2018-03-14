<#-- (c) https://github.com/MontiCore/monticore -->
  ${tc.signature("method", "clazz", "grammarName", "emfAttributes")}
  <#assign genHelper = glex.getGlobalVar("astHelper")>
    if (eIsProxy()) {
      return super.toString();
    }
    StringBuffer result = new StringBuffer(getClass().getSimpleName());
  <#list emfAttributes as emfAttribute>
    <#if emfAttribute.isOptional()>
    if (${emfAttribute.getAttributeName()}.isPresent()) {
      result.append(" ${astHelper.getPlainName(emfAttribute.getCdAttribute())}: ");
      result.append(${emfAttribute.getAttributeName()}.get());
    } 
    <#elseif emfAttribute.isAstList()>
    if (!${emfAttribute.getAttributeName()}.isEmpty()) {
      result.append(" ${astHelper.getPlainName(emfAttribute.getCdAttribute())}: ");
      result.append(${emfAttribute.getAttributeName()});
    } 
    <#else>
    result.append(" ${astHelper.getPlainName(emfAttribute.getCdAttribute())}: ");
    result.append(${emfAttribute.getAttributeName()});
    </#if>
  </#list>
    return result.toString();
