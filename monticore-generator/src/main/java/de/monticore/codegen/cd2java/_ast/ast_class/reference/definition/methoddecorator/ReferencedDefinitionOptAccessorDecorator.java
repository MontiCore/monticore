/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_class.reference.definition.methoddecorator;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.codegen.cd2java._ast.ast_class.reference.definition.ASTReferencedDefinitionDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.accessor.OptionalAccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import org.apache.commons.lang3.StringUtils;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

/**
 * creates all optional getter methods for the referencedDefinition AST
 */

public class ReferencedDefinitionOptAccessorDecorator extends OptionalAccessorDecorator {

  protected final SymbolTableService symbolTableService;

  public ReferencedDefinitionOptAccessorDecorator(final GlobalExtensionManagement glex,
                                                  final SymbolTableService symbolTableService) {
    super(glex, symbolTableService);
    this.symbolTableService = symbolTableService;
  }

  /**
   * is overwritten because the attributeName has the suffix 'Definition'
   */
  @Override
  protected String getNaiveAttributeName(ASTCDAttribute astcdAttribute) {
    return StringUtils.capitalize(getDecorationHelper().getNativeAttributeName(astcdAttribute.getName())) + ASTReferencedDefinitionDecorator.DEFINITION;
  }
  
  /**
   * Overwrite the get method implementation as it requires additional logic
   * then a normal getter.
   */
  @Override
  protected ASTCDMethod createGetMethod(final ASTCDAttribute ast) {
    String name = String.format(GET, StringUtils.capitalize(naiveAttributeName));
    ASTMCType type = getDecorationHelper().getReferenceTypeFromOptional(ast.getMCType().deepClone()).getMCTypeOpt().get();
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC.build(), type, name);
    String referencedSymbolType = symbolTableService.getReferencedSymbolTypeName(ast);
    String attributeName = this.getDecorationHelper().getNativeAttributeName(ast.getName());
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_ast.ast_class.refSymbolMethods.GetDefinition", attributeName, referencedSymbolType));
    return method;
  }
  
  /**
   * Overwrite the isPresent method implementation as it requires additional
   * logic then the normal method.
   */
  @Override
  protected ASTCDMethod createIsPresentMethod(final ASTCDAttribute ast) {
    String name = String.format(IS_PRESENT, StringUtils.capitalize(naiveAttributeName));
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC.build(), getMCTypeFacade().createBooleanType(), name);
    String attributeName = this.getDecorationHelper().getNativeAttributeName(ast.getName());
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_ast.ast_class.refSymbolMethods.IsPresentDefinition", attributeName));
    return method;
  }
}
