package de.monticore.codegen.cd2java._symboltable.symbol.symbolReferenceMethodDecorator.mutator;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.methods.mutator.ListMutatorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;

public class SymbolReferenceListMutatorDecorator extends ListMutatorDecorator {

  public SymbolReferenceListMutatorDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  protected static final String SET_METHOD_NAME = "set%sList";

  @Override
  protected ASTCDMethod createSetListMethod(ASTCDAttribute ast) {
    String signature = String.format(SET_LIST, capitalizedAttributeNameWithOutS, attributeType, ast.getName());
    String methodName = String.format(SET_METHOD_NAME, capitalizedAttributeNameWithOutS);
    ASTCDMethod getList = this.getCDMethodFacade().createMethodByDefinition(signature);
    this.replaceTemplate(EMPTY_BODY, getList, new TemplateHookPoint("_symboltable.symbolreferece.methods.Set",
        methodName, ast.getName()));
    return getList;
  }
}
