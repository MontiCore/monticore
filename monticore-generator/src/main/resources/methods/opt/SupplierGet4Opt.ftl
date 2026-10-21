<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute", "naiveAttributeName", "generatedErrorCode")}
    if (isPresent${naiveAttributeName}()) {
        return this.${attribute.getName()}.get().get();
    }
    Log.info("0xA7003${generatedErrorCode} get for ${naiveAttributeName} can't return a value. Attribute is empty.", "get${attribute.getName()}");
    return null;
