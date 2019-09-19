package de.monticore.codegen.cd2java._symboltable;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java._symboltable.language.LanguageBuilderDecorator;
import de.monticore.codegen.cd2java._symboltable.language.LanguageDecorator;
import de.monticore.codegen.cd2java._symboltable.modelloader.ModelLoaderBuilderDecorator;
import de.monticore.codegen.cd2java._symboltable.modelloader.ModelLoaderDecorator;
import de.monticore.codegen.cd2java._symboltable.scope.*;
import de.monticore.codegen.cd2java._symboltable.symbol.*;
import de.monticore.codegen.cd2java._symboltable.symboltablecreator.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.IterablePath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.PACKAGE;
import static de.monticore.codegen.cd2java.CoreTemplates.createPackageHookPoint;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.SYMBOL_TABLE_PACKAGE;
import static de.monticore.codegen.mc2cd.TransformationHelper.existsHandwrittenClass;
import static de.monticore.utils.Names.constructQualifiedName;

public class SymbolTableCDDecorator extends AbstractDecorator {

  protected final SymbolDecorator symbolDecorator;

  protected final SymbolBuilderDecorator symbolBuilderDecorator;

  protected final SymbolReferenceDecorator symbolReferenceDecorator;

  protected final SymbolReferenceBuilderDecorator symbolReferenceBuilderDecorator;

  protected final SymbolTableService symbolTableService;

  protected final ScopeClassDecorator scopeClassDecorator;

  protected final ScopeClassBuilderDecorator scopeClassBuilderDecorator;

  protected final ScopeInterfaceDecorator scopeInterfaceDecorator;

  protected final GlobalScopeInterfaceDecorator globalScopeInterfaceDecorator;

  protected final GlobalScopeClassDecorator globalScopeClassDecorator;

  protected final GlobalScopeClassBuilderDecorator globalScopeClassBuilderDecorator;

  protected final ArtifactScopeDecorator artifactScopeDecorator;

  protected final ArtifactScopeBuilderDecorator artifactScopeBuilderDecorator;

  protected final CommonSymbolInterfaceDecorator commonSymbolInterfaceDecorator;

  protected final LanguageDecorator languageDecorator;

  protected final LanguageBuilderDecorator languageBuilderDecorator;

  protected final IterablePath handCodedPath;

  protected final ModelLoaderDecorator modelLoaderDecorator;

  protected final ModelLoaderBuilderDecorator modelLoaderBuilderDecorator;

  protected final SymbolResolvingDelegateInterfaceDecorator symbolResolvingDelegateInterfaceDecorator;

  protected final SymbolTableCreatorDecorator symbolTableCreatorDecorator;

  protected final SymbolTableCreatorBuilderDecorator symbolTableCreatorBuilderDecorator;

  protected final SymbolTableCreatorDelegatorDecorator symbolTableCreatorDelegatorDecorator;

  protected final SymbolTableCreatorForSuperTypes symbolTableCreatorForSuperTypes;

  protected final SymbolTableCreatorDelegatorBuilderDecorator symbolTableCreatorDelegatorBuilderDecorator;

