/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable;

import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java._symboltable.scope.ArtifactScopeClassDecorator;
import de.monticore.codegen.cd2java._symboltable.scope.ArtifactScopeInterfaceDecorator;
import de.monticore.codegen.cd2java._symboltable.scope.GlobalScopeClassDecorator;
import de.monticore.codegen.cd2java._symboltable.scope.GlobalScopeInterfaceDecorator;
import de.monticore.codegen.cd2java._symboltable.scope.ScopeClassDecorator;
import de.monticore.codegen.cd2java._symboltable.scope.ScopeInterfaceDecorator;
import de.monticore.codegen.cd2java._symboltable.scopesgenitor.ScopesGenitorDecorator;
import de.monticore.codegen.cd2java._symboltable.scopesgenitor.ScopesGenitorDelegatorDecorator;
import de.monticore.codegen.cd2java._symboltable.serialization.ScopeDeSerDecorator;
import de.monticore.codegen.cd2java._symboltable.serialization.SymbolDeSerDecorator;
import de.monticore.codegen.cd2java._symboltable.serialization.Symbols2JsonDecorator;
import de.monticore.codegen.cd2java._symboltable.symbol.CommonSymbolInterfaceDecorator;
import de.monticore.codegen.cd2java._symboltable.symbol.SymbolBuilderDecorator;
import de.monticore.codegen.cd2java._symboltable.symbol.SymbolDecorator;
import de.monticore.codegen.cd2java._symboltable.symbol.SymbolResolverInterfaceDecorator;
import de.monticore.codegen.cd2java._symboltable.symbol.SymbolSurrogateBuilderDecorator;
import de.monticore.codegen.cd2java._symboltable.symbol.SymbolSurrogateDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.Joiners;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.cd.codegen.CD2JavaTemplates.ANNOTATIONS;
import static de.monticore.cd.codegen.CD2JavaTemplates.PACKAGE;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.SYMBOL_TABLE_PACKAGE;
import static de.monticore.generating.GeneratorEngine.existsHandwrittenClass;

/**
 * creates all classes and interfaces for the _symboltbale package
 */
public class SymbolTableCDDecorator extends AbstractDecorator {

  protected final SymbolDecorator symbolDecorator;

  protected final SymbolBuilderDecorator symbolBuilderDecorator;

  protected final SymbolSurrogateDecorator symbolReferenceDecorator;

  protected final SymbolSurrogateBuilderDecorator symbolReferenceBuilderDecorator;

  protected final SymbolTableService symbolTableService;

  protected final ScopeClassDecorator scopeClassDecorator;

  protected final ScopeInterfaceDecorator scopeInterfaceDecorator;

  protected final GlobalScopeClassDecorator globalScopeClassDecorator;

  protected final GlobalScopeInterfaceDecorator globalScopeInterfaceDecorator;

  protected final ArtifactScopeClassDecorator artifactScopeDecorator;

  protected final ArtifactScopeInterfaceDecorator artifactScopeInterfaceDecorator;

  protected final CommonSymbolInterfaceDecorator commonSymbolInterfaceDecorator;

  protected final MCPath handCodedPath;

  protected final SymbolResolverInterfaceDecorator symbolResolverInterfaceDecorator;

  protected final SymbolDeSerDecorator symbolDeSerDecorator;

  protected final ScopeDeSerDecorator scopeDeSerDecorator;

  protected final Symbols2JsonDecorator symbols2JsonDecorator;

  protected final ScopesGenitorDecorator scopesGenitorDecorator;

  protected final ScopesGenitorDelegatorDecorator scopesGenitorDelegatorDecorator;

