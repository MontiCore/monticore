package de.monticore.codegen.cd2java._ast.ast_class.reference.referencedDefinition.referencedDefinitionMethodDecorator;

import de.monticore.codegen.cd2java._ast.ast_class.reference.referencedDefinition.ASTReferencedDefinitionDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.accessor.ListAccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import org.apache.commons.lang3.StringUtils;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;

public class ReferencedDefinitionListAccessorDecorator extends ListAccessorDecorator {

  private final SymbolTableService symbolTableService;

  public ReferencedDefinitionListAccessorDecorator(final GlobalExtensionManagement glex,
                                                   final SymbolTableService symbolTableService) {
    super(glex);
    this.symbolTableService = symbolTableService;
  }

  @Override
  public String getCapitalizedAttributeNameWithS(ASTCDAttribute attribute) {
    return StringUtils.capitalize(DecorationHelper.getNativeAttributeName(attribute.getName())) + ASTReferencedDefinitionDecorator.DEFINITION;
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
    String referencedNodeTypeAsList = ast.printType();
    String referencedNodeType = referencedNodeTypeAsList.substring(5, referencedNodeTypeAsList.length() - 1);
    this.replaceTemplate(EMPTY_BODY, getList, new TemplateHookPoint("ast_new.refSymbolMethods.GetDefinitionList", ast.getName(),
        referencedSymbolType, referencedNodeType));
    return getList;
  }
}
