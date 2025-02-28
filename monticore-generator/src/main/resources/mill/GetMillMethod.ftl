<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("millName")}
  if (mill.get() == null) {
    mill.set(new ${millName}());
  }
  return mill.get();