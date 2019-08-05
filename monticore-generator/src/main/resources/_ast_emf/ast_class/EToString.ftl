<#-- (c) https://github.com/MontiCore/monticore -->
  ${tc.signature("attributeList")}
  <#assign genHelper = glex.getGlobalVar("astHelper")>
    if (eIsProxy()) {
      return super.toString();
    }
    StringBuffer result = new StringBuffer(getClass().getSimpleName());
  <#list attributeList as attribute>
    <#if genHelper.isOptional(attribute.getMCType())>
    if (${attribute.getName()}.isPresent()) {
      result.append(" ${attribute.getName()?cap_first}: ");
      result.append(${attribute.getName()}.get());
    } 
    <#elseif genHelper.isListType(attribute.printType())>
    if (!${attribute.getName()}.isEmpty()) {
      result.append(" ${attribute.getName()?cap_first}: ");
      result.append(${attribute.getName()});
    } 
    <#else>
    result.append(" ${attribute.getName()?cap_first}: ");
    result.append(${attribute.getName()});
    </#if>
  </#list>
    return result.toString();
