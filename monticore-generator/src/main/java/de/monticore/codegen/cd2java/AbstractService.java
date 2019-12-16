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
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mcfullgenerictypes._ast.MCFullGenericTypesMill;
import de.se_rwth.commons.JavaNamesHelper;
import de.se_rwth.commons.Names;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.*;
import static de.monticore.codegen.cd2java._ast.constants.ASTConstantsDecorator.LITERALS_SUFFIX;

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

  public CDDefinitionSymbol resolveCD(String qualifiedName) {
    return getCDSymbol().getEnclosingScope().<CDDefinitionSymbol>resolveCDDefinition(qualifiedName)
        .orElseThrow(() -> new DecorateException(DecoratorErrorCode.CD_SYMBOL_NOT_FOUND, qualifiedName));
  }

  public CDTypeSymbol resolveCDType(String qualifiedName) {
    return getCDSymbol().getEnclosingScope().<CDDefinitionSymbol>resolveCDType(qualifiedName)
        .orElseThrow(() -> new DecorateException(DecoratorErrorCode.CD_SYMBOL_NOT_FOUND, qualifiedName));
  }

  public String getCDName() {
    return getCDSymbol().getName();
  }

  protected String getBasePackage(CDDefinitionSymbol cdSymbol) {
    return cdSymbol.getPackageName();
  }

  private String getBasePackage() {
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

  public Collection<T> getServicesOfSuperCDs() {
    return getSuperCDsTransitive().stream()
        .map(this::createService)
        .collect(Collectors.toList());
  }

  protected T createService(CDDefinitionSymbol cdSymbol) {
    return (T) new AbstractService(cdSymbol);
  }

  public String getNativeAttributeName(String attributeName) {
    if (!attributeName.startsWith(JavaNamesHelper.PREFIX_WHEN_WORD_IS_RESERVED)) {
      return attributeName;
    }
    return attributeName.substring(JavaNamesHelper.PREFIX_WHEN_WORD_IS_RESERVED.length());
  }

  public String getNativeTypeName(ASTMCType astType) {
    // check if type is Generic type like 'List<automaton._ast.ASTState>' -> returns automaton._ast.ASTState
    // if not generic returns simple Type like 'int'
    if (astType instanceof ASTMCGenericType && ((ASTMCGenericType) astType).getMCTypeArgumentList().size() == 1) {
      return ((ASTMCGenericType) astType).getMCTypeArgumentList().get(0).getMCTypeOpt().get()
              .printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter());
    }
    return astType.printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter());
  }

  public String getSimpleNativeType(ASTMCType astType) {
    // check if type is Generic type like 'List<automaton._ast.ASTState>' -> returns ASTState
    // if not generic returns simple Type like 'int'
    String nativeAttributeType = getNativeTypeName(astType);
    return getSimpleNativeType(nativeAttributeType);
  }

  public String getSimpleNativeType(String nativeAttributeType) {
    // check if type is Generic type like 'List<automaton._ast.ASTState>' -> returns ASTState
    // if not generic returns simple Type like 'int'
    if (nativeAttributeType.contains(".")) {
      nativeAttributeType = nativeAttributeType.substring(nativeAttributeType.lastIndexOf(".") + 1);
    }
    if (nativeAttributeType.contains(">")) {
      nativeAttributeType = nativeAttributeType.replaceAll(">", "");
    }
    return nativeAttributeType;
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

  public boolean isInherited(ASTCDAttribute attribute) {
    return hasStereotype(attribute.getModifier(), MC2CDStereotypes.INHERITED);
  }

  public String getInheritedGrammarName(ASTCDAttribute attribute) {
    return getStereotypeValues(attribute.getModifier(), MC2CDStereotypes.INHERITED).get(0);
  }

  public boolean hasStereotype(ASTModifier modifier, MC2CDStereotypes stereotype) {
    if (modifier.isPresentStereotype()) {
      return modifier.getStereotype().getValueList().stream().anyMatch(v -> v.getName().equals(stereotype.toString()));
    }
    return false;
  }

  protected List<String> getStereotypeValues(ASTModifier modifier,
                                             MC2CDStereotypes stereotype) {
    List<String> values = Lists.newArrayList();
    if (modifier.isPresentStereotype()) {
      modifier.getStereotype().getValueList().stream()
          .filter(value -> value.getName().equals(stereotype.toString()))
          .filter(ASTCDStereoValue::isPresentValue)
          .forEach(value -> values.add(value.getValue()));
    }
    return values;
  }


  public boolean isClassOverwritten(ASTCDClass astcdClass, List<ASTCDClass> classList) {
    //if there is a Class with the same name in the current CompilationUnit, then the methods are only generated once
    return classList.stream().anyMatch(x -> x.getName().endsWith(astcdClass.getName()));
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

  public String getGrammarFromClass(ASTCDDefinition astcdDefinition, ASTCDAttribute astcdAttribute) {
    String simpleNativeAttributeType = getSimpleNativeType(astcdAttribute.getMCType());
    if (astcdDefinition.getCDClassList().stream().anyMatch(x -> x.getName().equals(simpleNativeAttributeType))) {
      return "this";
    } else {
      List<CDDefinitionSymbol> superCDs = getSuperCDsTransitive(resolveCD(astcdDefinition.getName()));
      for (CDDefinitionSymbol superCD : superCDs) {
        if (superCD.getTypes().stream().anyMatch(x -> x.getName().equals(simpleNativeAttributeType))) {
          return superCD.getName() + "PackageImpl";
        }
      }
    }
    return "this";
  }

  public boolean hasScopeStereotype(ASTModifier modifier) {
    return hasStereotype(modifier, MC2CDStereotypes.SCOPE);
  }

  public boolean hasSymbolStereotype(ASTModifier modifier) {
    return hasStereotype(modifier, MC2CDStereotypes.SYMBOL);
  }

  public boolean isLiteralsEnum(ASTCDEnum astcdEnum, String definitionName) {
    String enumName = astcdEnum.getName();
    // remove package
    if (astcdEnum.getName().contains(".")) {
      enumName = enumName.substring(enumName.lastIndexOf(".") + 1);
    }
    return enumName.equals(definitionName + LITERALS_SUFFIX);
  }

  public String getLanguageInterfaceName() {
    return getASTPackage() + "." + AST_PREFIX + getCDName() + NODE_SUFFIX;
  }

  public String getSimleLanguageInterfaceName() {
    return AST_PREFIX + getCDName() + NODE_SUFFIX;
  }

  public String getASTPackage() {
    return getASTPackage(getCDSymbol());
  }

  public String getASTPackage(CDDefinitionSymbol cdSymbol) {
    if (getBasePackage(cdSymbol).isEmpty()) {
      return String.join(".", cdSymbol.getName(), AST_PACKAGE).toLowerCase();
    }
    return String.join(".", getBasePackage(cdSymbol), cdSymbol.getName(), AST_PACKAGE).toLowerCase();
  }

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

  public String createASTFullName(String simpleName) {
    String packageName = simpleName.substring(0, simpleName.lastIndexOf("."));
    packageName = packageName.toLowerCase();
    String astName = simpleName.substring(simpleName.lastIndexOf(".") + 1);
    return packageName + "." + AST_PACKAGE + "." + astName;
  }
}
