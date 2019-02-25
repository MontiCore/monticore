package de.monticore.codegen.cd2java.ast_new;

import de.monticore.codegen.cd2java.methods.ListMethodDecoratorStrategy;
import de.monticore.codegen.cd2java.methods.MandatoryMethodDecoratorStrategy;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;

public class RefDefinitionListMethodDecoratorStrategy extends ListMethodDecoratorStrategy {

  private ASTType refSymbolType;
  private String refSymbolSimpleName;

  protected RefDefinitionListMethodDecoratorStrategy(GlobalExtensionManagement glex, MandatoryMethodDecoratorStrategy mandatoryMethodDecoratorStrategy, ASTType refSymbolType, String refSymbolSimpleName) {
    super(glex, mandatoryMethodDecoratorStrategy);
    this.refSymbolType = refSymbolType;
    this.refSymbolSimpleName = refSymbolSimpleName;
  }

}
