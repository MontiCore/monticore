package de.monticore.codegen.cd2java._ast_emf.ast_class.emfMutatorMethodDecorator;

import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java.methods.mutator.OptionalMutatorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast_emf.EmfConstants.PACKAGE_SUFFIX;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class EmfOptionalMutatorDecorator extends OptionalMutatorDecorator {
  private final ASTService astService;

  private final ASTCDClass astcdClass;

  public EmfOptionalMutatorDecorator(GlobalExtensionManagement glex, ASTService astService, ASTCDClass astcdClass) {
    super(glex);
    this.astService = astService;
    this.astcdClass = astcdClass;
  }
  @Override
  protected ASTCDMethod createSetOptMethod(final ASTCDAttribute attribute) {
    String packageName = astService.getCDName() + PACKAGE_SUFFIX;
    String className = astcdClass.getName();
    String name = String.format(SET_OPT, naiveAttributeName);
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC, name, this.getCDParameterFacade().createParameters(attribute));
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_ast_emf.ast_class.EmfSet",
        packageName, className, attribute));
    return method;
  }
}
