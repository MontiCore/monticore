<#-- (c) https://github.com/MontiCore/monticore -->
  String fileName = (isPresentName() ? getName() : "symbols") + "." + lang.getSymbolFileExtension();
  return java.nio.file.Paths.get(de.monticore.utils.Names.getPathFromPackage(getPackageName()), fileName);