package de.monticore.codegen.cd2java._symboltable.symbol.symbolReferenceMethodDecorator.accessor;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.methods.accessor.ListAccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;

/**
 * changes implementation of the getList method
 */
public class SymbolReferenceListAccessorDecorator extends ListAccessorDecorator {

  public SymbolReferenceListAccessorDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  protected static final String GET_METHOD_NAME = "get%sList";

  @Override
  protected ASTCDMethod createGetListMethod(ASTCDAttribute ast) {
    String signature = String.format(GET_LIST, attributeType, capitalizedAttributeNameWithOutS);
    String methodName = String.format(GET_METHOD_NAME, capitalizedAttributeNameWithOutS);
    ASTCDMethod getList = this.getCDMethodFacade().createMethodByDefinition(signature);
    this.replaceTemplate(EMPTY_BODY, getList, new TemplateHookPoint(
        "_symboltable.symbolreferece.methods.Get", methodName));
    return getList;
  }
}
