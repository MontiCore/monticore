<#-- (c) https://github.com/MontiCore/monticore -->
  java.io.File file = new java.io.File(fileName);
  String serialized = serialize(scope);
  de.monticore.io.FileReaderWriter.storeInFile(file.toPath(), serialized);
  return serialized;