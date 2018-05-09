<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute", "symbolClass")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
   if ((${attribute.getName()} != null) && isPresentEnclosingScope()) {
<#if genHelper.isOptional(attribute)>
     return enclosingScope.get().resolve(${attribute.getName()}.get(), ${symbolClass}.KIND);
<#else>
     return enclosingScope.get().resolve(${attribute.getName()}, ${symbolClass}.KIND);
</#if>
     }
     return Optional.empty();