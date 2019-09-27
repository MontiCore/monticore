<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("plainName", "errorCode")}
  if (this instanceof ${plainName}) {
    visitor.handle((${plainName}) this);
  } else {
    throw new UnsupportedOperationException("0xA7010${errorCode} Only handwritten class ${plainName} is supported for the visitor");
  }