package de.monticore.codegen.cd2java._ast_emf.ast_class.emfMutatorMethodDecorator;

import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java.methods.MutatorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;

public class EmfMutatorDecorator extends MutatorDecorator {

  public EmfMutatorDecorator(GlobalExtensionManagement glex, ASTService astService, ASTCDClass astcdClass) {
    super(glex, new EmfMandatoryMutatorDecorator(glex, astService, astcdClass),
        new EmfOptionalMutatorDecorator(glex, astService, astcdClass),
        new EmfListMutatorDecorator(glex, astService, astcdClass));
  }
}
