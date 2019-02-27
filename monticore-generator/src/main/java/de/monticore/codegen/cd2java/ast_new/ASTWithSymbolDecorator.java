package de.monticore.codegen.cd2java.ast_new;

import de.monticore.codegen.cd2java.Decorator;
import de.monticore.codegen.cd2java.factories.*;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.symboltable.SymbolTableGeneratorHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.se_rwth.commons.Names;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static de.se_rwth.commons.Names.getQualifier;

public class ASTWithSymbolDecorator implements Decorator<ASTCDClass, ASTCDClass> {
  private final GlobalExtensionManagement glex;

  private final CDTypeFactory cdTypeFactory;

  private final CDAttributeFactory cdAttributeFactory;

  private final ASTCDCompilationUnit compilationUnit;

  private static final String SYMBOL_PREFIX = "Symbol";

  private static final String SCOPE_PREFIX = "Scope";


  public ASTWithSymbolDecorator(GlobalExtensionManagement glex, ASTCDCompilationUnit compilationUnit) {
    this.glex = glex;
    this.compilationUnit = compilationUnit;
    this.cdTypeFactory = CDTypeFactory.getInstance();
    this.cdAttributeFactory = CDAttributeFactory.getInstance();
  }

  @Override
  public ASTCDClass decorate(ASTCDClass ast) {
    ASTDecorator astDecorator = new ASTDecorator(glex, compilationUnit);

    ast = astDecorator.decorate(ast);
    if (isSymbolClass(ast)) {
      String symbolName = getSymbolName(ast);
      String attributeName = StringUtils.uncapitalize(ast.getName().replaceFirst("AST", "")) + SYMBOL_PREFIX;
      final ASTCDAttribute symbolAttribute = addAttribute(symbolName, attributeName);
      ast.addCDAttribute(symbolAttribute);
      List<ASTCDMethod> methods = addMethod(symbolAttribute);
      ast.addAllCDMethods(methods);
    }
    if (isScopeClass(ast)) {
      String scopeName = getScopeName();
      ASTCDAttribute scopeAttribute = addAttribute(scopeName, StringUtils.uncapitalize(compilationUnit.getCDDefinition().getName())+SCOPE_PREFIX);
      ast.addCDAttribute(scopeAttribute);
      List<ASTCDMethod> methods = addMethod(scopeAttribute);
      ast.addAllCDMethods(methods);
    }
    return ast;
  }

  private ASTCDAttribute addAttribute(String typeName, String attrName){
    ASTType optSymbolType = cdTypeFactory.createTypeByDefinition("Optional<" + typeName + ">");
    return cdAttributeFactory.createAttribute(CDModifier.PROTECTED, optSymbolType, attrName);
  }

  private List<ASTCDMethod> addMethod(ASTCDAttribute attribute){
    MethodDecorator methodDecorator = new MethodDecorator(glex);
    return methodDecorator.decorate(attribute);
  }

  private boolean isSymbolClass(ASTCDClass ast) {
    return hasStereotype(ast, MC2CDStereotypes.SYMBOL.toString());
  }

  private boolean isScopeClass(ASTCDClass ast) {
    return hasStereotype(ast, MC2CDStereotypes.SCOPE.toString());
  }

  private boolean hasStereotype(ASTCDClass ast, String stereotype) {
    if (ast.isPresentModifier() && ast.getModifier().isPresentStereotype()) {
      return ast.getModifier().getStereotype().getValueList().stream().anyMatch((v) -> {
        return v.getName().equals(stereotype);
      });
    }
    return false;
  }

  private String getSymbolName(ASTCDClass astcdClass) {
    return getQualifiedName(astcdClass.getName().replaceFirst("AST", "") + SYMBOL_PREFIX);
  }

  private String getScopeName() {
    return getQualifiedName(compilationUnit.getCDDefinition().getName().replaceFirst("AST", "") + SCOPE_PREFIX);
  }
  
  private String getQualifiedName( String name){
    if (!getQualifier(name).isEmpty()) {
      name = SymbolTableGeneratorHelper
          .getQualifiedSymbolType(getQualifier(name)
              .toLowerCase(), Names.getSimpleName(name));
    }
    return name;
  }
}
