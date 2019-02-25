<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute", "referencedSymbol")}
<#assign attributeName = attribute.getName()>
<#assign genHelper = glex.getGlobalVar("astHelper")>
   if (get${attributeName?cap_first}Opt().isPresent()) {
     return get${attributeName?cap_first}Opt().get();
   }
   Log.error("0xA7003${genHelper.getGeneratedErrorCode(attribute)} get${attributeName?cap_first}Opt() can't return a value. It is empty.");
   // Normally this statement is not reachable
   throw new IllegalStateException();