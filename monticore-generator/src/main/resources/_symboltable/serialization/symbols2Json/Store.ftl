<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature()}
  getJsonPrinter().clearBuffer();
  scope.accept(getTraverser());
  String serialized = getJsonPrinter().getContent();
  de.monticore.io.FileReaderWriter.storeInFile(java.nio.file.Paths.get(fileName), serialized);
  return serialized;
