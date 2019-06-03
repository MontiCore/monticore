package de.monticore.codegen.cd2java._ast.ast_class.reference.referencedSymbol.referenedSymbolMethodDecorator;

import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.accessor.ListAccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;

public class ReferencedSymbolListAccessorDecorator extends ListAccessorDecorator {

  protected final SymbolTableService symbolTableService;

  public ReferencedSymbolListAccessorDecorator(final GlobalExtensionManagement glex,
                                               final SymbolTableService symbolTableService) {
    super(glex);
    this.symbolTableService = symbolTableService;
  }

  @Override
  public String getAttributeType(ASTCDAttribute attribute) {
    return "Optional<" + getTypeArgumentFromListType(attribute.getType()) + ">";
  }

  @Override
  protected ASTCDMethod createGetListMethod(ASTCDAttribute ast) {
    String signature = String.format(GET_LIST, attributeType, capitalizedAttributeNameWithS);
    ASTCDMethod getList = this.getCDMethodFacade().createMethodByDefinition(signature);
    String referencedSymbolType = symbolTableService.getReferencedSymbolTypeName(ast);
    String simpleSymbolName = symbolTableService.getSimpleSymbolName(referencedSymbolType);
    String scopeInterfaceTypeName = symbolTableService.getScopeInterfaceTypeName();
    this.replaceTemplate(EMPTY_BODY, getList, new TemplateHookPoint("_ast.ast_class.refSymbolMethods.GetSymbolList",
        ast.getName(), referencedSymbolType, simpleSymbolName, scopeInterfaceTypeName));
    return getList;
  }

}
