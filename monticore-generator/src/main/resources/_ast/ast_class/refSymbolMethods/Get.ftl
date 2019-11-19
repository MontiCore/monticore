<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute", "referencedSymbol")}
<#assign attributeName = attribute.getName()>
<#assign genHelper = glex.getGlobalVar("astHelper")>
   if (isPresent${attributeName?cap_first}()) {
     return get${attributeName?cap_first}();
   }
   Log.error("0xA7003${genHelper.getGeneratedErrorCode(attribute)} get${attributeName?cap_first} can't return a value. It is empty.");
   // Normally this statement is not reachable
   throw new IllegalStateException();