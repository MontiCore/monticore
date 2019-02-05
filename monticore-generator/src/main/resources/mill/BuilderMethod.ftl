<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("astNodeName")}
  if (mill${astNodeName} == null) {
    mill${astNodeName} = getMill();
  }
  return mill${astNodeName}._${astNodeName?uncap_first}Builder();
