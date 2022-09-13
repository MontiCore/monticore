/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_class.reference.definition.methoddecorator;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java._ast.ast_class.reference.definition.ASTReferencedDefinitionDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.accessor.ListAccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import org.apache.commons.lang3.StringUtils;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

/**
 * created all list getter methods for the referencedDefinition AST
 */

public class ReferencedDefinitionListAccessorDecorator extends ListAccessorDecorator {

  protected final SymbolTableService symbolTableService;

  public ReferencedDefinitionListAccessorDecorator(final GlobalExtensionManagement glex,
                                                   final SymbolTableService symbolTableService) {
    super(glex);
    this.symbolTableService = symbolTableService;
  }

  /**
   * has to overwrite this method to add the 'Definition' suffix
   */
  @Override
  public String getCapitalizedAttributeNameWithS(ASTCDAttribute attribute) {
    return StringUtils.capitalize(getDecorationHelper().getNativeAttributeName(attribute.getName())) + ASTReferencedDefinitionDecorator.DEFINITION;
  }

  /**
   * overwrite attributetype because List<Optional<ASTX>> has to be created
   */
  @Override
  public String getAttributeType(ASTCDAttribute attribute) {
    return "Optional<" + getTypeArgumentFromListType(attribute.getMCType()) + ">";
  }

  /**
   * overwrite only the getList method implementation, because the other methods are delegated to this one
   */
  @Override
  protected ASTCDMethod createGetListMethod(ASTCDAttribute ast) {
    String signature = String.format(GET_LIST, attributeType, capitalizedAttributeNameWithS);
    ASTCDMethod getList = this.getCDMethodFacade().createMethodByDefinition(signature);
    String referencedSymbolType = symbolTableService.getReferencedSymbolTypeName(ast);
    String referencedNodeTypeAsList = ast.printType();
    String referencedNodeType = referencedNodeTypeAsList.substring(5, referencedNodeTypeAsList.length() - 1);
    String attributeName = this.getDecorationHelper().getNativeAttributeName(ast.getName());
    this.replaceTemplate(EMPTY_BODY, getList, new TemplateHookPoint("_ast.ast_class.refSymbolMethods.GetDefinitionList", attributeName,
        referencedSymbolType, referencedNodeType));
    return getList;
  }
}
