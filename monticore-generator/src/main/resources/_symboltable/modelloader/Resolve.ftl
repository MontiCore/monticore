<#-- (c) https://github.com/MontiCore/monticore -->
  String simpleName = de.monticore.utils.Names.getSimpleName(qualifiedModelName);
  java.nio.file.Path qualifiedPath = java.nio.file.Paths.get(
  de.monticore.utils.Names.getPathFromQualifiedName(qualifiedModelName)).resolve(
      simpleName + "." + modelFileExtension);
  de.monticore.io.paths.ModelCoordinate qualifiedModel = de.monticore.io.paths.ModelCoordinates.createQualifiedCoordinate(qualifiedPath);

  return modelPath.resolveModel(qualifiedModel);