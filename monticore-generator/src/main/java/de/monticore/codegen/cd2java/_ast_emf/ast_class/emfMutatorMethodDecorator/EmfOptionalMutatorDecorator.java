package de.monticore.codegen.cd2java._ast_emf.ast_class.emfMutatorMethodDecorator;

import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java.methods.mutator.OptionalMutatorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast_emf.EmfConstants.PACKAGE_SUFFIX;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class EmfOptionalMutatorDecorator extends OptionalMutatorDecorator {
  private final ASTService astService;

  private String className;

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


  @Override
  protected ASTCDMethod createSetOptMethod(final ASTCDAttribute attribute) {
    String packageName = astService.getCDName() + PACKAGE_SUFFIX;
    String methodName = String.format(SET_OPT, naiveAttributeName);
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC, methodName, this.getCDParameterFacade().createParameters(attribute));
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_ast_emf.ast_class.Set",
        packageName, getClassName(), attribute));
    return method;
  }
}
