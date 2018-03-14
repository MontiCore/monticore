<#-- (c)  https://github.com/MontiCore/monticore -->

${tc.signature("d1", "ast", "call", "return", "parameters", "isBuilderClass")}
     <#if isBuilderClass>
        ${call}(${parameters});
        return this.realBuilder;
     <#else>
     <#if return>return </#if>${call}(${parameters});
     </#if>