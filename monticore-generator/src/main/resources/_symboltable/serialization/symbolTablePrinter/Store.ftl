<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("deSer")}
  java.io.File file = new java.io.File(fileName);
  String serialized = new ${deSer}().serialize(scope);
  de.monticore.io.FileReaderWriter.storeInFile(file.toPath(), serialized);
  return serialized;