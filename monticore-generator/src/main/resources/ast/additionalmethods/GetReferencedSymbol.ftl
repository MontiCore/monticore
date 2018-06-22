<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeName")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
<#assign symbol = attributeName + "Symbol">
    if (get${symbol?cap_first}Opt().isPresent()) {
      return get${symbol?cap_first}Opt().get();
    }
    Log.error("0xA7003${genHelper.getGeneratedErrorCode(ast)} get${symbol?cap_first}Opt() can't return a value. It is empty.");
    // Normally this statement is not reachable
    throw new IllegalStateException();