  public SymbolTableCDDecorator(final GlobalExtensionManagement glex,
                                final IterablePath handCodedPath,
                                final SymbolTableService symbolTableService,
                                final SymbolDecorator symbolDecorator,
                                final SymbolBuilderDecorator symbolBuilderDecorator,
                                final SymbolReferenceDecorator symbolReferenceDecorator,
                                final SymbolReferenceBuilderDecorator symbolReferenceBuilderDecorator,
                                final ScopeClassDecorator scopeClassDecorator,
                                final ScopeClassBuilderDecorator scopeClassBuilderDecorator,
                                final ScopeInterfaceDecorator scopeInterfaceDecorator,
                                final GlobalScopeInterfaceDecorator globalScopeInterfaceDecorator,
                                final GlobalScopeClassDecorator globalScopeClassDecorator,
                                final GlobalScopeClassBuilderDecorator globalScopeClassBuilderDecorator,
                                final ArtifactScopeDecorator artifactScopeDecorator,
                                final ArtifactScopeBuilderDecorator artifactScopeBuilderDecorator,
                                final CommonSymbolInterfaceDecorator commonSymbolInterfaceDecorator,
                                final LanguageDecorator languageDecorator,
                                final LanguageBuilderDecorator languageBuilderDecorator,
                                final ModelLoaderDecorator modelLoaderDecorator,
                                final ModelLoaderBuilderDecorator modelLoaderBuilderDecorator,
                                final SymbolResolvingDelegateInterfaceDecorator symbolResolvingDelegateInterfaceDecorator,
                                final SymbolTableCreatorDecorator symbolTableCreatorDecorator,
                                final SymbolTableCreatorBuilderDecorator symbolTableCreatorBuilderDecorator,
                                final SymbolTableCreatorDelegatorDecorator symbolTableCreatorDelegatorDecorator,
                                final SymbolTableCreatorForSuperTypes symbolTableCreatorForSuperTypes,
                                final SymbolTableCreatorDelegatorBuilderDecorator symbolTableCreatorDelegatorBuilderDecorator) {
    super(glex);
    this.symbolDecorator = symbolDecorator;
    this.symbolBuilderDecorator = symbolBuilderDecorator;
    this.symbolReferenceDecorator = symbolReferenceDecorator;
    this.symbolTableService = symbolTableService;
    this.scopeClassDecorator = scopeClassDecorator;
    this.scopeClassBuilderDecorator = scopeClassBuilderDecorator;
    this.scopeInterfaceDecorator = scopeInterfaceDecorator;
    this.globalScopeInterfaceDecorator = globalScopeInterfaceDecorator;
    this.globalScopeClassDecorator = globalScopeClassDecorator;
    this.globalScopeClassBuilderDecorator = globalScopeClassBuilderDecorator;
    this.artifactScopeDecorator = artifactScopeDecorator;
    this.artifactScopeBuilderDecorator = artifactScopeBuilderDecorator;
    this.symbolReferenceBuilderDecorator = symbolReferenceBuilderDecorator;
    this.commonSymbolInterfaceDecorator = commonSymbolInterfaceDecorator;
    this.languageDecorator = languageDecorator;
    this.handCodedPath = handCodedPath;
    this.languageBuilderDecorator = languageBuilderDecorator;
    this.modelLoaderDecorator = modelLoaderDecorator;
    this.modelLoaderBuilderDecorator = modelLoaderBuilderDecorator;
    this.symbolResolvingDelegateInterfaceDecorator = symbolResolvingDelegateInterfaceDecorator;
    this.symbolTableCreatorDecorator = symbolTableCreatorDecorator;
    this.symbolTableCreatorBuilderDecorator = symbolTableCreatorBuilderDecorator;
    this.symbolTableCreatorDelegatorDecorator = symbolTableCreatorDelegatorDecorator;
    this.symbolTableCreatorForSuperTypes = symbolTableCreatorForSuperTypes;
    this.symbolTableCreatorDelegatorBuilderDecorator = symbolTableCreatorDelegatorBuilderDecorator;
  }

