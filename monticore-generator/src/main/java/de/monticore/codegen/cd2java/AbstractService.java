package de.monticore.codegen.cd2java;

import com.google.common.collect.Lists;
import de.monticore.codegen.cd2java.exception.DecorateException;
import de.monticore.codegen.cd2java.exception.DecoratorErrorCode;
import de.monticore.codegen.cd2java.factories.CDTypeFacade;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.se_rwth.commons.JavaNamesHelper;
import de.se_rwth.commons.Names;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AbstractService<T extends AbstractService> {

  private final CDSymbol cdSymbol;

  private final CDTypeFacade cdTypeFacade;


  public AbstractService(final ASTCDCompilationUnit compilationUnit) {
    this((CDSymbol) compilationUnit.getCDDefinition().getSymbol());
  }

  public AbstractService(final CDSymbol cdSymbol) {
    this.cdSymbol = cdSymbol;
    this.cdTypeFacade = CDTypeFacade.getInstance();
  }

  public CDSymbol getCDSymbol() {
    return this.cdSymbol;
  }

  protected CDTypeFacade getCDTypeFactory() {
    return this.cdTypeFacade;
  }

  public Collection<CDSymbol> getAllCDs() {
    return Stream.of(Collections.singletonList(getCDSymbol()), getSuperCDs())
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  public Collection<CDSymbol> getSuperCDs() {
    return getSuperCDs(getCDSymbol());
  }

  public Collection<CDSymbol> getSuperCDs(CDSymbol cdSymbol) {
    // get direct parent CDSymbols
    List<CDSymbol> directSuperCdSymbols = cdSymbol.getImports().stream()
        .map(this::resolveCD)
        .collect(Collectors.toList());
    // search for super Cds in super Cds
    List<CDSymbol> resolvedCds = new ArrayList<>(directSuperCdSymbols);
    for (CDSymbol superSymbol : directSuperCdSymbols) {
      Collection<CDSymbol> superCDs = getSuperCDs(superSymbol);
      for (CDSymbol superCD : superCDs) {
        if (resolvedCds
            .stream()
            .noneMatch(c -> c.getFullName().equals(superCD.getFullName()))) {
          resolvedCds.add(superCD);
        }
      }
    }
    return resolvedCds;
  }

  public CDSymbol resolveCD(String qualifiedName) {
    return getCDSymbol().getEnclosingScope().<CDSymbol>resolve(qualifiedName, CDSymbol.KIND)
        .orElseThrow(() -> new DecorateException(DecoratorErrorCode.CD_SYMBOL_NOT_FOUND, qualifiedName));
  }

  public String getCDName() {
    return getCDSymbol().getName();
  }

  private String getBasePackage(CDSymbol cdSymbol) {
    return cdSymbol.getPackageName();
  }

  private String getBasePackage() {
    return getBasePackage(getCDSymbol());
  }

  public String getPackage() {
    return getPackage(getCDSymbol());
  }

  public String getPackage(CDSymbol cdSymbol) {
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

  protected T createService(CDSymbol cdSymbol) {
    return (T) new AbstractService(cdSymbol);
  }

  public static String getNativeAttributeName(String attributeName) {
    if (!attributeName.startsWith(JavaNamesHelper.PREFIX_WHEN_WORD_IS_RESERVED)) {
      return attributeName;
    }
    return attributeName.substring(JavaNamesHelper.PREFIX_WHEN_WORD_IS_RESERVED.length());
  }

  public static String getNativeAttributeType(ASTType astType) {
    // check if type is Generic type like 'List<automaton._ast.ASTState>' -> returns automaton._ast.ASTState
    // if not generic returns simple Type like 'int'
    if (astType instanceof ASTSimpleReferenceType && ((ASTSimpleReferenceType) astType).isPresentTypeArguments() &&
        ((ASTSimpleReferenceType) astType).getTypeArguments().sizeTypeArguments() == 1) {
      return TypesPrinter.printTypeArgument(((ASTSimpleReferenceType) astType).getTypeArguments().getTypeArgument(0));
    }
    return TypesPrinter.printType(astType);
  }

  public static String getSimpleNativeAttributeType(ASTType astType) {
    // check if type is Generic type like 'List<automaton._ast.ASTState>' -> returns ASTState
    // if not generic returns simple Type like 'int'
    String nativeAttributeType = getNativeAttributeType(astType);
    return nativeAttributeType.contains(".") ? nativeAttributeType.substring(nativeAttributeType.lastIndexOf(".")+1) :
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
      if (!method1.getCDParameter(i).getType().deepEquals(method2.getCDParameter(i).getType())) {
        return false;
      }
    }
    return true;
  }

}
