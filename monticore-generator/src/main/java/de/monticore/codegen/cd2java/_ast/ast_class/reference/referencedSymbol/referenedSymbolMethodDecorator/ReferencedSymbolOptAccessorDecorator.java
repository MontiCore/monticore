/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_class.reference.referencedSymbol.referenedSymbolMethodDecorator;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java._ast.ast_class.reference.referencedSymbol.ASTReferencedSymbolDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.accessor.OptionalAccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import org.apache.commons.lang3.StringUtils;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class ReferencedSymbolOptAccessorDecorator extends OptionalAccessorDecorator {

  protected final SymbolTableService symbolTableService;


  public ReferencedSymbolOptAccessorDecorator(final GlobalExtensionManagement glex, final SymbolTableService symbolTableService) {
    super(glex);
    this.symbolTableService = symbolTableService;
  }

  @Override
  protected ASTCDMethod createGetOptMethod(final ASTCDAttribute ast) {
    String name = String.format(GET_OPT, StringUtils.capitalize(ast.getName()));
    ASTMCType type = ast.getMCType().deepClone();
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC, type, name);
    //create correct Name A for resolveA method
    String simpleSymbolName = symbolTableService.getSimpleNameFromSymbolName(symbolTableService.getReferencedSymbolTypeName(ast));
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_ast.ast_class.refSymbolMethods.GetSymbolOpt",
        ast.getName(), simpleSymbolName, isOptionalAttribute(ast)));
    return method;
  }

  protected boolean isOptionalAttribute(final ASTCDAttribute clazz) {
    //have to ask here if the original attribute was an optional or mandatory String attribute
    //the template has to be different
    if (clazz.isPresentModifier() && clazz.getModifier().isPresentStereotype()) {
      return clazz.getModifier().getStereotype().getValueList()
          .stream()
          .anyMatch(v -> v.getName().equals(ASTReferencedSymbolDecorator.IS_OPTIONAL));
    }
    return false;
  }

}
