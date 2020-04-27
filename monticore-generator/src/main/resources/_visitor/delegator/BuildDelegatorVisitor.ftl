<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("visitorName", "attributes")}
<#assign genHelper = glex.getGlobalVar("astHelper")>

if (!isValid()) {
<#list attributes as attribute>
  if (${attribute.getName()} == null) {
    Log.error("0xA7222 ${attribute.getName()} of type ${attribute.printType()} must not be null");
  }
</#list>
  throw new IllegalStateException();
}

${visitorName} value = new ${visitorName}();
<#list  attributes as attribute>
<#assign setter = genHelper.getPlainSetter(attribute)>
<#if genHelper.isOptional(attribute.getMCType())>
  if (this.${attribute.getName()}.isPresent()) {
    value.${setter}(this.${attribute.getName()}.get());
  }
<#else>
  value.${setter}(this.${attribute.getName()});
</#if>
</#list>
return value;
