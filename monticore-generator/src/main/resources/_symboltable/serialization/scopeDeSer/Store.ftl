<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("deser")}
  // 1. Throw errors and abort storing in case of missing required information:
  if(!toSerialize.isPresentName()){
    Log.error("${deser} cannot store an artifact scope that has no name!");
    return;
  }
  if(null == getSymbolFileExtension()){
    Log.error("File extension for stored symbol tables has not been set in ${deser}!");
    return;
  }

  //2. calculate absolute location for the file to create, including the package if it is non-empty
  java.nio.file.Path path = symbolPath; //starting with symbol path
  if(null != toSerialize.getPackageName() && toSerialize.getPackageName().length()>0){
    path = path.resolve(de.se_rwth.commons.Names.getPathFromQualifiedName(toSerialize.getPackageName()));
  }
  path = path.resolve(toSerialize.getName() + "." + getSymbolFileExtension());

  //3. serialize artifact scope, which will become the file content
  String serialized = serialize(toSerialize);

  //4. store serialized artifact scope to calculated location
  de.monticore.io.FileReaderWriter.storeInFile(path, serialized);