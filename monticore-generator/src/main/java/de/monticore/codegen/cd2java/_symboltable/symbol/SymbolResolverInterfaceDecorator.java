/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.symbol;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cdbasis._ast.*;
import de.monticore.cd4codebasis._ast.*;
import de.monticore.cdinterfaceandenum._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.umlmodifier._ast.ASTModifier;

import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.codegen.cd2java.CDModifier.*;

/**
 * creates a SymbolResolver interface from a grammar
 */
public class SymbolResolverInterfaceDecorator extends AbstractCreator<ASTCDType, ASTCDInterface> {

  protected final SymbolTableService symbolTableService;

  public SymbolResolverInterfaceDecorator(final GlobalExtensionManagement glex,
                                                   final SymbolTableService symbolTableService) {
    super(glex);
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDInterface decorate(ASTCDType input) {
    String symbolResolverInterface = symbolTableService.getSymbolResolverInterfaceSimpleName(input);
    String symbolFullName = symbolTableService.getSymbolFullName(input);
    String symbolSimpleName = symbolTableService.getSymbolSimpleName(input);
    ASTModifier modifier = input.isPresentModifier() ?
        symbolTableService.createModifierPublicModifier(input.getModifier()):
        PUBLIC.build();

    return CD4AnalysisMill.cDInterfaceBuilder()
        .setName(symbolResolverInterface)
        .setModifier(modifier)
        .addCDMember(createResolveAdaptedStateSymbol(symbolFullName, symbolSimpleName))
        .build();
  }

  protected ASTCDMethod createResolveAdaptedStateSymbol(String fullSymbolName, String simpleSymbolName) {
    ASTMCType listTypeOfSymbol = getMCTypeFacade().createListTypeOf(fullSymbolName);

    ASTCDParameter nameParameter = getCDParameterFacade().createParameter(String.class, NAME_VAR);
    ASTCDParameter accessModifierParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(ACCESS_MODIFIER), MODIFIER_VAR);
    ASTCDParameter foundSymbolsParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createBooleanType(), FOUND_SYMBOLS_VAR);
    ASTCDParameter predicateParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createBasicGenericTypeOf(PREDICATE, fullSymbolName), PREDICATE_VAR);

    return getCDMethodFacade().createMethod(PUBLIC_ABSTRACT.build(), listTypeOfSymbol, String.format(RESOLVE_ADAPTED, simpleSymbolName),
        foundSymbolsParameter, nameParameter, accessModifierParameter, predicateParameter);
  }
}
