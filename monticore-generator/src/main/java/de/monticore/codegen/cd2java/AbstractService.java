package de.monticore.codegen.cd2java;

import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.codegen.cd2java.exception.DecorateException;
import de.monticore.codegen.cd2java.exception.DecoratorErrorCode;
import de.monticore.codegen.cd2java.factories.CDTypeFacade;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.se_rwth.commons.JavaNamesHelper;
import de.se_rwth.commons.Names;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AbstractService<T extends AbstractService> {

  private final CDDefinitionSymbol cdSymbol;

  private final CDTypeFacade cdTypeFacade;


  public AbstractService(final ASTCDCompilationUnit compilationUnit) {
    this(compilationUnit.getCDDefinition().getCDDefinitionSymbol());
  }

  public AbstractService(final CDDefinitionSymbol cdSymbol) {
    this.cdSymbol = cdSymbol;
    this.cdTypeFacade = CDTypeFacade.getInstance();
  }

  public CDDefinitionSymbol getCDSymbol() {
    return this.cdSymbol;
  }

  protected CDTypeFacade getCDTypeFactory() {
    return this.cdTypeFacade;
  }

  public Collection<CDDefinitionSymbol> getAllCDs() {
    return Stream.of(Collections.singletonList(getCDSymbol()), getSuperCDs())
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  public Collection<CDDefinitionSymbol> getSuperCDs() {
    return getSuperCDs(getCDSymbol());
  }

  public Collection<CDDefinitionSymbol> getSuperCDs(CDDefinitionSymbol cdSymbol) {
    // get direct parent CDSymbols
    List<CDDefinitionSymbol> directSuperCdSymbols = cdSymbol.getImports().stream()
        .map(this::resolveCD)
        .collect(Collectors.toList());
    // search for super Cds in super Cds
    List<CDDefinitionSymbol> resolvedCds = new ArrayList<>(directSuperCdSymbols);
    for (CDDefinitionSymbol superSymbol : directSuperCdSymbols) {
      Collection<CDDefinitionSymbol> superCDs = getSuperCDs(superSymbol);
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

  public String getCDName() {
    return getCDSymbol().getName();
  }

  private String getBasePackage(CDDefinitionSymbol cdSymbol) {
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
    return getSuperCDs().stream()
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
    if (astType instanceof ASTMCBasicGenericType && ((ASTMCBasicGenericType) astType).getMCTypeArgumentList().size() == 1 ) {
      return ((ASTMCBasicGenericType) astType).getMCTypeArgumentList().get(0).getMCTypeOpt().get().printType();
    }
    return astType.printType();
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
    return nativeAttributeType.contains(".") ? nativeAttributeType.substring(nativeAttributeType.lastIndexOf(".") + 1) :
        nativeAttributeType;
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
    modifier.getStereotype().getValueList().stream()
        .filter(value -> value.getName().equals(stereotype.toString()))
        .filter(ASTCDStereoValue::isPresentValue)
        .forEach(value -> values.add(value.getValue()));
    return values;
  }


  public boolean isClassOverwritten(ASTCDClass astcdClass, List<ASTCDClass> classList) {
    //if there is a Class with the same name in the current CompilationUnit, then the methods are only generated once
    return classList.stream().anyMatch(x -> x.getName().endsWith(astcdClass.getName()));
  }

  public boolean isMethodAlreadyDefined(String methodname, List<ASTCDMethod> definedMethods) {
    //if there is a Class with the same name in the current CompilationUnit, then the methods are only generated once
    return definedMethods
        .stream()
        .anyMatch(x -> x.getName().equals(methodname));
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
      if (!method1.getCDParameter(i).getMCType().printType().equals(method2.getCDParameter(i).getMCType().printType())) {
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
      Collection<CDDefinitionSymbol> superCDs = getSuperCDs(resolveCD(astcdDefinition.getName()));
      for (CDDefinitionSymbol superCD : superCDs) {
        if (superCD.getTypes().stream().anyMatch(x -> x.getName().equals(simpleNativeAttributeType))) {
          return superCD.getName() + "PackageImpl";
        }
      }
    }
    return "this";
  }

}
