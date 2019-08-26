<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("visitorTypeName", "scopeName", "superVisitorTypeName", "errorCode")}
  if (visitor instanceof ${visitorTypeName}) {
    accept((${visitorTypeName}) visitor);
  } else {
    throw new UnsupportedOperationException("0xA7010${errorCode} Scope node type ${scopeName} expected a visitor of type ${visitorTypeName}, but got ${superVisitorTypeName}.");
  }