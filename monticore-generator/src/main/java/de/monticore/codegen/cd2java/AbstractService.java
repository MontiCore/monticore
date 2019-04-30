package de.monticore.codegen.cd2java;

import de.monticore.codegen.cd2java.exception.DecorateException;
import de.monticore.codegen.cd2java.exception.DecoratorErrorCode;
import de.monticore.codegen.cd2java.factories.CDTypeFacade;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.se_rwth.commons.JavaNamesHelper;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractService<T extends AbstractService> {

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

  protected abstract String getSubPackage();

  public Collection<T> getServicesOfSuperCDs() {
    return getSuperCDs().stream()
        .map(this::createService)
        .collect(Collectors.toList());
  }

  protected abstract T createService(CDSymbol cdSymbol);

  public static String getNativeAttributeName(String attributeName) {
    if (!attributeName.startsWith(JavaNamesHelper.PREFIX_WHEN_WORD_IS_RESERVED)) {
      return attributeName;
    }
    return attributeName.substring(JavaNamesHelper.PREFIX_WHEN_WORD_IS_RESERVED.length());
  }

}
