<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ast", "attributeName", "referencedProdName", "isOptional")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
<#if isOptional>
     if(!${attributeName}.isPresent() && ${attributeName?remove_ending("Symbol")}.isPresent() && getEnclosingScope() != null){
        return getEnclosingScope().resolve${referencedProdName}(${attributeName?remove_ending("Symbol")}.get()).get();
<#else>
     if(!${attributeName}.isPresent() && ${attributeName?remove_ending("Symbol")} != null && getEnclosingScope() != null){
        return getEnclosingScope().resolve${referencedProdName}(${attributeName?remove_ending("Symbol")}).get();
</#if>
     }
     if (${attributeName}.isPresent()) {
       return ${attributeName}.get();
     }
     Log.error("0xA7003${genHelper.getGeneratedErrorCode(ast)} ${attributeName} can't return a value. It is empty.");
     // Normally this statement is not reachable
     throw new IllegalStateException();
     