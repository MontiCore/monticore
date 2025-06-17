<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature()}
    if (mill != null) {
        // Soon: Log.error("0x70002: A mill was initialized without being reset first");
        // Soon: throw new IllegalStateException("0x70002: A mill was initialized without being reset first");
        // For now: Only log a warning to stderr
        new IllegalStateException("0x70002: Warning: A mill was initialized without being reset first").printStackTrace();
    }
    mill = a;