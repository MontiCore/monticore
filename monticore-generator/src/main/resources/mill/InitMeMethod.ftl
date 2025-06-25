<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature()}
    if (mill != null) {
        // Soon: Log.error("0x70002: A mill was initialized without being reset first");
        // Soon: throw new IllegalStateException("0x70002: Warning: This mill is being initialized multiple times - Ensure to reset Mills after tests/tools/etc.");
        // For now: Only log a warning to stderr
        new IllegalStateException("0x70002: Warning: This mill is being initialized multiple times - Ensure to reset Mills after tests/tools/etc.").printStackTrace();
    }
    mill = a;