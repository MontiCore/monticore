<#-- (c) https://github.com/MontiCore/monticore -->
  List<String> remainingSymbolNames = new ArrayList<>();
  String packageAS = this.getPackageName();
  if(symbolName.startsWith(packageAS)){
    if(!packageAS.equals("")){
      symbolName = symbolName.substring(packageAS.length()+1);
    }
    String asName = this.getName() + ".";
    remainingSymbolNames.add(symbolName);
    if(symbolName.startsWith(asName)){
      symbolName = symbolName.substring(asName.length());
      remainingSymbolNames.add(symbolName);
    }
  }

  return remainingSymbolNames;