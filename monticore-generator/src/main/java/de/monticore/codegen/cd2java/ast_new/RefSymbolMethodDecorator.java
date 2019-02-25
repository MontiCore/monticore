package de.monticore.codegen.cd2java.ast_new;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.methods.ListMethodDecoratorStrategy;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.codegen.cd2java.methods.MethodDecoratorStrategy;
import de.monticore.codegen.cd2java.methods.OptionalMethodDecoratorStrategy;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;

public class RefSymbolMethodDecorator extends MethodDecorator {

  private ASTType refSymbolType;
  private String refSymbolSimpleName;

  public RefSymbolMethodDecorator(GlobalExtensionManagement glex, ASTType refSymbolType, String refSymbolSimpleName) {
    super(glex);
    this.refSymbolType = refSymbolType;
    this.refSymbolSimpleName = refSymbolSimpleName;
  }

  @Override
  protected MethodDecoratorStrategy determineMethodGeneratorStrategy(final ASTCDAttribute ast) {
    //TODO: helper durch OO-Ansatz ersetzen (und vereinheitlichen)
    if (GeneratorHelper.isListType(ast.printType())) {
      return createListMethodDecoratorStrategy();
    } else {
      return createOptionalMethodDecoratorStrategy();
    }
  }

  @Override
  protected OptionalMethodDecoratorStrategy createOptionalMethodDecoratorStrategy() {
    return new RefSymbolOptionalMethodDecoratorStrategy(this.getGlex(), refSymbolType);
  }

  @Override
  protected ListMethodDecoratorStrategy createListMethodDecoratorStrategy() {
    return new RefSymbolListMethodDecoratorStrategy(this.getGlex(), this.createMandatoryMethodDecoratorStrategy(), refSymbolType, refSymbolSimpleName);
  }
}