  public ASTCDCompilationUnit decorate(ASTCDCompilationUnit astCD, ASTCDCompilationUnit symbolCD, ASTCDCompilationUnit scopeCD) {
    List<String> symbolTablePackage = new ArrayList<>(astCD.getPackageList());
    symbolTablePackage.addAll(Arrays.asList(astCD.getCDDefinition().getName().toLowerCase(), SYMBOL_TABLE_PACKAGE));

    List<ASTCDType> symbolProds = symbolTableService.getSymbolDefiningProds(astCD.getCDDefinition());

    // create symbol classes
    List<ASTCDClass> decoratedSymbolClasses = createSymbolClasses(symbolCD.getCDDefinition().getCDClassList(), symbolTablePackage);
    decoratedSymbolClasses.addAll(createSymbolClasses(symbolCD.getCDDefinition().getCDInterfaceList(), symbolTablePackage));

    // create scope classes
    ASTCDClass scopeClass = createScopeClass(scopeCD, symbolCD, symbolTablePackage);

    ASTCDDefinition symTabCD = CD4AnalysisMill.cDDefinitionBuilder()
        .setName(astCD.getCDDefinition().getName())
        .addAllCDClasss(decoratedSymbolClasses)
        .addAllCDClasss(createSymbolBuilderClasses(decoratedSymbolClasses))
        .addCDClass(scopeClass)
        .addCDClass(createScopeClassBuilder(scopeClass))
        .addCDInterface(createScopeInterface(scopeCD, symbolCD))
        .addAllCDClasss(createSymbolReferenceClasses(symbolCD.getCDDefinition()))
        .addAllCDClasss(createSymbolReferenceBuilderClasses(symbolCD.getCDDefinition()))
        .addCDInterface(createICommonSymbol(astCD))
        .addAllCDInterfaces(createSymbolResolvingDelegateInterfaces(symbolProds))
        .build();

    if (symbolTableService.getStartProd(astCD.getCDDefinition()).isPresent()) {
      // global scope
      symTabCD.addCDInterface(createGlobalScopeInterface(astCD, symbolTablePackage));
      ASTCDClass globalScopeClass = createGlobalScopeClass(astCD);
      symTabCD.addCDClass(globalScopeClass);
      symTabCD.addCDClass(createGlobalScopeClassBuilder(globalScopeClass));

      // artifact scope
      boolean isArtifactScopeHandCoded = existsHandwrittenClass(handCodedPath,
          constructQualifiedName(symbolTablePackage, symbolTableService.getArtifactScopeSimpleName()));
      this.artifactScopeDecorator.setArtifactScopeTop(isArtifactScopeHandCoded);
      ASTCDClass artifactScope = createArtifactScope(astCD);
      symTabCD.addCDClass(artifactScope);
      symTabCD.addCDClass(createArtifactBuilderScope(artifactScope));

      // language
      // needs to know if it is overwritten to generate method differently
      boolean isLanguageHandCoded = existsHandwrittenClass(handCodedPath,
          constructQualifiedName(symbolTablePackage, symbolTableService.getLanguageClassSimpleName()));
      // set boolean if language is TOPed or not
      this.languageDecorator.setLanguageTop(isLanguageHandCoded);
      ASTCDClass languageClass = createLanguage(astCD);
      symTabCD.addCDClass(languageClass);
      if (!symbolTableService.isComponent() && isLanguageHandCoded) {
        symTabCD.addCDClass(createLanguageBuilder(languageClass));
      }

      // model loader
      Optional<ASTCDClass> modelLoader = createModelLoader(astCD);
      if (modelLoader.isPresent()) {
        symTabCD.addCDClass(modelLoader.get());
        if (!symbolTableService.isComponent()) {
          symTabCD.addCDClass(createModelLoaderBuilder(modelLoader.get()));
        }
      }

      // symbol table creator
      Optional<ASTCDClass> symbolTableCreator = createSymbolTableCreator(astCD);
      if (symbolTableCreator.isPresent()) {
        symTabCD.addCDClass(symbolTableCreator.get());
        symTabCD.addCDClass(createSymbolTableCreatorBuilder(astCD));
      }

      //todo activate when know what to do with component
//      // symboltable creator delegator
//      Optional<ASTCDClass> symbolTableCreatorDelegator = createSymbolTableCreatorDelegator(astCD);
//      if(symbolTableCreatorDelegator.isPresent()){
//        symTabCD.addCDClass(symbolTableCreatorDelegator.get());
//      symTabCD.addAllCDClasss(createSymbolTableCreatorDelegatorBuilder(symbolTableCreatorDelegator.get()));

//      }
//
//      // SuperSTCForSub
//      symTabCD.addAllCDClasss(createSymbolTableCreatorForSuperTypes(astCD));
    }

    for (ASTCDClass cdClass : symTabCD.getCDClassList()) {
      this.replaceTemplate(PACKAGE, cdClass, createPackageHookPoint(symbolTablePackage));
    }

    for (ASTCDInterface cdInterface : symTabCD.getCDInterfaceList()) {
      this.replaceTemplate(CoreTemplates.PACKAGE, cdInterface, createPackageHookPoint(symbolTablePackage));
    }

    for (ASTCDEnum cdEnum : symTabCD.getCDEnumList()) {
      this.replaceTemplate(CoreTemplates.PACKAGE, cdEnum, createPackageHookPoint(symbolTablePackage));
    }

    return CD4AnalysisMill.cDCompilationUnitBuilder()
        .setPackageList(symbolTablePackage)
        .setCDDefinition(symTabCD)
        .build();
  }

  protected List<ASTCDClass> createSymbolClasses(List<? extends ASTCDType> astcdTypeList, List<String> symbolTablePackage) {
    List<ASTCDClass> symbolClassList = new ArrayList<>();
    for (ASTCDType astcdType : astcdTypeList) {
      boolean isSymbolHandCoded = existsHandwrittenClass(handCodedPath,
          constructQualifiedName(symbolTablePackage, symbolTableService.getSymbolSimpleName(astcdType)));
      symbolDecorator.setSymbolTop(isSymbolHandCoded);
      symbolClassList.add(symbolDecorator.decorate(astcdType));
    }
    return symbolClassList;
  }

  protected List<ASTCDClass> createSymbolBuilderClasses(List<ASTCDClass> symbolASTClasses) {
    return symbolASTClasses
        .stream()
        .map(symbolBuilderDecorator::decorate)
        .collect(Collectors.toList());
  }

  protected List<ASTCDClass> createSymbolReferenceClasses(ASTCDDefinition symbolCD) {
    List<ASTCDClass> symbolReferences = symbolCD.getCDClassList()
        .stream()
        .map(symbolReferenceDecorator::decorate)
        .collect(Collectors.toList());
    symbolReferences.addAll(symbolCD.getCDInterfaceList()
        .stream()
        .map(symbolReferenceDecorator::decorate)
        .collect(Collectors.toList()));
    return symbolReferences;
  }

