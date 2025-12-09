<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolName")}

  if(!(obj instanceof ${symbolName})) {
    return false;
  }
  ${symbolName} s1 = getThis();
  ${symbolName} s2 = ((${symbolName}) obj).getThis();

  return s1 == s2;
