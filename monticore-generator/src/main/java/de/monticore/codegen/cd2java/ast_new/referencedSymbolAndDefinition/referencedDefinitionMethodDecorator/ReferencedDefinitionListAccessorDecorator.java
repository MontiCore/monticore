package de.monticore.codegen.cd2java.ast_new.referencedSymbolAndDefinition.referencedDefinitionMethodDecorator;

import de.monticore.codegen.cd2java.ast_new.referencedSymbolAndDefinition.ReferencedSymbolUtil;
import de.monticore.codegen.cd2java.methods.accessor.ListAccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;

public class ReferencedDefinitionListAccessorDecorator extends ListAccessorDecorator {
  public ReferencedDefinitionListAccessorDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  protected ASTCDMethod createGetListMethod(ASTCDAttribute ast) {
    String signature = String.format(GET_LIST, attributeType, capitalizedAttributeName);
    ASTCDMethod getList = this.getCDMethodFactory().createMethodByDefinition(signature);
    String referencedSymbolType =ReferencedSymbolUtil.getReferencedSymbolTypeName(ast);
    String referencedNodeTypeAsList = ast.printType();
    String referencedNodeType = referencedNodeTypeAsList.substring(5, referencedNodeTypeAsList.length()-1);
    String simpleName = ReferencedSymbolUtil.getSimpleSymbolName(referencedSymbolType);
    this.replaceTemplate(EMPTY_BODY, getList, new TemplateHookPoint("ast_new.refSymbolMethods.GetDefinitionList", ast.getName(),
        referencedSymbolType, simpleName, referencedNodeType));
    return getList;
  }
}
