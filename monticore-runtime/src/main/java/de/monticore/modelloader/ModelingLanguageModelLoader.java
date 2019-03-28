/* (c) https://github.com/MontiCore/monticore */

package de.monticore.modelloader;

import de.monticore.AmbiguityException;
import de.monticore.ModelingLanguage;
import de.monticore.ast.ASTNode;
import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.ModelCoordinate;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static de.monticore.generating.templateengine.reporting.Reporting.reportOpenInputFile;
import static de.monticore.io.paths.ModelCoordinates.createQualifiedCoordinate;
import static de.se_rwth.commons.Names.getPathFromQualifiedName;
import static de.se_rwth.commons.Names.getSimpleName;
import static java.nio.file.Paths.get;
import static java.util.Optional.of;

// TODO PN extract CommonModelLoader
// TODO PN rename to ModelLoader
public abstract class ModelingLanguageModelLoader<T extends ASTNode> {

  private final ModelingLanguage modelingLanguage;

  private AstProvider<T> astProvider;

  public ModelingLanguageModelLoader(ModelingLanguage modelingLanguage) {
    this.modelingLanguage = modelingLanguage;
    this.astProvider = new FileBasedAstProvider<>(modelingLanguage);
  }

  public void setAstProvider(AstProvider<T> astProvider) {
    this.astProvider = astProvider;
  }

  /**
   * Loads the model with the <code>qualifiedModelName</code>. If more than one
   * corresponding model is found, an {@link AmbiguityException} is
   * thrown.
   *
   * @param qualifiedModelName the qualified name of the model to be loaded
   * @param modelPath          the modelPath
   * @return An ast of the loaded model.
   * @throws AmbiguityException is thrown, if more than one model
   *                            with the name <code>qualifiedModelName</code>
   * @see #loadModels(String, ModelPath)
   */
  public Optional<T> loadModel(final String qualifiedModelName, final ModelPath modelPath) {
    final Collection<T> models = loadModels(qualifiedModelName, modelPath);
    if (models.size() > 1) {
      throw new AmbiguityException("0xA4092 Multiple models were found with name '"
              + qualifiedModelName + "'");
    }

    return models.stream().findFirst();
  }

  /**
   * Loads all models with the specified <code>qualifiedModelName</code>, creates
   * the corresponding scope graphs and puts each in the <code>enclosingScope</code>.
   *
   * @param qualifiedModelName     the qualified name of the model(s) to be loaded
   * @param modelPath              the model path
   * @param enclosingScope         the enclosing scope for each scope graph of the loaded models
   * @param resolvingConfiguration the configuration of the resolving filters
   * @return the asts of the loaded models (mapped to the corresponding symbol table elements)
   */
  public Collection<T> loadModelsIntoScope(final String qualifiedModelName,
                                           final ModelPath modelPath, final Scope enclosingScope,
                                           final ResolvingConfiguration resolvingConfiguration) {

    if (!loadSymbolsIntoScope(qualifiedModelName, modelPath, enclosingScope, resolvingConfiguration)) {
      final Collection<T> asts = loadModels(qualifiedModelName, modelPath);
      for (T ast : asts) {
        createSymbolTableFromAST(ast, qualifiedModelName, enclosingScope, resolvingConfiguration);
      }
      return asts;
    }
    return Collections.EMPTY_SET;
  }


  /**
   * Loads all models with the specified <code>qualifiedModelName</code>, creates
   * the corresponding scope graphs and puts each in the <code>enclosingScope</code>.
   *
   * @param qualifiedModelName     the qualified name of the model(s) to be loaded
   * @param modelPath              the model path
   * @param enclosingScope         the enclosing scope for each scope graph of the loaded models
   * @param resolvingConfiguration the configuration of the resolving filters
   */
  public boolean loadSymbolsIntoScope(final String qualifiedModelName,
                                   final ModelPath modelPath, final Scope enclosingScope,
                                   final ResolvingConfiguration resolvingConfiguration)  {

    final ModelCoordinate resolvedCoordinate = resolveSymbol(qualifiedModelName, modelPath);
    if (resolvedCoordinate.hasLocation()) {
      if (modelingLanguage.getSymbolTableDeserializer().isPresent()) {
        Path resolvedLocation;
        try {
           resolvedLocation = Paths.get(new File(resolvedCoordinate.getLocation().toURI()).getPath());
        } catch (URISyntaxException e) {
          e.printStackTrace();
          return false;
        }
        String content = new FileReaderWriter().readFromFile(resolvedLocation);
        Optional<ArtifactScope> scope = modelingLanguage.getSymbolTableDeserializer().get().deserialize(content);
        if (scope.isPresent()) {
          enclosingScope.addSubScope(scope.get());
          return true;
        }
      }
    }
    return false;
  }

    /**
     * Creates the symbol table for the model in <code>modelName</code> using its
     * <code>ast</code> and adds its top scope (usually
     * {@link ArtifactScope}) to the sub scopes.
     *
     * @param ast       the ast of the model in <code>modelName</code>
     * @param modelName name of the model
     */
    protected abstract void createSymbolTableFromAST (T ast, String modelName,
            Scope enclosingScope,
            ResolvingConfiguration resolvingConfiguration);

    /**
     * Loads one or more models with the <code>qualifiedModelName</code>. If only
     * one model is expected, use
     * {@link #loadModel(String, ModelPath)} instead.
     *
     * @param qualifiedModelName the qualified name of the model(s) to be loaded
     * @param modelPath          the model path
     * @return the asts of the loaded models
     * @see #loadModel(String, ModelPath)
     */
    public Collection<T> loadModels ( final String qualifiedModelName, ModelPath modelPath){
      checkArgument(!isNullOrEmpty(qualifiedModelName));

      final Collection<T> foundModels = new ArrayList<>();

      final ModelCoordinate resolvedCoordinate = resolve(qualifiedModelName, modelPath);
      if (resolvedCoordinate.hasLocation()) {
        final T ast = astProvider.getRootNode(resolvedCoordinate);
        reportOpenInputFile(of(resolvedCoordinate.getParentDirectoryPath()),
                resolvedCoordinate.getQualifiedPath());
        foundModels.add(ast);
      }

      return foundModels;
    }

    /**
     * @param qualifiedModelName example: "de.mc.statechartOne"
     * @return the resolved coordinate (the location of the model is set if
     * successful)
     */
    private ModelCoordinate resolve ( final String qualifiedModelName, final ModelPath modelPath){
      String simpleName = getSimpleName(qualifiedModelName);
      Path qualifiedPath = get(
              getPathFromQualifiedName(qualifiedModelName)).resolve(
              simpleName + "." + modelingLanguage.getFileExtension());
      ModelCoordinate qualifiedModel = createQualifiedCoordinate(qualifiedPath);

      return modelPath.resolveModel(qualifiedModel);
    }

    /**
     * @param qualifiedModelName example: "de.mc.statechartOne"
     * @return the resolved coordinate (the location of the model is set if
     * successful)
     */
    private ModelCoordinate resolveSymbol ( final String qualifiedModelName, final ModelPath modelPath){
      String simpleName = getSimpleName(qualifiedModelName);
      Path qualifiedPath = get(
              getPathFromQualifiedName(qualifiedModelName)).resolve(
              simpleName + ".json");
      ModelCoordinate qualifiedModel = createQualifiedCoordinate(qualifiedPath);

      return modelPath.resolveModel(qualifiedModel);
    }

    public ModelingLanguage getModelingLanguage () {
      return modelingLanguage;
    }

  }
