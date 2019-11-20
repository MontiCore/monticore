<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeName", "referencedProdName", "isOptional")}
<#if isOptional>
     if(!${attributeName}.isPresent() && ${attributeName?remove_ending("Symbol")}.isPresent() && getEnclosingScope() != null){
        getEnclosingScope().resolve${referencedProdName}(${attributeName?remove_ending("Symbol")}.get());
        return true;
<#else>
     if(!${attributeName}.isPresent() && ${attributeName?remove_ending("Symbol")} != null && getEnclosingScope() != null){
        getEnclosingScope().resolve${referencedProdName}(${attributeName?remove_ending("Symbol")});
        return true;
</#if>
     }
     return false;
     