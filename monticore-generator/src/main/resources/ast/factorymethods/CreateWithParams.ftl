<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("method", "ast", "astNodeName", "paramCall")}
  {
    if (factory${astNodeName} == null) {
      factory${astNodeName} = getFactory();
    }
    return factory${astNodeName}.doCreate${astNodeName}(${paramCall});
  }
