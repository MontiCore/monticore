<#-- (c) https://github.com/MontiCore/monticore -->
  if (this.exportsSymbols()) {
    final String symbolQualifier = de.se_rwth.commons.Names.getQualifier(symbolName);

    final List<String> symbolQualifierParts = de.se_rwth.commons.Splitters.DOT.splitToList(symbolQualifier);
    final List<String> packageParts = de.se_rwth.commons.Splitters.DOT.splitToList(packageName);

    boolean symbolNameStartsWithPackage = true;

    if (packageName.isEmpty()) {
      // symbol qualifier always contains default package (i.e., empty string)
      symbolNameStartsWithPackage = true;
    } else if (symbolQualifierParts.size() >= packageParts.size()) {
      for (int i = 0; i < packageParts.size(); i++) {
        if (!packageParts.get(i).equals(symbolQualifierParts.get(i))) {
          symbolNameStartsWithPackage = false;
          break;
        }
      }
    } else {
      symbolNameStartsWithPackage = false;
    }
      return symbolNameStartsWithPackage;
  }
  return false;