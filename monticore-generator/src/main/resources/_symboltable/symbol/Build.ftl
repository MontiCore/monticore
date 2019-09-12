<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolClassName", "attributes")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
    ${symbolClassName} symbol = new ${symbolClassName}(name);
    <#list  attributes as attribute>
      symbol.${genHelper.getPlainSetter(attribute)}(this.${attribute.getName()});
    </#list>
    return symbol;
