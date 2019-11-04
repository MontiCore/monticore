package de.monticore.codegen.cd2java._symboltable.symbol.symbolReferenceMethodDecorator.mutator;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.methods.mutator.OptionalMutatorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

/**
 * changes implementation of the setOpt method
 */
public class SymbolReferenceOptMutatorDecorator extends OptionalMutatorDecorator {

  public SymbolReferenceOptMutatorDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  protected ASTCDMethod createSetOptMethod(final ASTCDAttribute ast) {
    String methodName = String.format(SET_OPT, naiveAttributeName);
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC, methodName, this.getCDParameterFacade().createParameters(ast));
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(
        "_symboltable.symbolreferece.methods.Set", methodName, ast.getName()));
    return method;
  }
}
