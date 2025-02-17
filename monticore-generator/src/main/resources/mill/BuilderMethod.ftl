<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("astNodeName", "methodName")}
  if (mill${astNodeName}.get() == null) {
    mill${astNodeName}.set(getMill());
  }
  return mill${astNodeName}.get()._${methodName}();
