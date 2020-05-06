/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java;

import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbolLoader;
import de.monticore.codegen.cd2java.exception.DecorateException;
import de.monticore.codegen.cd2java.exception.DecoratorErrorCode;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcfullgenerictypes._ast.MCFullGenericTypesMill;
import de.se_rwth.commons.Names;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.*;
import static de.monticore.codegen.cd2java._ast.constants.ASTConstantsDecorator.LITERALS_SUFFIX;
import static de.monticore.codegen.cd2java.mill.MillConstants.MILL_SUFFIX;

public class AbstractService<T extends AbstractService> {

  private final CDDefinitionSymbol cdSymbol;

  private final MCTypeFacade mcTypeFacade;

  private final DecorationHelper decorationHelper;

  public AbstractService(final ASTCDCompilationUnit compilationUnit) {
    this(compilationUnit.getCDDefinition().getSymbol());
  }

  public AbstractService(final CDDefinitionSymbol cdSymbol) {
    this.cdSymbol = cdSymbol;
    this.mcTypeFacade = MCTypeFacade.getInstance();
    this.decorationHelper = DecorationHelper.getInstance();
  }

  public CDDefinitionSymbol getCDSymbol() {
    return this.cdSymbol;
  }

  public DecorationHelper getDecorationHelper() {
    return decorationHelper;
  }

  protected MCTypeFacade getMCTypeFacade() {
    return this.mcTypeFacade;
  }

