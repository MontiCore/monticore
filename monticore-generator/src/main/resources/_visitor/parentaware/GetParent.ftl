<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("languageInterfaceName")}
  if (!parents.isEmpty()) {
    ${languageInterfaceName} topElement = (${languageInterfaceName}) parents.peek();
    return Optional.of(topElement);
  }
  // no parent, return an absent value
  return Optional.empty();