${tc.signature("languageName", "millName", "parserName")}
  this.modelPath = Log.errorIfNull(modelPath);

  de.monticore.modelloader.ParserBasedAstProvider astProvider =
  new de.monticore.modelloader.ParserBasedAstProvider(new ${millName}(), "${languageName}");

  ${languageName}SymbolTableCreator stc = ${millName}
  .${languageName?uncap_first}SymbolTableCreatorBuilder()
  .addToScopeStack(this)
  .build();

  ${languageName}ModelLoader ml = ${millName}
  .${languageName?uncap_first}ModelLoaderBuilder()
  .setAstProvider(astProvider)
  .setSymbolTableCreator(stc)
  .setModelFileExtension(fileExtension)
  .setSymbolFileExtension(fileExtension+"sym")
  .build();

  this.modelLoader = Optional.ofNullable(ml);