<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolReferenceName")}
  ${symbolReferenceName} symbolReference = new ${symbolReferenceName}(name);
  symbolReference.setEnclosingScope(enclosingScope);
  return symbolReference;