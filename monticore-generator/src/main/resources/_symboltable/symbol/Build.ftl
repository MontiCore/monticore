<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolClassName", "attributes")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
    ${symbolClassName} symbol = new ${symbolClassName}(name);
    <#list  attributes as attribute>
      <#if genHelper.isMandatory(attribute)>
      if (${attribute.getName()} == null) {
        Log.error("0xA7222 ${attribute.getName()} of type ${attribute.printType()} must not be null");
      }
      </#if>
      symbol.${genHelper.getPlainSetter(attribute)}(this.${attribute.getName()});
    </#list>
    return symbol;
