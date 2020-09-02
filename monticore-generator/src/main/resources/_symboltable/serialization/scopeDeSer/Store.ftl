<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("deser", "generatedError")}
  // 1. Throw error and abort storing in case of missing required information:
  if(!scope.isPresentName()){
    Log.error("0xA7015${generatedError} ${deser} cannot store an artifact scope that has no name!");
  }

  //2. calculate absolute location for the file to create, including the package if it is non-empty
  java.nio.file.Path path = symbolPath; //starting with symbol path
  if(null != scope.getPackageName() && scope.getPackageName().length()>0){
    path = path.resolve(de.se_rwth.commons.Names.getPathFromQualifiedName(scope.getPackageName()));
  }
  path = path.resolve(scope.getName() + "." + symbolFileExtension);
  return store(scope, path.toString());