<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute", "methodName", "paramCall")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
    this.${genHelper.getPlainGetter(attribute)}().${methodName}(${paramCall});
    return this.realBuilder;
