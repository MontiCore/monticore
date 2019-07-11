<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeName", "referencedProdName", "isOptional")}
<#if isOptional>
     if(!${attributeName}.isPresent() && ${attributeName?remove_ending("Symbol")}.isPresent() && getEnclosingScope2() != null){
        return getEnclosingScope2().resolve${referencedProdName}(${attributeName?remove_ending("Symbol")}.get());
<#else>
     if(!${attributeName}.isPresent() && ${attributeName?remove_ending("Symbol")} != null && getEnclosingScope2() != null){
        return getEnclosingScope2().resolve${referencedProdName}(${attributeName?remove_ending("Symbol")});
</#if>
     }
     return ${attributeName};