<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolClassName", "attributes")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
${symbolClassName} symbol = new ${symbolClassName}(name);
<#list  attributes as attribute>
<#assign setter = genHelper.getPlainSetter(attribute)>
<#if genHelper.isOptional(attribute.getMCType())>
  if (this.${attribute.getName()}.isPresent()) {
    symbol.${setter}(this.${attribute.getName()}.get());
  } else {
    symbol.${setter}Absent();
  }
<#else>
  symbol.${setter}(this.${attribute.getName()});
</#if>
</#list>
return symbol;
