<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute", "methodName", "paramCall","castType")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
    <#if castType?has_content>((${castType})</#if>this.${genHelper.getPlainGetter(attribute)}()<#if castType?has_content>)</#if>.${methodName}(${paramCall});
    return this.realBuilder;
