<#-- (c)  https://github.com/MontiCore/monticore -->

${tc.signature("call", "return", "parameters", "isBuilderClass", "hasSymbolReference", "attrName")}
     <#if isBuilderClass>
        ${call}(${parameters});
        return this.realBuilder;
     <#else>
     <#if hasSymbolReference>
       ${attrName}Map.clear();
     </#if>
     <#if return>return </#if>${call}(${parameters});
     </#if>