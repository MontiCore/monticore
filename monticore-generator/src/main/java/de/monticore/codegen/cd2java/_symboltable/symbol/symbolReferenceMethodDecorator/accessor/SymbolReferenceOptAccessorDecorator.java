package de.monticore.codegen.cd2java._symboltable.symbol.symbolReferenceMethodDecorator.accessor;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.methods.accessor.OptionalAccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class SymbolReferenceOptAccessorDecorator extends OptionalAccessorDecorator {
  public SymbolReferenceOptAccessorDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  protected ASTCDMethod createGetOptMethod(final ASTCDAttribute ast) {
    String methodName = String.format(GET_OPT, naiveAttributeName);
    ASTMCType type = ast.getMCType().deepClone();
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC, type, methodName);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(
        "_symboltable.symbolreferece.methods.Get", methodName));
    return method;
  }

}
