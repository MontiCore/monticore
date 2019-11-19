package de.monticore.codegen.cd2java._symboltable.symbol;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.cd.facade.CDModifier.*;

/**
 * creates a SymbolResolvingDelegate interface from a grammar
 */
public class SymbolResolvingDelegateInterfaceDecorator extends AbstractCreator<ASTCDType, ASTCDInterface> {

  protected final SymbolTableService symbolTableService;

  public SymbolResolvingDelegateInterfaceDecorator(final GlobalExtensionManagement glex,
                                                   final SymbolTableService symbolTableService) {
    super(glex);
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDInterface decorate(ASTCDType input) {
    String symbolResolvingDelegateInterface = symbolTableService.getSymbolResolvingDelegateInterfaceSimpleName(input);
    String symbolFullName = symbolTableService.getSymbolFullName(input);
    String symbolSimpleName = symbolTableService.getSymbolSimpleName(input);


    return CD4AnalysisMill.cDInterfaceBuilder()
        .setName(symbolResolvingDelegateInterface)
        .setModifier(PUBLIC.build())
        .addCDMethod(createResolveAdaptedStateSymbol(symbolFullName, symbolSimpleName))
        .build();
  }

  protected ASTCDMethod createResolveAdaptedStateSymbol(String fullSymbolName, String simpleSymbolName) {
    ASTMCType listTypeOfSymbol = getMCTypeFacade().createListTypeOf(fullSymbolName);

    ASTCDParameter nameParameter = getCDParameterFacade().createParameter(String.class, NAME_VAR);
    ASTCDParameter accessModifierParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(ACCESS_MODIFIER), MODIFIER_VAR);
    ASTCDParameter foundSymbolsParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createBooleanType(), FOUND_SYMBOLS_VAR);
    ASTCDParameter predicateParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createBasicGenericTypeOf(PREDICATE, fullSymbolName), PREDICATE_VAR);

    return getCDMethodFacade().createMethod(PUBLIC_ABSTRACT, listTypeOfSymbol, String.format(RESOLVE_ADAPTED, simpleSymbolName),
        foundSymbolsParameter, nameParameter, accessModifierParameter, predicateParameter);
  }
}
