package de.monticore.codegen.cd2java.ast_new;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.methods.ListMethodDecoratorStrategy;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.codegen.cd2java.methods.MethodDecoratorStrategy;
import de.monticore.codegen.cd2java.methods.OptionalMethodDecoratorStrategy;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;

public class RefDefinitionMethodDecorator extends MethodDecorator {

  private String refSymbolType;
  private String refSymbolSimpleName;

  public RefDefinitionMethodDecorator(GlobalExtensionManagement glex, String refSymbolType, String refSymbolSimpleName) {
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
    return new RefDefinitionOptionalMethodDecoratorStrategy(this.getGlex(), refSymbolType, refSymbolSimpleName);
  }

  @Override
  protected ListMethodDecoratorStrategy createListMethodDecoratorStrategy() {
    return new RefDefinitionListMethodDecoratorStrategy(this.getGlex(), this.createMandatoryMethodDecoratorStrategy(), refSymbolType, refSymbolSimpleName);
  }
}
