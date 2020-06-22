<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolReferenceName", "attributeList")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
  ${symbolReferenceName} symbolReference = new ${symbolReferenceName}(name);
  symbolReference.setEnclosingScope(enclosingScope);
<#list attributeList as attribute>
  <#assign methName = genHelper.getNativeAttributeName(attribute.getName())?cap_first>
  <#if genHelper.isListType(attribute.printType())>
    symbolReference.set${methName?remove_ending("s")}List(this.${attribute.getName()});
  <#elseif genHelper.isOptional(attribute.getMCType())>
    if (this.${attribute.getName()}.isPresent()) {
      symbolReference.set${methName}(this.${attribute.getName()}.get());
    } else {
      symbolReference.set${methName}Absent();
    }
  <#else>
    symbolReference.set${methName}(this.${attribute.getName()});
  </#if>
</#list>
  return symbolReference;