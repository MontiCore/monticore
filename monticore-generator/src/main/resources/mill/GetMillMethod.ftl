<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("millName")}
  if (mill == null) {
    mill = new ${millName}();
  }
  return mill;