${tc.signature("attribute")}
<#assign attributeName = attribute.getName()>
<#assign genHelper = glex.getGlobalVar("astHelper")>
if (isPresent${attributeName?cap_first}()) {
    return this.${attributeName}.get();
}
Log.error("0xA7003${genHelper.getGeneratedErrorCode(attribute)} ${attributeName?cap_first} can't return a value. It is empty.");
// Normally this statement is not reachable
throw new IllegalStateException();