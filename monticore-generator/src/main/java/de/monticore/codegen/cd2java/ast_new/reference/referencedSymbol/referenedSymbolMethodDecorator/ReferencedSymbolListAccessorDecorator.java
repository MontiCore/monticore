package de.monticore.codegen.cd2java.ast_new.reference.referencedSymbol.referenedSymbolMethodDecorator;

import de.monticore.codegen.cd2java.methods.accessor.ListAccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;

public class ReferencedSymbolListAccessorDecorator extends ListAccessorDecorator {

  public ReferencedSymbolListAccessorDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  protected ASTCDMethod createGetListMethod(ASTCDAttribute ast) {
    String signature = String.format(GET_LIST, attributeType, capitalizedAttributeName);
    ASTCDMethod getList = this.getCDMethodFactory().createMethodByDefinition(signature);
    String referencedSymbolTypeAsList = ast.printType();
    String referencedSymbolType = referencedSymbolTypeAsList.substring(5, referencedSymbolTypeAsList.length()-1);
    this.replaceTemplate(EMPTY_BODY, getList, new TemplateHookPoint("ast_new.refSymbolMethods.GetSymbolList", ast.getName(), referencedSymbolType));
    return getList;
  }

}
