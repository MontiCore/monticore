<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeName", "referencedSymbol")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
<#assign definition = attributeName + "Definition">
   if (get${definition?cap_first}Opt().isPresent()) {
     return get${definition?cap_first}Opt().get();
   }
   Log.error("0xA7003${genHelper.getGeneratedErrorCode(ast)} get${definition?cap_first}Opt() can't return a value. It is empty.");
   // Normally this statement is not reachable
   throw new IllegalStateException();