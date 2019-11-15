/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_class.reference.definition.methoddecorator;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java._ast.ast_class.reference.definition.ASTReferencedDefinitionDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.accessor.OptionalAccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import org.apache.commons.lang3.StringUtils;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.*;

/**
 * creates all optional getter methods for the referencedDefinition AST
 */

public class ReferencedDefinitionOptAccessorDecorator extends OptionalAccessorDecorator {

  protected final SymbolTableService symbolTableService;

  public ReferencedDefinitionOptAccessorDecorator(final GlobalExtensionManagement glex,
                                                  final SymbolTableService symbolTableService) {
    super(glex);
    this.symbolTableService = symbolTableService;
  }

  /**
   * is overwritten because the attributeName has the suffix 'Definition'
   */
  @Override
  protected String getNaiveAttributeName(ASTCDAttribute astcdAttribute) {
    return StringUtils.capitalize(DecorationHelper.getNativeAttributeName(astcdAttribute.getName())) + ASTReferencedDefinitionDecorator.DEFINITION;
  }

  /**
   * overwrite only the getOpt method implementation, because the other methods are delegated to this one
   */
  @Override
  protected ASTCDMethod createGetOptMethod(final ASTCDAttribute ast) {
    String name = String.format(GET_OPT, StringUtils.capitalize(naiveAttributeName));
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC, ast.getMCType().deepClone(), name);
    String referencedSymbolType = symbolTableService.getReferencedSymbolTypeName(ast);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_ast.ast_class.refSymbolMethods.GetDefinitionOpt",
        ast.getName(), referencedSymbolType));
    return method;
  }
}