  public SymbolTableCDDecorator(final GlobalExtensionManagement glex,
                                final MCPath handCodedPath,
                                final SymbolTableService symbolTableService,
                                final SymbolDecorator symbolDecorator,
                                final SymbolBuilderDecorator symbolBuilderDecorator,
                                final SymbolSurrogateDecorator symbolReferenceDecorator,
                                final SymbolSurrogateBuilderDecorator symbolReferenceBuilderDecorator,
                                final ScopeInterfaceDecorator scopeInterfaceDecorator,
                                final ScopeClassDecorator scopeClassDecorator,
                                final GlobalScopeInterfaceDecorator globalScopeInterfaceDecorator,
                                final GlobalScopeClassDecorator globalScopeClassDecorator,
                                final ArtifactScopeInterfaceDecorator artifactScopeInterfaceDecorator,
                                final ArtifactScopeClassDecorator artifactScopeDecorator,
                                final CommonSymbolInterfaceDecorator commonSymbolInterfaceDecorator,
                                final SymbolResolverInterfaceDecorator symbolResolverInterfaceDecorator,
                                final SymbolDeSerDecorator symbolDeSerDecorator,
                                final ScopeDeSerDecorator scopeDeSerDecorator,
                                final Symbols2JsonDecorator symbols2JsonDecorator,
                                final ScopesGenitorDecorator scopesGenitorDecorator,
                                final ScopesGenitorDelegatorDecorator scopesGenitorDelegatorDecorator) {
    super(glex);
    this.symbolDecorator = symbolDecorator;
    this.symbolBuilderDecorator = symbolBuilderDecorator;
    this.symbolReferenceDecorator = symbolReferenceDecorator;
    this.symbolTableService = symbolTableService;
    this.scopeInterfaceDecorator = scopeInterfaceDecorator;
    this.scopeClassDecorator = scopeClassDecorator;
    this.globalScopeInterfaceDecorator = globalScopeInterfaceDecorator;
    this.globalScopeClassDecorator = globalScopeClassDecorator;
    this.artifactScopeDecorator = artifactScopeDecorator;
    this.artifactScopeInterfaceDecorator = artifactScopeInterfaceDecorator;
    this.symbolReferenceBuilderDecorator = symbolReferenceBuilderDecorator;
    this.commonSymbolInterfaceDecorator = commonSymbolInterfaceDecorator;
    this.handCodedPath = handCodedPath;
    this.symbolResolverInterfaceDecorator = symbolResolverInterfaceDecorator;
    this.symbolDeSerDecorator = symbolDeSerDecorator;
    this.scopeDeSerDecorator = scopeDeSerDecorator;
    this.symbols2JsonDecorator = symbols2JsonDecorator;
    this.scopesGenitorDecorator = scopesGenitorDecorator;
    this.scopesGenitorDelegatorDecorator = scopesGenitorDelegatorDecorator;
  }

