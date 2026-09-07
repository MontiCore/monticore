<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature()}
  String serialized = this.serialize(scope);
  de.monticore.io.FileReaderWriter.storeInFile(java.nio.file.Paths.get(fileName), serialized);
  return serialized;
