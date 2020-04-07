<#-- (c) https://github.com/MontiCore/monticore -->
String serialized = de.monticore.io.FileReaderWriter.readFromFile(url);
return deserialize(serialized, enclosingScope);