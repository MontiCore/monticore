<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("method", "ast", "attributeName", "symbolClass")}
  if ((${attributeName} != null) && isPresentEnclosingScope()) {
    return enclosingScope.get().resolve(${attributeName}, ${symbolClass}.KIND);
  }

  return Optional.empty();
