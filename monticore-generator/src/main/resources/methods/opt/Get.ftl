${tc.signature("attribute", "naiveAttributeName")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
        if (isPresent${naiveAttributeName}()) {
            return this.get${naiveAttributeName}Opt().get();
        }
        Log.error("0xA7003${genHelper.getGeneratedErrorCode(attribute)} ${naiveAttributeName} can't return a value. It is empty.");
        // Normally this statement is not reachable
        throw new IllegalStateException();