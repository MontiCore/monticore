/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast_emf.ast_class.mutatordecorator;

import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java.methods.MutatorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

public class EmfMutatorDecorator extends MutatorDecorator {

  public EmfMutatorDecorator(GlobalExtensionManagement glex, ASTService astService) {
    super(glex, new EmfMandatoryMutatorDecorator(glex, astService),
        new EmfOptionalMutatorDecorator(glex, astService),
        new EmfListMutatorDecorator(glex, astService));
  }

  public void setClassName(String className) {
    ((EmfMandatoryMutatorDecorator) mandatoryMethodDecorator).setClassName(className);
    ((EmfListMutatorDecorator) listMethodDecorator).setClassName(className);
    ((EmfOptionalMutatorDecorator) optionalMethodDecorator).setClassName(className);
  }
}
