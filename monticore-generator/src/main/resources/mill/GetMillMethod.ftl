<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("millName")}
  if (mill == null) {
    // Soon: throw new IllegalStateException("0x70001: A mill was used before initialization");
    // For now: Only log a deprecation-warning to std err (to avoid creating findings)
    System.err.println("0x70001: Warning: The ${millName} was used before initialization. This access is deprecated!");
    mill = new ${millName}();
  }
  return mill;
