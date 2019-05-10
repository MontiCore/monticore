<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeName", "referencedProdName","scopeInterface",  "isOptional")}
<#if isOptional>
     if(!${attributeName}.isPresent() && ${attributeName}.isPresent() && isPresentEnclosingScope()){
        return  ( (${scopeInterface}) enclosingScope.get()).resolve${referencedProdName}(${attributeName?remove_ending("Symbol")}.get());
<#else>
     if(!${attributeName}.isPresent() && ${attributeName} != null && isPresentEnclosingScope()){
        return  ( (${scopeInterface}) enclosingScope.get()).resolve${referencedProdName}(${attributeName?remove_ending("Symbol")});
</#if>
     }
     return ${attributeName};