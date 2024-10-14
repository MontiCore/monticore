/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._symboltable.CDPackageSymbol;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._symboltable.ICDBasisArtifactScope;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.cd2java.exception.DecorateException;
import de.monticore.codegen.cd2java.exception.DecoratorErrorCode;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.umlstereotype._ast.ASTStereoValue;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.*;
import static de.monticore.codegen.cd2java._ast.constants.ASTConstantsDecorator.LITERALS_SUFFIX;
import static de.monticore.codegen.cd2java.cli.CLIConstants.CLI_SUFFIX;
import static de.monticore.codegen.cd2java.mill.MillConstants.MILL_SUFFIX;
import static de.monticore.codegen.prettyprint.PrettyPrinterConstants.*;
import static de.se_rwth.commons.Names.getQualifier;

public class AbstractService<T extends AbstractService> {

  protected final DiagramSymbol cdSymbol;


  protected final MCTypeFacade mcTypeFacade;

  protected final DecorationHelper decorationHelper;

  public AbstractService(final ASTCDCompilationUnit compilationUnit) {
    this(compilationUnit.getCDDefinition().getSymbol());
  }

  public AbstractService(final DiagramSymbol cdSymbol) {
    this.cdSymbol = cdSymbol;
    this.mcTypeFacade = MCTypeFacade.getInstance();
    this.decorationHelper = DecorationHelper.getInstance();
  }

  public DiagramSymbol getCDSymbol() {
    return this.cdSymbol;
  }

  public DecorationHelper getDecorationHelper() {
    return decorationHelper;
  }

  protected MCTypeFacade getMCTypeFacade() {
    return this.mcTypeFacade;
  }

