<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("astNodeName")}
  {
    if (factory${astNodeName} == null) {
      factory${astNodeName} = getFactory();
    }
    return factory${astNodeName}.doCreate${astNodeName}();
  }
