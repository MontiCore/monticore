<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute", "naiveAttributeName", "generatedErrorCode")}
    if (isPresent${naiveAttributeName}()) {
        return this.${attribute.getName()}.get();
    }
    Log.error("0xA7003${generatedErrorCode} get for ${naiveAttributeName} can't return a value. Attribute is empty.");
    // Normally this statement is not reachable
    throw new IllegalStateException();
