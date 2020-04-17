<#-- (c) https://github.com/MontiCore/monticore -->
  //1. Calculate qualified path of of stored artifact scopes relative to model path entries
  String simpleName = de.monticore.utils.Names.getSimpleName(qualifiedModelName);
  String packagePath = de.monticore.utils.Names.getPathFromQualifiedName(qualifiedModelName);
  java.nio.file.Path qualifiedPath = java.nio.file.Paths.get(packagePath, simpleName + "." + getSymbolFileExtension());

  //2. try to find qualified path within model path entries
  de.monticore.io.paths.ModelCoordinate modelCoordinate = de.monticore.io.paths.ModelCoordinates.createQualifiedCoordinate(qualifiedPath);
  modelPath.resolveModel(modelCoordinate);

  //3. Load symbol table into enclosing global scope if a file has been found
  if (modelCoordinate.hasLocation()) {
    load(modelCoordinate.getLocation(), enclosingScope);
  }