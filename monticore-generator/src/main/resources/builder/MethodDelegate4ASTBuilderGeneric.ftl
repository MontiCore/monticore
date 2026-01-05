<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute", "methodName", "paramCall")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
    ((${attribute.getMCType().printType()})this.${genHelper.getPlainGetter(attribute)}())<#if castType?has_content>)</#if>.${methodName}(${paramCall});
    return this.realBuilder;
