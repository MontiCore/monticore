<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature()}
    if (mill != null) {
        // Soon: Log.error("0x70002: Warning: This mill is being initialized multiple times - Ensure to reset Mills after tests/tools/etc");
        // Soon: throw new IllegalStateException("0x70002: Warning: This mill is being initialized multiple times - Ensure to reset Mills after tests/tools/etc.");
        // For now: Only log a warning to log#debug
        Log.debug("0x70002: Warning: This mill is being initialized multiple times - Ensure to reset Mills after tests/tools/etc.", "Mill0x70002");
        // Do not print this warning until "https://git.rwth-aachen.de/monticore/monticore/-/issues/4687" has been decided
        // and a solution to this problem (of multi-mill-initialization) can be proposed
    }
    mill = a;