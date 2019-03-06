package de.monticore.codegen.cd2java.ast_new;

import de.monticore.codegen.cd2java.Decorator;
import de.monticore.codegen.cd2java.factories.CDAttributeFactory;
import de.monticore.codegen.cd2java.factories.CDTypeFactory;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import org.apache.commons.lang3.StringUtils;

import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;

public class ASTScopeDecorator implements Decorator<ASTCDClass, ASTCDClass> {

  private static final String SCOPE_SUFFIX = "Scope";

  private static final String SYMBOLTABLE_PACKAGE = "._symboltable.";

  private final GlobalExtensionManagement glex;

  private final ASTCDCompilationUnit compilationUnit;

  private final CDTypeFactory cdTypeFactory;

  private final CDAttributeFactory cdAttributeFactory;

  public ASTScopeDecorator(final GlobalExtensionManagement glex, final ASTCDCompilationUnit compilationUnit) {
    this.glex = glex;
    this.compilationUnit = compilationUnit;
    this.cdTypeFactory = CDTypeFactory.getInstance();
    this.cdAttributeFactory = CDAttributeFactory.getInstance();
  }

  @Override
  public ASTCDClass decorate(final ASTCDClass clazz) {
    ASTCDClass decoratedClass = clazz.deepClone();
    if (isScopeClass(decoratedClass)) {
      String symbolTablePackage = (String.join(".", compilationUnit.getPackageList()) + "." + compilationUnit.getCDDefinition().getName() + SYMBOLTABLE_PACKAGE).toLowerCase();
      ASTType scopeType = this.cdTypeFactory.createOptionalTypeOf(symbolTablePackage + compilationUnit.getCDDefinition().getName() + SCOPE_SUFFIX);
      String attributeName = StringUtils.uncapitalize(compilationUnit.getCDDefinition().getName()) + SCOPE_SUFFIX;
      ASTCDAttribute scopeAttribute = this.cdAttributeFactory.createAttribute(PROTECTED, scopeType, attributeName);
      decoratedClass.addCDAttribute(scopeAttribute);
      decoratedClass.addAllCDMethods(new MethodDecorator(this.glex).decorate(scopeAttribute));
    }
    return decoratedClass;
  }

  private boolean isScopeClass(final ASTCDClass clazz) {
    if (clazz.isPresentModifier() && clazz.getModifier().isPresentStereotype()) {
      return clazz.getModifier().getStereotype().getValueList().stream().anyMatch(v -> v.getName().equals(MC2CDStereotypes.SCOPE.toString()));
    }
    return false;
  }
}