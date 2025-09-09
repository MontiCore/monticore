<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("millName")}
  if (mill == null) {
    // Soon: Log.error("0x70001: A mill was used before initialization");
    // Soon: throw new IllegalStateException("0x70001: A mill was used before initialization");
    // For now: Only log a warning to stderr
    new IllegalStateException("0x70001: Warning: A mill was used before initialization. This access is deprecated!").printStackTrace();
    mill = new ${millName}();
  }
  return mill;
