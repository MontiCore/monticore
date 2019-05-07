package de.monticore.codegen.cd2java;

import com.google.common.collect.Lists;
import de.monticore.codegen.cd2java.exception.DecorateException;
import de.monticore.codegen.cd2java.exception.DecoratorErrorCode;
import de.monticore.codegen.cd2java.factories.CDTypeFacade;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDStereoValue;
import de.monticore.umlcd4a.cd4analysis._ast.ASTModifier;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.se_rwth.commons.JavaNamesHelper;

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
    return getCDSymbol().getImports().stream()
        .map(this::resolveCD)
        .collect(Collectors.toList());
  }

  public CDSymbol resolveCD(String qualifiedName) {
    return getCDSymbol().getEnclosingScope().<CDSymbol>resolve(qualifiedName, CDSymbol.KIND)
        .orElseThrow(() -> new DecorateException(DecoratorErrorCode.CD_SYMBOL_NOT_FOUND, qualifiedName));
  }

  public String getCDName() {
    return getCDSymbol().getName();
  }

  private String getBasePackage() {
    return getCDSymbol().getPackageName();
  }

  public String getPackage() {
    if (getBasePackage().isEmpty()) {
      return String.join(".", getCDName(), getSubPackage()).toLowerCase();
    }
    return String.join(".", getBasePackage(), getCDName(), getSubPackage()).toLowerCase();
  }

  public String getSubPackage() {
    return "";
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

  public boolean isMethodBodyPresent(ASTCDMethod method) {
    return hasStereotype(method.getModifier(), MC2CDStereotypes.METHOD_BODY);
  }

  public String getMethodBody(ASTCDMethod method) {
    if (method.getModifier().isPresentStereotype()) {
      List<String> stereotypeValues = getStereotypeValues(method.getModifier(), MC2CDStereotypes.METHOD_BODY);
      return stereotypeValues.stream().reduce((a, b) -> a + " \n " + b).orElse("");
    }
    return "";
  }

  protected boolean hasStereotype(ASTModifier modifier, MC2CDStereotypes stereotype) {
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

}
