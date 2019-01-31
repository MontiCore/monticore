<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domainClass", "mandatoryAttributes")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
if (!isValid()) {
<#list mandatoryAttributes as attribute>
    if (${attribute.getName()} == null) {
        Log.error("0xA7222${genHelper.getGeneratedErrorCode(attribute)} ${attribute.getName()} of type ${attribute.printType()} must not be null");
    }
</#list>
}
${domainClass.getName()} value = new ${domainClass.getName()}();
<#list domainClass.getCDAttributeList() as attribute>
value.set${attribute.getName()?cap_first}(this.${attribute.getName()});
</#list>
return value;