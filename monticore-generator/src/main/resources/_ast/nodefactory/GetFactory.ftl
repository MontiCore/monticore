<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className")}
  if (factory == null) {
    factory = new ${className}();
  }
  return factory;
