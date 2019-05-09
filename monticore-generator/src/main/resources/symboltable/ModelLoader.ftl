<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className")}

<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign grammarName = ast.getName()?cap_first>
<#assign package = genHelper.getTargetPackage()?lower_case>
<#assign topAstName = genHelper.getQualifiedStartRuleName()>
<#assign skipSTGen = glex.getGlobalVar("skipSTGen")>

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${package};

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.io.paths.ModelCoordinate;
import de.monticore.io.paths.ModelCoordinates;
import de.monticore.io.paths.ModelPath;
import de.monticore.modelloader.AstProvider;
import de.monticore.modelloader.IModelLoader;
import de.monticore.modelloader.ParserBasedAstProvider;
import de.monticore.utils.Names;
import de.se_rwth.commons.logging.Log;
import ${package}.serialization.*;

public class ${className} implements de.monticore.modelloader.IModelLoader<${topAstName}, I${grammarName}Scope> {

  protected final ${grammarName}Language language;
  
  protected AstProvider<${topAstName}> astProvider;

  public ${className}(${grammarName}Language language) {
    this.language = language;
    this.astProvider = new ParserBasedAstProvider<>(language);
  }

  @Override
  public void createSymbolTableFromAST(final ${topAstName} ast, final String modelName, final I${grammarName}Scope enclosingScope) {
    <#if !skipSTGen>
    final ${grammarName}SymbolTableCreator symbolTableCreator =
            getModelingLanguage().getSymbolTableCreator(enclosingScope);

    if (symbolTableCreator != null) {
      Log.debug("Start creation of symbol table for model \"" + modelName + "\".",
          ${grammarName}ModelLoader.class.getSimpleName());
      final I${grammarName}Scope scope = symbolTableCreator.createFromAST(ast);

      if (!(scope instanceof ${grammarName}ArtifactScope)) {
        Log.warn("0xA7001${genHelper.getGeneratedErrorCode(ast)} Top scope of model " + modelName + " is expected to be an artifact scope, but"
          + " is scope \"" + scope.getName() + "\"");
      }

      Log.debug("Created symbol table for model \"" + modelName + "\".", ${grammarName}ModelLoader.class.getSimpleName());
    }
    else {
      Log.warn("0xA7002${genHelper.getGeneratedErrorCode(ast)} No symbol created, because '" + getModelingLanguage().getName()
        + "' does not define a symbol table creator.");
    }
    </#if>
  }

  public ${grammarName}Language getModelingLanguage() {
    return this.language;
  }
  
  public Collection<${topAstName}> loadModelsIntoScope(final String qualifiedModelName,
      final ModelPath modelPath, I${grammarName}Scope enclosingScope) {
    
    if (!loadSymbolsIntoScope(qualifiedModelName, modelPath, enclosingScope)) {
      final Collection<${topAstName}> asts = loadModels(qualifiedModelName, modelPath);
      for (${topAstName} ast : asts) {
        createSymbolTableFromAST(ast, qualifiedModelName, enclosingScope);
      }
      showWarningIfParsedModels(asts, qualifiedModelName);
      return asts;
    }
    return Collections.emptySet();
  }
  
  public Collection<${topAstName}> loadModels(final String qualifiedModelName, ModelPath modelPath){
    Preconditions.checkArgument(!Strings.isNullOrEmpty(qualifiedModelName));
    
    final Collection<${topAstName}> foundModels = new ArrayList<>();
    
    final ModelCoordinate resolvedCoordinate = resolve(qualifiedModelName, modelPath);
    if (resolvedCoordinate.hasLocation()) {
      final ${topAstName} ast = astProvider.getRootNode(resolvedCoordinate);
      Reporting.reportOpenInputFile(Optional.of(resolvedCoordinate.getParentDirectoryPath()),
          resolvedCoordinate.getQualifiedPath());
      foundModels.add(ast);
    }
    
    return foundModels;
  }
  
  public boolean loadSymbolsIntoScope(final String qualifiedModelName,
      final ModelPath modelPath, final I${grammarName}Scope enclosingScope)  {
    
    final ModelCoordinate resolvedCoordinate = resolveSymbol(qualifiedModelName, modelPath);
    if (resolvedCoordinate.hasLocation()) {
      Optional<I${grammarName}Scope> deser  = new ${grammarName}ScopeDeSer().load(resolvedCoordinate.getLocation());
      if(deser.isPresent()) {
        enclosingScope.addSubScope(deser.get());
        return true;
      }
    }
    return false;
  }
  
  /**
   * @param qualifiedModelName example: "de.mc.statechartOne"
   * @return the resolved coordinate (the location of the model is set if
   * successful)
   */
  // TODO move to interface?
  private ModelCoordinate resolve(final String qualifiedModelName, final ModelPath modelPath){
    String simpleName = Names.getSimpleName(qualifiedModelName);
    Path qualifiedPath = Paths.get(
        Names.getPathFromQualifiedName(qualifiedModelName)).resolve(
        simpleName + "." + getModelingLanguage().getFileExtension());
    ModelCoordinate qualifiedModel = ModelCoordinates.createQualifiedCoordinate(qualifiedPath);
    
    return modelPath.resolveModel(qualifiedModel);
  }
  
  protected ModelCoordinate resolveSymbol(final String qualifiedModelName, final ModelPath modelPath){
    String simpleName = Names.getSimpleName(qualifiedModelName);
    Path qualifiedPath = Paths.get(
        Names.getPathFromQualifiedName(qualifiedModelName)).resolve(
        simpleName + "." + getModelingLanguage().getSymbolFileExtension());
    ModelCoordinate qualifiedModel = ModelCoordinates.createQualifiedCoordinate(qualifiedPath);
    
    return modelPath.resolveModel(qualifiedModel);
  }
  
  protected void showWarningIfParsedModels(Collection<?> asts, String modelName) {
    if(!asts.isEmpty()) {
      Log.warn("Symbol for model \""+modelName+"\" found in a model file that has not been translated yet. This works for compatibility reasons, but is deprecated and will be removed soon. Please adjust your building process.");
    }
  }
  
}
