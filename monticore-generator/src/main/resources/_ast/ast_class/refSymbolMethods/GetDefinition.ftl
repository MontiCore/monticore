<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeName", "referencedSymbolType")}
<#assign service = glex.getGlobalVar("service")>
    if (isPresent${attributeName?cap_first}Definition()) {
        return get${attributeName?cap_first}Symbol().getAstNode();
    }
    Log.error("0xA7003${service.getGeneratedErrorCode(attributeName+referencedSymbolType)} ${attributeName}Definition can't return a value. It is empty.");
    // Normally this statement is not reachable
    throw new IllegalStateException();