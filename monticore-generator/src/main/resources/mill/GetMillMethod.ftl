<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("millName")}
  if (mill == null) {
    // Soon: throw new IllegalStateException("0x70001: A mill was used before initialization");
    // For now: Only log a deprecation-warning to the info channel (to avoid creating findings)
    Log.info("0x70001: Warning: A mill was used before initialization. This access is deprecated!", "${millName}");
    mill = new ${millName}();
  }
  return mill;
