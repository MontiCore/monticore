package de.monticore.codegen.cd2java._ast.ast_class.reference.referencedSymbol.referenedSymbolMethodDecorator;

import de.monticore.codegen.cd2java._ast.ast_class.reference.referencedSymbol.ASTReferencedSymbolDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.accessor.OptionalAccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
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
    ASTType type = ast.getType().deepClone();
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC, type, name);
    //create correct Name A for resolveA method
    String simpleSymbolName = symbolTableService.getSimpleSymbolName(symbolTableService.getReferencedSymbolTypeName(ast));
    //create scopeInterface for cast od enclosingScope
    String scopeInterfaceTypeName = symbolTableService.getScopeInterfaceTypeName();
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("ast_new.refSymbolMethods.GetSymbolOpt",
        ast.getName(), simpleSymbolName, scopeInterfaceTypeName, isOptionalAttribute(ast)));
    return method;
  }

  private boolean isOptionalAttribute(final ASTCDAttribute clazz) {
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
