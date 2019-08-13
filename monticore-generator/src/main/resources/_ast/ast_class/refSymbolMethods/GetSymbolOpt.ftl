<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeName", "referencedProdName", "isOptional")}
<#if isOptional>
     if(!${attributeName}.isPresent() && ${attributeName?remove_ending("Symbol")}.isPresent() && getEnclosingScope() != null){
        return getEnclosingScope().resolve${referencedProdName}(${attributeName?remove_ending("Symbol")}.get());
<#else>
     if(!${attributeName}.isPresent() && ${attributeName?remove_ending("Symbol")} != null && getEnclosingScope() != null){
        return getEnclosingScope().resolve${referencedProdName}(${attributeName?remove_ending("Symbol")});
</#if>
     }
     return ${attributeName};