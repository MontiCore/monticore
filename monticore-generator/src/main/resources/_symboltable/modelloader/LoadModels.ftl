<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "astStartProd")}
  com.google.common.base.Preconditions.checkArgument(!com.google.common.base.Strings.isNullOrEmpty(qualifiedModelName));

  final Collection<${astStartProd}> foundModels = new ArrayList<>();

  final de.monticore.io.paths.ModelCoordinate resolvedCoordinate = resolve(qualifiedModelName, modelPath);
  if (resolvedCoordinate.hasLocation()) {
    final ${astStartProd} ast = astProvider.getRootNode(resolvedCoordinate);
    de.monticore.generating.templateengine.reporting.Reporting.reportOpenInputFile(Optional.of(resolvedCoordinate.getParentDirectoryPath()),
    resolvedCoordinate.getQualifiedPath());
    foundModels.add(ast);
  }

  return foundModels;