  protected List<ASTCDClass> createSymbolReferenceBuilderClasses(ASTCDDefinition astcdDefinition) {
    List<ASTCDClass> symbolReferences = astcdDefinition.getCDClassList()
        .stream()
        .map(symbolReferenceBuilderDecorator::decorate)
        .collect(Collectors.toList());
    symbolReferences.addAll(astcdDefinition.getCDInterfaceList()
        .stream()
        .map(symbolReferenceBuilderDecorator::decorate)
        .collect(Collectors.toList()));
    return symbolReferences;
  }

  protected List<ASTCDInterface> createSymbolResolvingDelegateInterfaces(List<? extends ASTCDType> astcdTypeList) {
    return astcdTypeList
        .stream()
        .map(symbolResolvingDelegateInterfaceDecorator::decorate)
        .collect(Collectors.toList());
  }

  protected ASTCDClass createScopeClass(ASTCDCompilationUnit scopeCD, ASTCDCompilationUnit symbolCd, List<String> symbolTablePackage) {
    boolean isScopeTop = existsHandwrittenClass(handCodedPath,
        constructQualifiedName(symbolTablePackage, symbolTableService.getScopeClassSimpleName()));
    scopeClassDecorator.setScopeTop(isScopeTop);
    return scopeClassDecorator.decorate(scopeCD, symbolCd);
  }

  protected ASTCDClass createScopeClassBuilder(ASTCDClass scopeClass) {
    return scopeClassBuilderDecorator.decorate(scopeClass);
  }

  protected ASTCDInterface createScopeInterface(ASTCDCompilationUnit scopeCD, ASTCDCompilationUnit symbolCD) {
    return scopeInterfaceDecorator.decorate(scopeCD, symbolCD);
  }

  protected ASTCDInterface createGlobalScopeInterface(ASTCDCompilationUnit compilationUnit, List<String> symbolTablePackage) {
    boolean isGlobalScopeTop = existsHandwrittenClass(handCodedPath,
        constructQualifiedName(symbolTablePackage, symbolTableService.getGlobalScopeInterfaceSimpleName()));
    globalScopeInterfaceDecorator.setGlobalScopeTop(isGlobalScopeTop);
    return globalScopeInterfaceDecorator.decorate(compilationUnit);
  }

  protected ASTCDClass createGlobalScopeClass(ASTCDCompilationUnit compilationUnit) {
    return globalScopeClassDecorator.decorate(compilationUnit);
  }

  protected ASTCDClass createGlobalScopeClassBuilder(ASTCDClass globalScopeClass) {
    return globalScopeClassBuilderDecorator.decorate(globalScopeClass);
  }

  protected ASTCDClass createArtifactScope(ASTCDCompilationUnit compilationUnit) {
    return artifactScopeDecorator.decorate(compilationUnit);
  }

  protected ASTCDClass createArtifactBuilderScope(ASTCDClass artifactScopeClass) {
    return artifactScopeBuilderDecorator.decorate(artifactScopeClass);
  }

  protected ASTCDInterface createICommonSymbol(ASTCDCompilationUnit compilationUnit) {
    return commonSymbolInterfaceDecorator.decorate(compilationUnit);
  }

  protected ASTCDClass createLanguage(ASTCDCompilationUnit compilationUnit) {
    return languageDecorator.decorate(compilationUnit);
  }

  protected ASTCDClass createLanguageBuilder(ASTCDClass astcdClass) {
    return languageBuilderDecorator.decorate(astcdClass);
  }

  protected Optional<ASTCDClass> createModelLoader(ASTCDCompilationUnit compilationUnit) {
    return modelLoaderDecorator.decorate(compilationUnit);
  }

  protected ASTCDClass createModelLoaderBuilder(ASTCDClass modelLoaderClass) {
    return modelLoaderBuilderDecorator.decorate(modelLoaderClass);
  }

  protected Optional<ASTCDClass> createSymbolTableCreator(ASTCDCompilationUnit compilationUnit) {
    return symbolTableCreatorDecorator.decorate(compilationUnit);
  }

  protected ASTCDClass createSymbolTableCreatorBuilder(ASTCDCompilationUnit compilationUnit) {
    return symbolTableCreatorBuilderDecorator.decorate(compilationUnit);
  }

  protected Optional<ASTCDClass> createSymbolTableCreatorDelegator(ASTCDCompilationUnit compilationUnit) {
    return symbolTableCreatorDelegatorDecorator.decorate(compilationUnit);
  }

  protected List<ASTCDClass> createSymbolTableCreatorForSuperTypes(ASTCDCompilationUnit compilationUnit) {
    return symbolTableCreatorForSuperTypes.decorate(compilationUnit);
  }

  protected ASTCDClass createSymbolTableCreatorDelegatorBuilder(ASTCDClass sTCDelegatorClass) {
    return symbolTableCreatorDelegatorBuilderDecorator.decorate(sTCDelegatorClass);
  }
}
