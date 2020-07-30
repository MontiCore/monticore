<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "modelLoaderClass")}
  ${modelLoaderClass} obj = new ${modelLoaderClass}(astProvider, symbolTableCreator,
                                                    modelFileExtension, symbolFileExtension);
  return obj;