  public void decorate(ASTCDCompilationUnit astCD, ASTCDCompilationUnit symbolCD, ASTCDCompilationUnit scopeCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage symbolTablePackage = getPackage(astCD, decoratedCD, SYMBOL_TABLE_PACKAGE);
    List<ASTCDType> symbolProds = symbolTableService.getSymbolDefiningProds(astCD.getCDDefinition());

    // create symbol classes
    List<ASTCDClass> decoratedSymbolClasses = createSymbolClasses(symbolCD.getCDDefinition().getCDClassesList(), symbolTablePackage.getName());

    // create scope classes
    ASTCDClass scopeClass = createScopeClass(scopeCD, symbolCD, symbolTablePackage.getName());

    //create symbolDeSer classes
    List<ASTCDClass> symbolDeSerList = createSymbolDeSerClasses(symbolCD.getCDDefinition().getCDClassesList());

    //create symbolTablePrinter class
    ASTCDClass symbolTablePrinterClass = createSymbolTablePrinterClass(scopeCD, symbolCD);

    symbolTablePackage.addAllCDElements(decoratedSymbolClasses);
    symbolTablePackage.addAllCDElements(createSymbolBuilderClasses(symbolCD.getCDDefinition().getCDClassesList()));
    symbolTablePackage.addCDElement(scopeClass);
    symbolTablePackage.addCDElement(createScopeInterface(scopeCD, symbolCD));
    symbolTablePackage.addAllCDElements(createSymbolReferenceClasses(symbolCD.getCDDefinition().getCDClassesList()));
    symbolTablePackage.addAllCDElements(createSymbolReferenceBuilderClasses(symbolCD.getCDDefinition().getCDClassesList()));
    symbolTablePackage.addAllCDElements(symbolDeSerList);
    symbolTablePackage.addCDElement(symbolTablePrinterClass);
    symbolTablePackage.addCDElement(createICommonSymbol(astCD));
    symbolTablePackage.addAllCDElements(createSymbolResolverInterfaces(symbolProds));
    symbolTablePackage.addCDElement(createGlobalScopeInterface(astCD, symbolTablePackage.getName()));
    symbolTablePackage.addCDElement(createArtifactScopeInterface(astCD));

    Optional<ASTCDClass> scopeSkeletonCreatorDelegator = createScopesGenitorDelegator(astCD);
    scopeSkeletonCreatorDelegator.ifPresent(symbolTablePackage::addCDElement);

    // artifact scope
    boolean isArtifactScopeHandCoded = existsHandwrittenClass(handCodedPath,
            Joiners.DOT.join(symbolTablePackage.getName(), symbolTableService.getArtifactScopeSimpleName()));
    this.artifactScopeDecorator.setArtifactScopeTop(isArtifactScopeHandCoded);
    ASTCDClass artifactScope = createArtifactScope(astCD);
    symbolTablePackage.addCDElement(artifactScope);

    // scope deser
    ASTCDClass scopeDeSer = createScopeDeSerClass(scopeCD, symbolCD);
    symbolTablePackage.addCDElement(scopeDeSer);

    // global scope
    ASTCDClass globalScopeClass = createGlobalScopeClass(astCD, symbolTablePackage.getName());
    symbolTablePackage.addCDElement(globalScopeClass);


    //scope skeleton creator
    Optional<ASTCDClass> scopeSkeletonCreator = createScopesGenitor(astCD);
    scopeSkeletonCreator.ifPresent(symbolTablePackage::addCDElement);

  }

  protected void addPackageAndAnnotation(ASTCDDefinition symTabCD, List<String> symbolTablePackage) {
    for (ASTCDClass cdClass : symTabCD.getCDClassesList()) {
      this.replaceTemplate(PACKAGE, cdClass, decorationHelper.createPackageHookPoint(symbolTablePackage));
      this.replaceTemplate(ANNOTATIONS, cdClass, decorationHelper.createAnnotationsHookPoint(cdClass.getModifier()));
    }

    for (ASTCDInterface cdInterface : symTabCD.getCDInterfacesList()) {
      this.replaceTemplate(CD2JavaTemplates.PACKAGE, cdInterface, decorationHelper.createPackageHookPoint(symbolTablePackage));
      this.replaceTemplate(ANNOTATIONS, cdInterface, decorationHelper.createAnnotationsHookPoint(cdInterface.getModifier()));
    }

    for (ASTCDEnum cdEnum : symTabCD.getCDEnumsList()) {
      this.replaceTemplate(CD2JavaTemplates.PACKAGE, cdEnum, decorationHelper.createPackageHookPoint(symbolTablePackage));
      this.replaceTemplate(ANNOTATIONS, cdEnum, decorationHelper.createAnnotationsHookPoint(cdEnum.getModifier()));
    }
  }

  protected List<ASTCDClass> createSymbolClasses(List<ASTCDClass> symbolClases, String symbolTablePackage) {
    List<ASTCDClass> symbolClassList = new ArrayList<>();
    for (ASTCDClass astcdClass : symbolClases) {
      boolean isSymbolHandCoded = existsHandwrittenClass(handCodedPath,
          Joiners.DOT.join(symbolTablePackage, symbolTableService.getSymbolSimpleName(astcdClass)));
      symbolDecorator.setSymbolTop(isSymbolHandCoded);
      symbolClassList.add(symbolDecorator.decorate(astcdClass));
    }
    return symbolClassList;
  }

  protected List<ASTCDClass> createSymbolBuilderClasses(List<ASTCDClass> symbolASTClasses) {
    return symbolASTClasses
        .stream()
        .map(symbolBuilderDecorator::decorate)
        .collect(Collectors.toList());
  }

