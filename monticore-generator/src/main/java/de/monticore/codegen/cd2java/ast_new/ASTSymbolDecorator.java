package de.monticore.codegen.cd2java.ast_new;

import de.monticore.codegen.cd2java.Decorator;
import de.monticore.codegen.cd2java.factories.*;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import org.apache.commons.lang3.StringUtils;

import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;

public class ASTSymbolDecorator implements Decorator<ASTCDClass, ASTCDClass> {

  private static final String SYMBOL_SUFFIX = "Symbol";

  private static final String SYMBOLTABLE_PACKAGE = "._symboltable.";


  private final GlobalExtensionManagement glex;

  private final CDTypeFactory cdTypeFactory;

  private final CDAttributeFactory cdAttributeFactory;

  private final ASTCDCompilationUnit compilationUnit;

  public ASTSymbolDecorator(final GlobalExtensionManagement glex, final ASTCDCompilationUnit compilationUnit) {
    this.glex = glex;
    this.compilationUnit = compilationUnit;
    this.cdTypeFactory = CDTypeFactory.getInstance();
    this.cdAttributeFactory = CDAttributeFactory.getInstance();
  }

  @Override
  public ASTCDClass decorate(final ASTCDClass clazz) {
    ASTCDClass decoratedClass = clazz.deepClone();
    if (isSymbolClass(decoratedClass)) {
      String symbolTablePackage = (String.join(".", compilationUnit.getPackageList()) + "." + compilationUnit.getCDDefinition().getName() + SYMBOLTABLE_PACKAGE).toLowerCase();
      ASTType symbolType = this.cdTypeFactory.createOptionalTypeOf(symbolTablePackage + decoratedClass.getName().replaceFirst("AST", "") + SYMBOL_SUFFIX);
      String attributeName = StringUtils.uncapitalize(decoratedClass.getName().replaceFirst("AST", "")) + SYMBOL_SUFFIX;
      ASTCDAttribute symbolAttribute = this.cdAttributeFactory.createAttribute(PROTECTED, symbolType, attributeName);
      decoratedClass.addCDAttribute(symbolAttribute);
      decoratedClass.addAllCDMethods(new MethodDecorator(this.glex).decorate(symbolAttribute));
    }
    return decoratedClass;
  }

  private boolean isSymbolClass(ASTCDClass ast) {
    if (ast.isPresentModifier() && ast.getModifier().isPresentStereotype()) {
      return ast.getModifier().getStereotype().getValueList().stream().anyMatch(v -> v.getName().equals(MC2CDStereotypes.SYMBOL.toString()));
    }
    return false;
  }
}