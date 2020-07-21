<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("astNodeName", "methodName")}
  if (mill${astNodeName} == null) {
    mill${astNodeName} = getMill();
  }
  return mill${astNodeName}._${methodName}();
