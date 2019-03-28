package de.monticore.codegen.cd2java.ast_new.reference.referencedDefinition.referencedDefinitionMethodDecorator;

import de.monticore.codegen.cd2java.methods.accessor.OptionalAccessorDecorator;
import de.monticore.codegen.cd2java.symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import org.apache.commons.lang3.StringUtils;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class ReferencedDefinitionOptAccessorDecorator extends OptionalAccessorDecorator {

  private final SymbolTableService symbolTableService;

  public ReferencedDefinitionOptAccessorDecorator(final GlobalExtensionManagement glex,
      final SymbolTableService symbolTableService) {
    super(glex);
    this.symbolTableService = symbolTableService;
  }

  @Override
  protected ASTCDMethod createGetOptMethod(final ASTCDAttribute ast) {
    String name = String.format(GET_OPT, StringUtils.capitalize(ast.getName()));
    ASTType type = ast.getType().deepClone();
    ASTCDMethod method = this.getCDMethodFactory().createMethod(PUBLIC, type, name);
    String referencedSymbolType = symbolTableService.getReferencedSymbolTypeName(ast);
    String simpleName = symbolTableService.getSimpleSymbolName(referencedSymbolType);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("ast_new.refSymbolMethods.GetDefinitionOpt",
        ast.getName(),referencedSymbolType, simpleName));
    return method;
  }
}
