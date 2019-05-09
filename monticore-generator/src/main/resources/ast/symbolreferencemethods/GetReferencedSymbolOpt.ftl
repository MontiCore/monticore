<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeName", "symbolName", "isOptional")}
<#assign genHelper = glex.getGlobalVar("stHelper")>
<#if isOptional>
     if(!${attributeName}Symbol.isPresent() && ${attributeName}.isPresent() && isPresentEnclosingScope()){
        return ((${genHelper.getQualifiedScopeInterfaceType(genHelper.getCdSymbol())}) enclosingScope.get()).resolve${symbolName}(${attributeName}.get());
<#else >
     if(!${attributeName}Symbol.isPresent() && ${attributeName} != null && isPresentEnclosingScope()){
        return ((${genHelper.getQualifiedScopeInterfaceType(genHelper.getCdSymbol())}) enclosingScope.get()).resolve${symbolName}(${attributeName});
</#if>
     }
     return ${attributeName}Symbol;