  protected List<ASTCDClass> createSymbolReferenceClasses(List<ASTCDClass> symbolClasses) {
    return symbolClasses
        .stream()
        .map(symbolReferenceDecorator::decorate)
        .collect(Collectors.toList());
  }

  protected List<ASTCDClass> createSymbolReferenceBuilderClasses(List<ASTCDClass> symbolClasses) {
    return symbolClasses
        .stream()
        .map(symbolReferenceBuilderDecorator::decorate)
        .collect(Collectors.toList());
  }

  protected List<ASTCDInterface> createSymbolResolverInterfaces(List<? extends ASTCDType> astcdTypeList) {
    return astcdTypeList
        .stream()
        .map(symbolResolverInterfaceDecorator::decorate)
        .collect(Collectors.toList());
  }

  protected ASTCDClass createScopeClass(ASTCDCompilationUnit scopeCD, ASTCDCompilationUnit symbolCd, String symbolTablePackage) {
    boolean isScopeTop = existsHandwrittenClass(handCodedPath,
            Joiners.DOT.join(symbolTablePackage, symbolTableService.getScopeClassSimpleName()));
    scopeClassDecorator.setScopeTop(isScopeTop);
    return scopeClassDecorator.decorate(scopeCD, symbolCd);
  }

  protected ASTCDInterface createScopeInterface(ASTCDCompilationUnit scopeCD, ASTCDCompilationUnit symbolCD) {
    return scopeInterfaceDecorator.decorate(scopeCD, symbolCD);
  }

  protected ASTCDClass createGlobalScopeClass(ASTCDCompilationUnit compilationUnit, String symbolTablePackage) {
    boolean isGlobalScopeTop = existsHandwrittenClass(handCodedPath,
            Joiners.DOT.join(symbolTablePackage, symbolTableService.getGlobalScopeSimpleName()));
    globalScopeClassDecorator.setGlobalScopeTop(isGlobalScopeTop);
    return globalScopeClassDecorator.decorate(compilationUnit);
  }

  protected ASTCDInterface createGlobalScopeInterface(ASTCDCompilationUnit compilationUnit, String symbolTablePackage) {
    boolean isGlobalScopeInterfaceTop = existsHandwrittenClass(handCodedPath,
            Joiners.DOT.join(symbolTablePackage, symbolTableService.getGlobalScopeInterfaceSimpleName()));
    globalScopeInterfaceDecorator.setGlobalScopeInterfaceTop(isGlobalScopeInterfaceTop);
    return globalScopeInterfaceDecorator.decorate(compilationUnit);
  }

  protected ASTCDClass createArtifactScope(ASTCDCompilationUnit compilationUnit) {
    return artifactScopeDecorator.decorate(compilationUnit);
  }

  protected ASTCDInterface createArtifactScopeInterface(ASTCDCompilationUnit compilationUnit) {
    return artifactScopeInterfaceDecorator.decorate(compilationUnit);
  }

  protected ASTCDInterface createICommonSymbol(ASTCDCompilationUnit compilationUnit) {
    return commonSymbolInterfaceDecorator.decorate(compilationUnit);
  }

  protected List<ASTCDClass> createSymbolDeSerClasses(List<ASTCDClass> symbolClassList) {
    return symbolClassList
        .stream()
        .map(symbolDeSerDecorator::decorate)
        .collect(Collectors.toList());
  }

  protected ASTCDClass createScopeDeSerClass(ASTCDCompilationUnit scopeCD, ASTCDCompilationUnit symbolCd) {
    return scopeDeSerDecorator.decorate(scopeCD, symbolCd);
  }

  protected ASTCDClass createSymbolTablePrinterClass(ASTCDCompilationUnit scopeCD, ASTCDCompilationUnit symbolCd) {
    return symbols2JsonDecorator.decorate(scopeCD, symbolCd);
  }

  protected Optional<ASTCDClass> createScopesGenitorDelegator(ASTCDCompilationUnit astCD){
    return scopesGenitorDelegatorDecorator.decorate(astCD);
  }

  protected Optional<ASTCDClass> createScopesGenitor(ASTCDCompilationUnit astCD){
    return scopesGenitorDecorator.decorate(astCD);
  }

}
