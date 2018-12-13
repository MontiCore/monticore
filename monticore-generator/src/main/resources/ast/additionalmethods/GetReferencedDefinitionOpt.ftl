<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute", "referencedSymbol", "symbolName")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
<#if genHelper.isOptional(attribute)>
     if(!${attribute.getName()}Definition.isPresent() && ${attribute.getName()}.isPresent()&& isPresentEnclosingScope()){
         Optional<${referencedSymbol}> symbol = enclosingScope.get().resolve(${attribute.getName()}.get(), ${referencedSymbol}.KIND);
<#else>
     if(!${attribute.getName()}Definition.isPresent() && ${attribute.getName()} != null && isPresentEnclosingScope()){
         Optional<${referencedSymbol}> symbol = enclosingScope.get().resolve(${attribute.getName()}, ${referencedSymbol}.KIND);
</#if>
         ${attribute.getName()}Definition = symbol.get().get${symbolName}Node();
     }
     return ${attribute.getName()}Definition;