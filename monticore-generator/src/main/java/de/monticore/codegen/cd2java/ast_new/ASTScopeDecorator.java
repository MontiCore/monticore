package de.monticore.codegen.cd2java.ast_new;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import org.apache.commons.lang3.StringUtils;

import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;

public class ASTScopeDecorator extends AbstractDecorator<ASTCDClass, ASTCDClass> {

  private static final String SCOPE_SUFFIX = "Scope";

  private static final String SYMBOLTABLE_PACKAGE = "._symboltable.";

  private final ASTCDCompilationUnit compilationUnit;

  public ASTScopeDecorator(final GlobalExtensionManagement glex, final ASTCDCompilationUnit compilationUnit) {
    super(glex);
    this.compilationUnit = compilationUnit;
  }

  @Override
  public ASTCDClass decorate(final ASTCDClass clazz) {
    ASTCDClass decoratedClass = clazz.deepClone();
    if (isScopeClass(decoratedClass)) {
      String symbolTablePackage = (String.join(".", compilationUnit.getPackageList()) + "." + compilationUnit.getCDDefinition().getName() + SYMBOLTABLE_PACKAGE).toLowerCase();
      ASTType scopeType = this.getCDTypeFactory().createOptionalTypeOf(symbolTablePackage + compilationUnit.getCDDefinition().getName() + SCOPE_SUFFIX);
      String attributeName = StringUtils.uncapitalize(compilationUnit.getCDDefinition().getName()) + SCOPE_SUFFIX;
      ASTCDAttribute scopeAttribute = this.getCDAttributeFactory().createAttribute(PROTECTED, scopeType, attributeName);
      decoratedClass.addCDAttribute(scopeAttribute);
      decoratedClass.addAllCDMethods(new MethodDecorator(this.getGlex()).decorate(scopeAttribute));
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