<#-- (c) https://github.com/MontiCore/monticore -->
  //1. load content of file at given location as String
  String serialized = de.monticore.io.FileReaderWriter.readFromFile(url);

  //2. deserialize String to an artifact scope and add to enclosing scope
  return deserialize(serialized, enclosingScope);