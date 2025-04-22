<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("millName")}
  if (mill == null) {
    Log.error("0x70001: A mill was used before initialization");
    throw new IllegalStateException("0x70001: A mill was used before initialization");
  }
  return mill;