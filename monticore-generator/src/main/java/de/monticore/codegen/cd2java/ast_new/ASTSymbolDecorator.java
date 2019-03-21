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

public class ASTSymbolDecorator extends AbstractDecorator<ASTCDClass, ASTCDClass> {

  private static final String SYMBOL_SUFFIX = "Symbol";

  private static final String SYMBOLTABLE_PACKAGE = "._symboltable.";

  private final ASTCDCompilationUnit compilationUnit;

  private final MethodDecorator methodDecorator;

  public ASTSymbolDecorator(final GlobalExtensionManagement glex, final ASTCDCompilationUnit compilationUnit, final MethodDecorator methodDecorator) {
    super(glex);
    this.compilationUnit = compilationUnit;
    this.methodDecorator = methodDecorator;
  }

  @Override
  public ASTCDClass decorate(final ASTCDClass clazz) {
    if (isSymbolClass(clazz)) {
      String symbolTablePackage = (String.join(".", compilationUnit.getPackageList()) + "." + compilationUnit.getCDDefinition().getName() + SYMBOLTABLE_PACKAGE).toLowerCase();
      ASTType symbolType = this.getCDTypeFactory().createOptionalTypeOf(symbolTablePackage + clazz.getName().replaceFirst("AST", "") + SYMBOL_SUFFIX);
      String attributeName = StringUtils.uncapitalize(clazz.getName().replaceFirst("AST", "")) + SYMBOL_SUFFIX;
      ASTCDAttribute symbolAttribute = this.getCDAttributeFactory().createAttribute(PROTECTED, symbolType, attributeName);
      clazz.addCDAttribute(symbolAttribute);
      clazz.addAllCDMethods(methodDecorator.decorate(symbolAttribute));
    }
    return clazz;
  }

  protected boolean isSymbolClass(ASTCDClass ast) {
    if (ast.isPresentModifier() && ast.getModifier().isPresentStereotype()) {
      return ast.getModifier().getStereotype().getValueList().stream().anyMatch(v -> v.getName().equals(MC2CDStereotypes.SYMBOL.toString()));
    }
    return false;
  }
}