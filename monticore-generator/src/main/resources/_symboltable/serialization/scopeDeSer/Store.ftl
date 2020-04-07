<#-- (c) https://github.com/MontiCore/monticore -->
//1. calculate absolute location for symbol file to create
String packagePath = de.se_rwth.commons.Names.getPathFromQualifiedName(toSerialize.getPackageName());
String fileName = de.se_rwth.commons.Names.getFileName(toSerialize.getName(), getSymbolFileEnding());
java.nio.file.Path path = symbolPath.resolve(packagePath).resolve(fileName);

//2. serialize artifact scope
String serialized = serialize(toSerialize);

//3. store serialized artifact scope to calculated location
de.monticore.io.FileReaderWriter.storeInFile(path, serialized);