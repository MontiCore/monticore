<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute", "referencedSymbol", "symbolName")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
     if(!${attribute.getName()}Definition.isPresent()){
       if ((${attribute.getName()} != null) && isPresentEnclosingScope()) {
<#if genHelper.isOptional(attribute)>
         Optional<${referencedSymbol}> symbol = enclosingScope.get().resolve(${attribute.getName()}.get(), ${referencedSymbol}.KIND);
<#else>
         Optional<${referencedSymbol}> symbol = enclosingScope.get().resolve(${attribute.getName()}, ${referencedSymbol}.KIND);
</#if>
         ${attribute.getName()}Definition = symbol.get().get${symbolName}Node();
       }
     }
     return ${attribute.getName()}Definition;