  public Collection<CDDefinitionSymbol> getAllCDs() {
    return Stream.of(Collections.singletonList(getCDSymbol()), getSuperCDsTransitive())
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  /**
   * different methods for getting a list od super ClassDiagrams
   */
  public List<CDDefinitionSymbol> getSuperCDsDirect() {
    return getSuperCDsDirect(getCDSymbol());
  }

  public List<CDDefinitionSymbol> getSuperCDsDirect(CDDefinitionSymbol cdSymbol) {
    // get direct parent CDSymbols
    return cdSymbol.getImports().stream()
        .map(this::resolveCD)
        .collect(Collectors.toList());
  }

  public List<CDDefinitionSymbol> getSuperCDsTransitive() {
    return getSuperCDsTransitive(getCDSymbol());
  }

  public List<CDDefinitionSymbol> getSuperCDsTransitive(CDDefinitionSymbol cdSymbol) {
    // get direct parent CDSymbols
    List<CDDefinitionSymbol> directSuperCdSymbols = cdSymbol.getImports().stream()
        .map(this::resolveCD)
        .collect(Collectors.toList());
    // search for super Cds in super Cds
    List<CDDefinitionSymbol> resolvedCds = new ArrayList<>(directSuperCdSymbols);
    for (CDDefinitionSymbol superSymbol : directSuperCdSymbols) {
      List<CDDefinitionSymbol> superCDs = getSuperCDsTransitive(superSymbol);
      for (CDDefinitionSymbol superCD : superCDs) {
        if (resolvedCds
            .stream()
            .noneMatch(c -> c.getFullName().equals(superCD.getFullName()))) {
          resolvedCds.add(superCD);
        }
      }
    }
    return resolvedCds;
  }

  /**
   * methods for super CDTypes (CDClass and CDInterface)
   */
  public List<String> getAllSuperClassesTransitive(ASTCDClass astcdClass) {
    return getAllSuperClassesTransitive(astcdClass.getSymbol());
  }

  public List<String> getAllSuperClassesTransitive(CDTypeSymbol cdTypeSymbol) {
    List<String> superSymbolList = new ArrayList<>();
    if (cdTypeSymbol.isPresentSuperClass()) {
      String fullName = cdTypeSymbol.getSuperClass().getLoadedSymbol().getFullName();
      superSymbolList.add(createASTFullName(fullName));
      CDTypeSymbol superSymbol = resolveCDType(fullName);
      superSymbolList.addAll(getAllSuperClassesTransitive(superSymbol));
    }
    return superSymbolList;
  }

  public List<String> getAllSuperInterfacesTransitive(ASTCDClass astcdClass) {
    return getAllSuperInterfacesTransitive(astcdClass.getSymbol());
  }

  public List<String> getAllSuperInterfacesTransitive(CDTypeSymbol cdTypeSymbol) {
    List<String> superSymbolList = new ArrayList<>();
    for (CDTypeSymbolLoader cdInterface : cdTypeSymbol.getCdInterfaceList()) {
      String fullName = cdInterface.getLoadedSymbol().getFullName();
      superSymbolList.add(createASTFullName(fullName));
      CDTypeSymbol superSymbol = resolveCDType(fullName);
      superSymbolList.addAll(getAllSuperInterfacesTransitive(superSymbol));
    }
    return superSymbolList;
  }

  /**
   * use symboltabe to resolve for ClassDiagrams or CDTypes
   */
  public CDDefinitionSymbol resolveCD(String qualifiedName) {
    return getCDSymbol().getEnclosingScope().<CDDefinitionSymbol>resolveCDDefinition(qualifiedName)
        .orElseThrow(() -> new DecorateException(DecoratorErrorCode.CD_SYMBOL_NOT_FOUND, qualifiedName));
  }

  public CDTypeSymbol resolveCDType(String qualifiedName) {
    return getCDSymbol().getEnclosingScope().<CDDefinitionSymbol>resolveCDType(qualifiedName)
        .orElseThrow(() -> new DecorateException(DecoratorErrorCode.CD_SYMBOL_NOT_FOUND, qualifiedName));
  }

  /**
   * getters for attributes of the ClassDiagram
   */
  public String getCDName() {
    return getCDSymbol().getName();
  }

  public String getBasePackage(CDDefinitionSymbol cdSymbol) {
    return cdSymbol.getPackageName();
  }

  public String getBasePackage() {
    return getBasePackage(getCDSymbol());
  }

  public String getPackage() {
    return getPackage(getCDSymbol());
  }

  public String getPackage(CDDefinitionSymbol cdSymbol) {
    if (getBasePackage(cdSymbol).isEmpty()) {
      return String.join(".", cdSymbol.getName(), getSubPackage()).toLowerCase();
    }
    return String.join(".", getBasePackage(cdSymbol), cdSymbol.getName(), getSubPackage()).toLowerCase();
  }

  public String getSubPackage() {
    return "";
  }

  public String getQualifiedCDName() {
    return Names.getQualifiedName(getBasePackage(), getCDName());
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
  protected T createService(CDDefinitionSymbol cdSymbol) {
    return (T) new AbstractService(cdSymbol);
  }

  /**
   * helper methods to find and get the stereotype of a modifier
   */
  public boolean hasStereotype(ASTModifier modifier, MC2CDStereotypes stereotype) {
    if (modifier.isPresentStereotype()) {
      return modifier.getStereotype().getValueList().stream().anyMatch(v -> v.getName().equals(stereotype.toString()));
    }
    return false;
  }

  protected List<String> getStereotypeValues(ASTModifier modifier, MC2CDStereotypes stereotype) {
    List<String> values = Lists.newArrayList();
    if (modifier.isPresentStereotype()) {
      modifier.getStereotype().getValueList().stream()
          .filter(value -> value.getName().equals(stereotype.toString()))
          .filter(ASTCDStereoValue::isPresentValue)
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

  public boolean isMethodBodyPresent(ASTCDMethod method) {
    return hasStereotype(method.getModifier(), MC2CDStereotypes.METHOD_BODY);
  }

  public boolean isReferencedSymbolAttribute(ASTCDAttribute attribute) {
    return attribute.isPresentModifier() && hasStereotype(attribute.getModifier(), MC2CDStereotypes.REFERENCED_SYMBOL_ATTRIBUTE);
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
      modifier.setStereotype(CD4AnalysisNodeFactory
          .createASTCDStereotype());
    }
    List<ASTCDStereoValue> stereoValueList = modifier.getStereotype()
        .getValueList();
    ASTCDStereoValue stereoValue = CD4AnalysisNodeFactory
        .createASTCDStereoValue();
    stereoValue.setName(MC2CDStereotypes.DEPRECATED.toString());
    if (deprecatedValue.isPresent()) {
      stereoValue.setValue(deprecatedValue.get());
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

  public String getASTPackage(CDDefinitionSymbol cdSymbol) {
    if (getBasePackage(cdSymbol).isEmpty()) {
      return String.join(".", cdSymbol.getName(), AST_PACKAGE).toLowerCase();
    }
    return String.join(".", getBasePackage(cdSymbol), cdSymbol.getName(), AST_PACKAGE).toLowerCase();
  }

  /**
   * adds the '_ast' package to a fullName to create an valid AST-package
   */
  public String createASTFullName(String simpleName) {
    String packageName = simpleName.substring(0, simpleName.lastIndexOf("."));
    packageName = packageName.toLowerCase();
    String astName = simpleName.substring(simpleName.lastIndexOf(".") + 1);
    return packageName + "." + AST_PACKAGE + "." + astName;
  }

  private int count = 0;

  public String getGeneratedErrorCode(String name) {
    // Use the string representation
    // also use a count to make sure no double codes can appear
    // because sometimes there is not enough information for a unique string
    String codeString = getPackage() + getCDSymbol() + name + count;
    count++;
    int hashCode = Math.abs(codeString.hashCode());
    String errorCodeSuffix = String.valueOf(hashCode);
    return "x" + errorCodeSuffix;
  }


  /**
   * Mill class names e.g. AutomataMill
   */

  public String getMillSimpleName(CDDefinitionSymbol cdSymbol) {
    return cdSymbol.getName() + MILL_SUFFIX;
  }

  public String getMillSimpleName() {
    return getMillSimpleName(getCDSymbol());
  }

  public String getMillFullName(CDDefinitionSymbol cdSymbol) {
    if (getBasePackage(cdSymbol).isEmpty()) {
      return cdSymbol.getName().toLowerCase() + "." + getMillSimpleName(cdSymbol);
    }else {
      return String.join(".", getBasePackage(cdSymbol), cdSymbol.getName()).toLowerCase() + "." + getMillSimpleName(cdSymbol);
    }
  }

  public String getMillFullName() {
    return getMillFullName(getCDSymbol());
  }

}