  public Collection<DiagramSymbol> getAllCDs() {
    return Stream.of(Collections.singletonList(getCDSymbol()), getSuperCDsTransitive())
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  /**
   * different methods for getting a list od super ClassDiagrams
   */
  public Collection<DiagramSymbol> getSuperCDsDirect() {
    return getSuperCDsDirect(getCDSymbol());
  }

  public Collection<DiagramSymbol> getSuperCDsDirect(DiagramSymbol cdSymbol) {
    // get direct parent CDSymbols
    List<DiagramSymbol> superCDs = ((ICDBasisArtifactScope) cdSymbol.getEnclosingScope()).getImportsList().stream()
        .map(i -> i.getStatement())
        .map(this::resolveCD)
        .collect(Collectors.toList());
    return superCDs;
  }

  public List<DiagramSymbol> getSuperCDsTransitive() {
    return getSuperCDsTransitive(getCDSymbol());
  }

  protected final LoadingCache<DiagramSymbol, List<DiagramSymbol>> superCDsTransitiveCache = CacheBuilder.newBuilder()
          .maximumSize(10000)
          .build(new CacheLoader<DiagramSymbol, List<DiagramSymbol>>() {
            @Override
            public List<DiagramSymbol> load(@Nonnull DiagramSymbol cdSymbol) {
              return getSuperCDsTransitiveUncached(cdSymbol);
            }
          });

  // Cache this methods return value
  protected List<DiagramSymbol> getSuperCDsTransitiveUncached(DiagramSymbol cdSymbol) {
    // get direct parent CDSymbols
    List<DiagramSymbol> directSuperCdSymbols = ((ICDBasisArtifactScope) cdSymbol.getEnclosingScope()).getImportsList().stream()
            .map(i -> i.getStatement())
            .filter(i -> !isJava(i))
            .map(AbstractService.this::resolveCD)
            .collect(Collectors.toList());
    // search for super Cds in super Cds
    List<DiagramSymbol> resolvedCds = new ArrayList<>(directSuperCdSymbols);
    for (DiagramSymbol superSymbol : directSuperCdSymbols) {
      List<DiagramSymbol> superCDs = getSuperCDsTransitive(superSymbol);
      for (DiagramSymbol superCD : superCDs) {
        if (resolvedCds
                .stream()
                .noneMatch(c -> c.getFullName().equals(superCD.getFullName()))) {
          resolvedCds.add(superCD);
        }
      }
    }
    return resolvedCds;
  }

  public List<DiagramSymbol> getSuperCDsTransitive(DiagramSymbol cdSymbol) {
    return this.superCDsTransitiveCache.getUnchecked(cdSymbol);
  }

  public List<CDTypeSymbol> getAllCDTypes() {
    return getAllCDTypes(getCDSymbol());
  }

  public List<CDTypeSymbol> getAllCDTypes(DiagramSymbol cdSymbol) {
    List<CDPackageSymbol> directPackages = ((ICDBasisArtifactScope) cdSymbol.getEnclosingScope()).getLocalCDPackageSymbols().stream().collect(Collectors.toList());
    List<CDTypeSymbol> types = Lists.newArrayList();
    directPackages.forEach(p -> types.addAll(p.getSpannedScope().getLocalCDTypeSymbols()));
    types.addAll(((ICDBasisArtifactScope) cdSymbol.getEnclosingScope()).getLocalCDTypeSymbols());
    return types;
  }

  /**
   * methods for super CDTypes (CDClass and CDInterface)
   */
  public List<String> getAllSuperClassesTransitive(ASTCDClass astcdClass) {
    return getAllSuperClassesTransitive(astcdClass.getSymbol())
        .stream()
        .map(s -> createASTFullName(s))
        .collect(Collectors.toList());
  }

  protected List<CDTypeSymbol> getAllSuperClassesTransitive(CDTypeSymbol cdTypeSymbol) {
    List<CDTypeSymbol> superSymbolList = new ArrayList<>();
    if (cdTypeSymbol.isPresentSuperClass()) {
      TypeSymbol superSymbol = cdTypeSymbol.getSuperClass().getTypeInfo();
      if (superSymbol instanceof TypeSymbolSurrogate) {
        if (!((TypeSymbolSurrogate) superSymbol).checkLazyLoadDelegate()) {
          return superSymbolList;
        }
        superSymbol = ((TypeSymbolSurrogate) superSymbol).lazyLoadDelegate();
      }

      superSymbolList.add((CDTypeSymbol) superSymbol);
      superSymbolList.addAll(getAllSuperClassesTransitive((CDTypeSymbol) superSymbol));
    }
    return superSymbolList;
  }

  public List<String> getAllSuperInterfacesTransitive(CDTypeSymbol cdTypeSymbol) {
    List<String> superSymbolList = new ArrayList<>();
        List<CDTypeSymbol> localSuperInterfaces = Lists.newArrayList();
        cdTypeSymbol.getSuperTypesList().stream()
                .filter(s -> !isTFInterface(s))
            .filter(s -> ((TypeSymbolSurrogate)s.getTypeInfo()).checkLazyLoadDelegate())
                    .map(s -> ((TypeSymbolSurrogate)s.getTypeInfo()).lazyLoadDelegate())
            .forEach(t -> {if(t instanceof CDTypeSymbol && ((CDTypeSymbol)t).isIsInterface()) localSuperInterfaces.add((CDTypeSymbol) t);});
    for (CDTypeSymbol superInterface : localSuperInterfaces) {
      superSymbolList.add(createASTFullName(superInterface));
      superSymbolList.addAll(getAllSuperInterfacesTransitive(superInterface));
    }
    return superSymbolList;
  }

  protected boolean isTFInterface(SymTypeExpression s) {
    return s.print().startsWith("de.monticore.tf.ast");
  }

  /**
   * use symboltabe to resolve for ClassDiagrams or CDTypes
   */
  public DiagramSymbol resolveCD(String qualifiedName) {
    Set<DiagramSymbol> symbols = Sets.newHashSet(CD4CodeMill.globalScope().resolveDiagramMany(qualifiedName));
    if (symbols.size() == 1) {
      return symbols.iterator().next();
    } else {
      throw new DecorateException(DecoratorErrorCode.CD_SYMBOL_NOT_FOUND, qualifiedName);
    }
  }

  public CDTypeSymbol resolveCDType(String qualifiedName) {
    return CD4CodeMill.globalScope().resolveCDType(qualifiedName)
        .orElseThrow(() -> new DecorateException(DecoratorErrorCode.CD_SYMBOL_NOT_FOUND, qualifiedName));
  }

  /**
   * getters for attributes of the ClassDiagram
   */
  public String getCDName() {
    return getCDSymbol().getName();
  }

  public String getPackage() {
    return getPackage(getCDSymbol());
  }

  public String getPackage(DiagramSymbol cdSymbol) {
    if (cdSymbol.getPackageName().isEmpty()) {
      return String.join(".", cdSymbol.getName(), getSubPackage()).toLowerCase();
    }
    return String.join(".", cdSymbol.getPackageName(), cdSymbol.getName(), getSubPackage()).toLowerCase();
  }

  public String getSubPackage() {
    return "";
  }

  public String getQualifiedCDName() {
    return Names.getQualifiedName(getCDSymbol().getPackageName(), getCDName());
  }

  /**
   * get Collection of Services from the Super ClassDiagrams
   */
  public Collection<T> getServicesOfSuperCDs() {
    return getSuperCDsTransitive().stream()
        .map(this::createService)
        .collect(Collectors.toList());
  }

  /**
   * method should be overwritten in SubClasses of the AbstractService to return the correct type
   */
  protected T createService(DiagramSymbol cdSymbol) {
    return (T) new AbstractService(cdSymbol);
  }

  /**
   * helper methods to find and get the stereotype of a modifier
   */
  public boolean hasStereotype(ASTModifier modifier, MC2CDStereotypes stereotype) {
    if (modifier.isPresentStereotype()) {
      return modifier.getStereotype().getValuesList().stream().anyMatch(v -> v.getName().equals(stereotype.toString()));
    }
    return false;
  }

  protected List<String> getStereotypeValues(ASTModifier modifier, MC2CDStereotypes stereotype) {
    List<String> values = Lists.newArrayList();
    if (modifier.isPresentStereotype()) {
      modifier.getStereotype().getValuesList().stream()
          .filter(value -> value.getName().equals(stereotype.toString()))
          .filter(ASTStereoValue::isPresentText)
          .forEach(value -> values.add(value.getValue()));
    }
    return values;
  }

  /**
   * methods for determination and access to special stereotypes
   */

  public boolean hasScopeStereotype(ASTModifier modifier) {
    return hasStereotype(modifier, MC2CDStereotypes.SCOPE);
  }

  public boolean hasSymbolStereotype(ASTModifier modifier) {
    return hasStereotype(modifier, MC2CDStereotypes.SYMBOL);
  }

  public boolean hasSymbolStereotype(ASTCDType type) {
    return hasStereotype(type.getModifier(), MC2CDStereotypes.SYMBOL);
  }

  public boolean isMethodBodyPresent(ASTCDMethod method) {
    return hasStereotype(method.getModifier(), MC2CDStereotypes.METHOD_BODY);
  }

  public boolean isReferencedSymbolAttribute(ASTCDAttribute attribute) {
    return hasStereotype(attribute.getModifier(), MC2CDStereotypes.REFERENCED_SYMBOL_ATTRIBUTE);
  }

  public String getMethodBody(ASTCDMethod method) {
    if (method.getModifier().isPresentStereotype()) {
      List<String> stereotypeValues = getStereotypeValues(method.getModifier(), MC2CDStereotypes.METHOD_BODY);
      return stereotypeValues.stream().reduce((a, b) -> a + " \n " + b).orElse("");
    }
    return "";
  }

  public boolean isInheritedAttribute(ASTCDAttribute attribute) {
    return hasStereotype(attribute.getModifier(), MC2CDStereotypes.INHERITED);
  }

  public String getInheritedGrammarName(ASTCDAttribute attribute) {
    return getStereotypeValues(attribute.getModifier(), MC2CDStereotypes.INHERITED).get(0);
  }

  public boolean hasDeprecatedStereotype(ASTModifier modifier) {
    return hasStereotype(modifier, MC2CDStereotypes.DEPRECATED);
  }

  public boolean hasComponentStereotype(ASTModifier modifier) {
    return hasStereotype(modifier, MC2CDStereotypes.COMPONENT);
  }

  public boolean hasLeftRecursiveStereotype(ASTModifier modifier){
    return hasStereotype(modifier, MC2CDStereotypes.LEFT_RECURSIVE);
  }

  public boolean hasExternalInterfaceStereotype(ASTModifier modifier){
    return hasStereotype(modifier, MC2CDStereotypes.EXTERNAL_INTERFACE);
  }

  public boolean hasExternalStereotype(ASTModifier modifier) {
    return hasStereotype(modifier, MC2CDStereotypes.EXTERNAL_TYPE);
  }

  public boolean hasStartProd() {
    return getStartProd().isPresent();
  }

  public boolean hasStartProd(ASTCDDefinition astcdDefinition) {
    if (hasStartProdStereotype(astcdDefinition.getModifier())) {
      return true;
    }
    for (ASTCDClass prod : astcdDefinition.getCDClassesList()) {
      if (hasStereotype(prod.getModifier(), MC2CDStereotypes.START_PROD)) {
        return true;
      }
    }
    for (ASTCDInterface prod : astcdDefinition.getCDInterfacesList()) {
      if (hasStereotype(prod.getModifier(), MC2CDStereotypes.START_PROD)) {
        return true;
      }
    }
    return false;
  }

  public Optional<String> getStartProd() {
    if(this.getCDSymbol().isPresentAstNode()){
      return getStartProd((ASTCDDefinition) this.getCDSymbol().getAstNode());
    }
    else return Optional.empty();
  }

  public Optional<String> getStartProd(ASTCDDefinition astcdDefinition) {
    if (hasStartProdStereotype(astcdDefinition.getModifier())) {
      return getStartProdValue(astcdDefinition.getModifier());
    }
    for (ASTCDClass prod : astcdDefinition.getCDClassesList()) {
      if (hasStereotype(prod.getModifier(), MC2CDStereotypes.START_PROD)) {
        return Optional.of(getCDSymbol().getPackageName() + "." + getCDName() + "." + prod.getName());
      }
    }
    for (ASTCDInterface prod : astcdDefinition.getCDInterfacesList()) {
      if (hasStereotype(prod.getModifier(), MC2CDStereotypes.START_PROD)) {
        return Optional.of(getCDSymbol().getPackageName() + "." + getCDName() + "." + prod.getName());
      }
    }
    return Optional.empty();
  }

  public Optional<String> getStartProdASTFullName(){
    return getStartProdASTFullName((ASTCDDefinition) cdSymbol.getAstNode());
  }

  public Optional<String> getStartProdASTFullName(ASTCDDefinition astcdDefinition) {
    Optional<String> startProd = getStartProd(astcdDefinition);
    if (startProd.isPresent()) {
      String simpleName = Names.getSimpleName(startProd.get());
      simpleName = simpleName.startsWith(AST_PREFIX) ? simpleName : AST_PREFIX + simpleName;
      String startProdAstName = getQualifier(startProd.get()).toLowerCase() + "." + AST_PACKAGE + "." + simpleName;
      return Optional.of(startProdAstName);
    }
    return Optional.empty();
  }

  public Optional<String> getStartProdValue(ASTModifier modifier) {
    List<String> stereotypeValues = getStereotypeValues(modifier, MC2CDStereotypes.START_PROD);
    if (!stereotypeValues.isEmpty()) {
      return Optional.ofNullable(stereotypeValues.get(0));
    }
    return Optional.empty();
  }

  public boolean hasStartProdStereotype(ASTModifier modifier) {
    return hasStereotype(modifier, MC2CDStereotypes.START_PROD);
  }

  public Optional<String> getDeprecatedStereotypeValue(ASTModifier modifier) {
    List<String> stereotypeValues = getStereotypeValues(modifier, MC2CDStereotypes.DEPRECATED);
    if (stereotypeValues.size() >= 1) {
      return Optional.of(stereotypeValues.get(0));
    } else {
      return Optional.empty();
    }
  }

  public void addDeprecatedStereotype(ASTModifier modifier, Optional<String> deprecatedValue) {
    if (!modifier.isPresentStereotype()) {
      modifier.setStereotype(CD4AnalysisMill.stereotypeBuilder().build());
    }
    List<ASTStereoValue> stereoValueList = modifier.getStereotype()
        .getValuesList();
    ASTStereoValue stereoValue = CD4AnalysisMill.stereoValueBuilder()
            .setName(MC2CDStereotypes.DEPRECATED.toString()).uncheckedBuild();
    if (deprecatedValue.isPresent()) {
      stereoValue.setText(
              CD4AnalysisMill.stringLiteralBuilder().setSource(deprecatedValue.get()).build());
    }
    stereoValueList.add(stereoValue);
  }

  /**
   * method checks if the given modifier is deprecated
   * is deprecated -> new public modifier with deprecation stereotype returned
   * is NOT deprecated -> new normal public modifier defined
   *
   * @param givenModifier is the modifier which determines if the new modifier should be deprecated or not
   * @return
   */
  public ASTModifier createModifierPublicModifier(ASTModifier givenModifier) {
    ASTModifier newASTModifier = PUBLIC.build();
    if (hasDeprecatedStereotype(givenModifier)) {
      addDeprecatedStereotype(newASTModifier, getDeprecatedStereotypeValue(givenModifier));
      return newASTModifier;
    }
    return newASTModifier;
  }


  /**
   * checking for duplicate classes and methods
   */
  public boolean isClassOverwritten(ASTCDType astcdClass, List<ASTCDClass> classList) {
    //if there is a Class with the same name in the current CompilationUnit, then the methods are only generated once
    return classList.stream().anyMatch(x -> x.getName().endsWith(astcdClass.getName()));
  }

  public boolean isClassOverwritten(String className, List<ASTCDClass> classList) {
    //if there is a Class with the same name in the current CompilationUnit, then the methods are only generated once
    return classList.stream().anyMatch(x -> x.getName().equals(className));
  }

  public boolean isMethodAlreadyDefined(String methodname, List<ASTCDMethod> definedMethods) {
    return definedMethods
        .stream()
        .anyMatch(x -> x.getName().equals(methodname));
  }

  public boolean isMethodAlreadyDefined(ASTCDMethod method, List<ASTCDMethod> definedMethods) {
    return definedMethods
        .stream()
        .anyMatch(x -> isSameMethodSignature(method, x));
  }

  public List<ASTCDMethod> getMethodListWithoutDuplicates(List<ASTCDMethod> astRuleMethods, List<ASTCDMethod> attributeMethods) {
    List<ASTCDMethod> methodList = new ArrayList<>(attributeMethods);
    for (int i = 0; i < astRuleMethods.size(); i++) {
      ASTCDMethod cdMethod = astRuleMethods.get(i);
      for (int j = 0; j < attributeMethods.size(); j++) {
        if (isSameMethodSignature(cdMethod, attributeMethods.get(j))) {
          methodList.remove(attributeMethods.get(j));
        }
      }
    }
    return methodList;
  }

  protected boolean isSameMethodSignature(ASTCDMethod method1, ASTCDMethod method2) {
    if (!method1.getName().equals(method2.getName()) || method1.sizeCDParameters() != method2.sizeCDParameters()) {
      return false;
    }
    for (int i = 0; i < method1.getCDParameterList().size(); i++) {
      if (!method1.getCDParameter(i).getMCType().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter())
          .equals(method2.getCDParameter(i).getMCType().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()))) {
        return false;
      }
    }
    return true;
  }

  /**
   * Determines whether a enum is the Literals Enum e.g. AutomataLiterals
   * Sometimes the enum for Literals, which is always generated has to be excluded
   */
  public boolean isLiteralsEnum(ASTCDEnum astcdEnum, String definitionName) {
    String enumName = astcdEnum.getName();
    // remove package
    if (astcdEnum.getName().contains(".")) {
      enumName = enumName.substring(enumName.lastIndexOf(".") + 1);
    }
    return enumName.equals(definitionName + LITERALS_SUFFIX);
  }

  /**
   * methods return Name of the LanguageInterface for this.cdSymbol e.g. ASTAutomataNode
   */
  public String getLanguageInterfaceName() {
    return getASTPackage() + "." + AST_PREFIX + getCDName() + NODE_SUFFIX;
  }

  public String getSimpleLanguageInterfaceName() {
    return AST_PREFIX + getCDName() + NODE_SUFFIX;
  }

  /**
   * returns package with the '_ast' part
   */
  public String getASTPackage() {
    return getASTPackage(getCDSymbol());
  }

  public String getASTPackage(DiagramSymbol cdSymbol) {
    if (cdSymbol.getPackageName().isEmpty()) {
      return String.join(".", cdSymbol.getName(), AST_PACKAGE).toLowerCase();
    }
    return String.join(".", cdSymbol.getPackageName(), cdSymbol.getName(), AST_PACKAGE).toLowerCase();
  }

  /**
   * adds the '_ast' package to a fullName to create an valid AST-package
   */
  public String createASTFullName(CDTypeSymbol typeSymbol) {
    ICDBasisScope scope = typeSymbol.getEnclosingScope();
    List<DiagramSymbol> diagramSymbols = scope.getLocalDiagramSymbols();
    while (diagramSymbols.isEmpty()) {
      scope = scope.getEnclosingScope();
      diagramSymbols = scope.getLocalDiagramSymbols();
    }
    return Joiners.DOT.join(getASTPackage(diagramSymbols.get(0)), typeSymbol.getName());
  }

  public String getGeneratedErrorCode(String name) {
    // Use the string representation
    String codeString = getPackage() + getCDSymbol().getName() + name;

    //calculate hashCode, but limit the values to have at most 5 digits
    int hashCode = Math.abs(codeString.hashCode() % 100000);
    //use String formatting to add leading zeros to always have 5 digits
    String errorCodeSuffix = String.format("%05d", hashCode);
    return "x" + errorCodeSuffix;
  }


  /**
   * Mill class names e.g. AutomataMill
   */

  public String getMillSimpleName(DiagramSymbol cdSymbol) {
    return cdSymbol.getName() + MILL_SUFFIX;
  }

  public String getMillSimpleName() {
    return getMillSimpleName(getCDSymbol());
  }

  public String getMillFullName(DiagramSymbol cdSymbol) {
    if (cdSymbol.getPackageName().isEmpty()) {
      return cdSymbol.getName().toLowerCase() + "." + getMillSimpleName(cdSymbol);
    }else {
      return String.join(".", cdSymbol.getPackageName(), cdSymbol.getName()).toLowerCase() + "." + getMillSimpleName(cdSymbol);
    }
  }

  public String getMillFullName() {
    return getMillFullName(getCDSymbol());
  }

  /**
   * Cli class names e.g. AutomataCli
   */

  public String getCliSimpleName(DiagramSymbol cdSymbol) {
    return cdSymbol.getName() + CLI_SUFFIX;
  }

  public String getCliSimpleName() {
    return getCliSimpleName(getCDSymbol());
  }

  public String getCliFullName(DiagramSymbol cdSymbol) {
    if (cdSymbol.getPackageName().isEmpty()) {
      return cdSymbol.getName().toLowerCase() + "." + getCliSimpleName(cdSymbol);
    }else {
      return String.join(".", cdSymbol.getPackageName(), cdSymbol.getName()).toLowerCase() + "." + getCliSimpleName(cdSymbol);
    }
  }

  public String getCliFullName() {
    return getCliFullName(getCDSymbol());
  }

  public String getPrettyPrinterSimpleName() {
    return getPrettyPrinterSimpleName(getCDSymbol());
  }

  public String getPrettyPrinterSimpleName(DiagramSymbol cdSymbol) {
    return cdSymbol.getName() + PRETTYPRINTER_SUFFIX;
  }

  public String getPrettyPrinterFullName() {
    return getPrettyPrinterFullName(getCDSymbol());
  }

  public String getPrettyPrinterFullName(DiagramSymbol cdSymbol) {
    if(cdSymbol.getPackageName().isEmpty()) {
      return String.join(".", cdSymbol.getName().toLowerCase(), PRETTYPRINT_PACKAGE, getPrettyPrinterSimpleName(cdSymbol));
    }else{
      return String.join(".", cdSymbol.getPackageName(), cdSymbol.getName().toLowerCase(), PRETTYPRINT_PACKAGE, getPrettyPrinterSimpleName(cdSymbol));
    }
  }

  public String getFullPrettyPrinterSimpleName(){
    return getFullPrettyPrinterSimpleName(getCDSymbol());
  }

  public String getFullPrettyPrinterSimpleName(DiagramSymbol cdSymbol){
    return cdSymbol.getName() + FULLPRETTYPRINTER_SUFFIX;
  }

  public String getFullPrettyPrinterFullName() {
    return getFullPrettyPrinterFullName(getCDSymbol());
  }

  public String getFullPrettyPrinterFullName(DiagramSymbol cdSymbol) {
    if(cdSymbol.getPackageName().isEmpty()) {
      return String.join(".", cdSymbol.getName().toLowerCase(), PRETTYPRINT_PACKAGE, getFullPrettyPrinterSimpleName(cdSymbol));
    }else{
      return String.join(".", cdSymbol.getPackageName(), cdSymbol.getName().toLowerCase(), PRETTYPRINT_PACKAGE, getFullPrettyPrinterSimpleName(cdSymbol));
    }
  }

  public boolean isJava(String name) {
    return "java.lang".equals(name);
  }
}
