<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeName", "referencedSymbol", "isOptional")}
<#if isOptional>
     if(!${attributeName}Symbol.isPresent() && ${attributeName}.isPresent() && isPresentEnclosingScope()){
        return  enclosingScope.get().resolve(${attributeName}.get(), ${referencedSymbol}.KIND);
<#else >
     if(!${attributeName}Symbol.isPresent() && ${attributeName} != null && isPresentEnclosingScope()){
        return  enclosingScope.get().resolve(${attributeName}, ${referencedSymbol}.KIND);
</#if>
     }
     return ${attributeName}Symbol;