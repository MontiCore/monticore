<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeName", "errorCode")}
  if (this instanceof ${scopeName}) {
    visitor.handle((${scopeName}) this);
  } else {
    throw new java.lang.UnsupportedOperationException("${errorCode} Only handwritten class ${scopeName} is supported for the visitor");
  }