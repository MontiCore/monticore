<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute", "naiveAttributeName", "generatedErrorCode")}
    if (isPresent${naiveAttributeName}()) {
        return this.${attribute.getName()}.get().get();
    }
    Log.warn("0xA7003${generatedErrorCode} get for ${naiveAttributeName} can't return a value. Attribute is empty.");
    return null;
