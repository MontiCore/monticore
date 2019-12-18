/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast_emf.ast_class.mutatordecorator;

import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java.methods.mutator.OptionalMutatorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

public class EmfOptionalMutatorDecorator extends OptionalMutatorDecorator {
  protected final ASTService astService;

  protected String className;

  public EmfOptionalMutatorDecorator(GlobalExtensionManagement glex, ASTService astService) {
    super(glex);
    this.astService = astService;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }
}
